package vallegrande.edu.pe.misistema.model;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class VentaDAO {

    public List<Venta> listar() {
        List<Venta> ventas = new ArrayList<>();

        String sql = """
                SELECT id, fecha_venta, nombre_cliente,
                       documento_cliente, metodo_pago, total
                FROM venta
                ORDER BY fecha_venta DESC
                """;

        try (Connection conn = Conexion.conectar();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                ventas.add(mapearVenta(rs));
            }

        } catch (SQLException e) {
            System.err.println("Error al listar ventas: " + e.getMessage());
        }

        return ventas;
    }

    public Venta buscarPorId(long id) {
        String sql = """
                SELECT id, fecha_venta, nombre_cliente,
                       documento_cliente, metodo_pago, total
                FROM venta
                WHERE id = ?
                """;

        try (Connection conn = Conexion.conectar();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, id);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    Venta venta = mapearVenta(rs);
                    venta.setDetalles(listarDetalles(conn, id));
                    return venta;
                }
            }

        } catch (SQLException e) {
            System.err.println("Error al buscar venta: " + e.getMessage());
        }

        return null;
    }

    public boolean insertar(Venta venta) {
        String sql = """
                INSERT INTO venta (
                    nombre_cliente, documento_cliente, metodo_pago, total
                )
                VALUES (?, ?, ?, 0)
                """;

        try (Connection conn = Conexion.conectar()) {
            conn.setAutoCommit(false);

            try {
                long idVenta;

                try (PreparedStatement stmt = conn.prepareStatement(
                        sql,
                        PreparedStatement.RETURN_GENERATED_KEYS
                )) {
                    stmt.setString(1, venta.getNombreCliente());
                    stmt.setString(2, venta.getDocumentoCliente());
                    stmt.setString(3, venta.getMetodoPago());
                    stmt.executeUpdate();

                    try (ResultSet keys = stmt.getGeneratedKeys()) {
                        if (!keys.next()) {
                            throw new SQLException(
                                    "MySQL no devolvió el ID de la venta."
                            );
                        }
                        idVenta = keys.getLong(1);
                    }
                }

                BigDecimal total = insertarDetalles(
                        conn,
                        idVenta,
                        venta.getDetalles()
                );

                actualizarTotal(conn, idVenta, total);
                conn.commit();

                venta.setId(idVenta);
                venta.setTotal(total);
                return true;

            } catch (SQLException | RuntimeException e) {
                conn.rollback();
                System.err.println(
                        "Error al registrar venta: " + e.getMessage()
                );
                return false;
            }

        } catch (SQLException e) {
            System.err.println(
                    "Error de conexión al registrar venta: " + e.getMessage()
            );
            return false;
        }
    }

    public boolean actualizar(Venta venta) {
        String comprobarSql = "SELECT id FROM venta WHERE id = ? FOR UPDATE";
        String actualizarSql = """
                UPDATE venta
                SET nombre_cliente = ?,
                    documento_cliente = ?,
                    metodo_pago = ?
                WHERE id = ?
                """;
        String borrarDetallesSql =
                "DELETE FROM detalle_venta WHERE venta_id = ?";

        try (Connection conn = Conexion.conectar()) {
            conn.setAutoCommit(false);

            try {
                try (PreparedStatement comprobar =
                             conn.prepareStatement(comprobarSql)) {
                    comprobar.setLong(1, venta.getId());

                    try (ResultSet rs = comprobar.executeQuery()) {
                        if (!rs.next()) {
                            throw new SQLException("La venta no existe.");
                        }
                    }
                }

                reponerStockDeDetallesExistentes(conn, venta.getId());

                try (PreparedStatement borrar =
                             conn.prepareStatement(borrarDetallesSql)) {
                    borrar.setLong(1, venta.getId());
                    borrar.executeUpdate();
                }

                BigDecimal total = insertarDetalles(
                        conn,
                        venta.getId(),
                        venta.getDetalles()
                );

                try (PreparedStatement actualizar =
                             conn.prepareStatement(actualizarSql)) {
                    actualizar.setString(1, venta.getNombreCliente());
                    actualizar.setString(2, venta.getDocumentoCliente());
                    actualizar.setString(3, venta.getMetodoPago());
                    actualizar.setLong(4, venta.getId());
                    actualizar.executeUpdate();
                }

                actualizarTotal(conn, venta.getId(), total);
                conn.commit();

                venta.setTotal(total);
                return true;

            } catch (SQLException | RuntimeException e) {
                conn.rollback();
                System.err.println(
                        "Error al actualizar venta: " + e.getMessage()
                );
                return false;
            }

        } catch (SQLException e) {
            System.err.println(
                    "Error de conexión al actualizar venta: " + e.getMessage()
            );
            return false;
        }
    }

    public boolean eliminar(long id) {
        String borrarDetallesSql =
                "DELETE FROM detalle_venta WHERE venta_id = ?";
        String borrarVentaSql = "DELETE FROM venta WHERE id = ?";

        try (Connection conn = Conexion.conectar()) {
            conn.setAutoCommit(false);

            try {
                reponerStockDeDetallesExistentes(conn, id);

                try (PreparedStatement borrarDetalles =
                             conn.prepareStatement(borrarDetallesSql)) {
                    borrarDetalles.setLong(1, id);
                    borrarDetalles.executeUpdate();
                }

                int filas;
                try (PreparedStatement borrarVenta =
                             conn.prepareStatement(borrarVentaSql)) {
                    borrarVenta.setLong(1, id);
                    filas = borrarVenta.executeUpdate();
                }

                if (filas == 0) {
                    throw new SQLException("La venta no existe.");
                }

                conn.commit();
                return true;

            } catch (SQLException | RuntimeException e) {
                conn.rollback();
                System.err.println(
                        "Error al eliminar venta: " + e.getMessage()
                );
                return false;
            }

        } catch (SQLException e) {
            System.err.println(
                    "Error de conexión al eliminar venta: " + e.getMessage()
            );
            return false;
        }
    }

    private BigDecimal insertarDetalles(
            Connection conn,
            long ventaId,
            List<DetalleVenta> detalles
    ) throws SQLException {
        if (detalles == null || detalles.isEmpty()) {
            throw new SQLException(
                    "La venta debe tener por lo menos un producto."
            );
        }

        String actualizarStockSql = """
                UPDATE producto
                SET stock = stock - ?
                WHERE id = ?
                """;

        String insertarDetalleSql = """
                INSERT INTO detalle_venta (
                    venta_id, producto_id, cantidad,
                    precio_unitario, subtotal
                )
                VALUES (?, ?, ?, ?, ?)
                """;

        BigDecimal total = BigDecimal.ZERO;

        for (DetalleVenta detalle : detalles) {
            if (detalle.getCantidad() <= 0) {
                throw new SQLException(
                        "La cantidad de cada producto debe ser mayor que cero."
                );
            }

            ProductoVenta producto =
                    bloquearProducto(conn, detalle.getProductoId());

            if (producto.stock() < detalle.getCantidad()) {
                throw new SQLException(
                        "No hay stock suficiente para: " + producto.nombre()
                );
            }

            BigDecimal subtotal = producto.precio()
                    .multiply(BigDecimal.valueOf(detalle.getCantidad()))
                    .setScale(2, RoundingMode.HALF_UP);

            try (PreparedStatement actualizarStock =
                         conn.prepareStatement(actualizarStockSql)) {
                actualizarStock.setInt(1, detalle.getCantidad());
                actualizarStock.setLong(2, detalle.getProductoId());
                actualizarStock.executeUpdate();
            }

            try (PreparedStatement insertarDetalle =
                         conn.prepareStatement(insertarDetalleSql)) {
                insertarDetalle.setLong(1, ventaId);
                insertarDetalle.setLong(2, detalle.getProductoId());
                insertarDetalle.setInt(3, detalle.getCantidad());
                insertarDetalle.setBigDecimal(4, producto.precio());
                insertarDetalle.setBigDecimal(5, subtotal);
                insertarDetalle.executeUpdate();
            }

            detalle.setVentaId(ventaId);
            detalle.setNombreProducto(producto.nombre());
            detalle.setPrecioUnitario(producto.precio());
            detalle.setSubtotal(subtotal);

            total = total.add(subtotal);
        }

        return total.setScale(2, RoundingMode.HALF_UP);
    }

    private ProductoVenta bloquearProducto(
            Connection conn,
            long productoId
    ) throws SQLException {
        String sql = """
                SELECT nombre, precio, stock, activo
                FROM producto
                WHERE id = ?
                FOR UPDATE
                """;

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, productoId);

            try (ResultSet rs = stmt.executeQuery()) {
                if (!rs.next()) {
                    throw new SQLException(
                            "No se encontró el producto con ID " + productoId
                    );
                }

                if (!rs.getBoolean("activo")) {
                    throw new SQLException(
                            "El producto está inactivo: " +
                                    rs.getString("nombre")
                    );
                }

                return new ProductoVenta(
                        rs.getString("nombre"),
                        rs.getBigDecimal("precio"),
                        rs.getInt("stock")
                );
            }
        }
    }

    private void reponerStockDeDetallesExistentes(
            Connection conn,
            long ventaId
    ) throws SQLException {
        String consultarSql = """
                SELECT producto_id, cantidad
                FROM detalle_venta
                WHERE venta_id = ?
                """;

        String reponerSql = """
                UPDATE producto
                SET stock = stock + ?
                WHERE id = ?
                """;

        try (PreparedStatement consultar =
                     conn.prepareStatement(consultarSql)) {
            consultar.setLong(1, ventaId);

            try (ResultSet rs = consultar.executeQuery()) {
                while (rs.next()) {
                    try (PreparedStatement reponer =
                                 conn.prepareStatement(reponerSql)) {
                        reponer.setInt(1, rs.getInt("cantidad"));
                        reponer.setLong(2, rs.getLong("producto_id"));
                        reponer.executeUpdate();
                    }
                }
            }
        }
    }

    private List<DetalleVenta> listarDetalles(
            Connection conn,
            long ventaId
    ) throws SQLException {
        List<DetalleVenta> detalles = new ArrayList<>();

        String sql = """
                SELECT dv.id, dv.venta_id, dv.producto_id,
                       p.nombre AS nombre_producto, dv.cantidad,
                       dv.precio_unitario, dv.subtotal
                FROM detalle_venta dv
                JOIN producto p ON p.id = dv.producto_id
                WHERE dv.venta_id = ?
                ORDER BY dv.id
                """;

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, ventaId);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    DetalleVenta detalle = new DetalleVenta();
                    detalle.setId(rs.getLong("id"));
                    detalle.setVentaId(rs.getLong("venta_id"));
                    detalle.setProductoId(rs.getLong("producto_id"));
                    detalle.setNombreProducto(
                            rs.getString("nombre_producto")
                    );
                    detalle.setCantidad(rs.getInt("cantidad"));
                    detalle.setPrecioUnitario(
                            rs.getBigDecimal("precio_unitario")
                    );
                    detalle.setSubtotal(rs.getBigDecimal("subtotal"));
                    detalles.add(detalle);
                }
            }
        }

        return detalles;
    }

    private void actualizarTotal(
            Connection conn,
            long ventaId,
            BigDecimal total
    ) throws SQLException {
        String sql = "UPDATE venta SET total = ? WHERE id = ?";

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setBigDecimal(1, total);
            stmt.setLong(2, ventaId);
            stmt.executeUpdate();
        }
    }

    private Venta mapearVenta(ResultSet rs) throws SQLException {
        Venta venta = new Venta();

        venta.setId(rs.getLong("id"));
        venta.setFechaVenta(rs.getTimestamp("fecha_venta"));
        venta.setNombreCliente(rs.getString("nombre_cliente"));
        venta.setDocumentoCliente(rs.getString("documento_cliente"));
        venta.setMetodoPago(rs.getString("metodo_pago"));
        venta.setTotal(rs.getBigDecimal("total"));

        return venta;
    }

    private record ProductoVenta(
            String nombre,
            BigDecimal precio,
            int stock
    ) {
    }
}