package vallegrande.edu.pe.misistema.model;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ProductoDAO {

    public List<Producto> listar() {
        List<Producto> productos = new ArrayList<>();

        String sql = """
                SELECT id, codigo, nombre, descripcion, precio, stock,
                       unidad_medida, activo, fecha_creacion
                FROM producto
                ORDER BY nombre
                """;

        try (Connection conn = Conexion.conectar();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                productos.add(mapearProducto(rs));
            }

        } catch (SQLException e) {
            System.err.println("Error al listar productos: " + e.getMessage());
        }

        return productos;
    }

    public Producto buscarPorId(long id) {
        String sql = """
                SELECT id, codigo, nombre, descripcion, precio, stock,
                       unidad_medida, activo, fecha_creacion
                FROM producto
                WHERE id = ?
                """;

        try (Connection conn = Conexion.conectar();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, id);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapearProducto(rs);
                }
            }

        } catch (SQLException e) {
            System.err.println("Error al buscar producto: " + e.getMessage());
        }

        return null;
    }

    public boolean insertar(Producto producto) {
        String sql = """
                INSERT INTO producto (
                    codigo, nombre, descripcion, precio, stock,
                    unidad_medida, activo
                )
                VALUES (?, ?, ?, ?, ?, ?, ?)
                """;

        try (Connection conn = Conexion.conectar();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            asignarDatos(stmt, producto);
            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("Error al registrar producto: " + e.getMessage());
            return false;
        }
    }

    public boolean actualizar(Producto producto) {
        String sql = """
                UPDATE producto
                SET codigo = ?,
                    nombre = ?,
                    descripcion = ?,
                    precio = ?,
                    stock = ?,
                    unidad_medida = ?,
                    activo = ?
                WHERE id = ?
                """;

        try (Connection conn = Conexion.conectar();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            asignarDatos(stmt, producto);
            stmt.setLong(8, producto.getId());

            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("Error al actualizar producto: " + e.getMessage());
            return false;
        }
    }

    public boolean eliminar(long id) {
        String sql = "DELETE FROM producto WHERE id = ?";

        try (Connection conn = Conexion.conectar();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, id);
            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("Error al eliminar producto: " + e.getMessage());
            return false;
        }
    }

    private void asignarDatos(
            PreparedStatement stmt,
            Producto producto
    ) throws SQLException {
        stmt.setString(1, producto.getCodigo());
        stmt.setString(2, producto.getNombre());
        stmt.setString(3, producto.getDescripcion());
        stmt.setBigDecimal(4, producto.getPrecio());
        stmt.setInt(5, producto.getStock());
        stmt.setString(6, producto.getUnidadMedida());
        stmt.setBoolean(7, producto.isActivo());
    }

    private Producto mapearProducto(ResultSet rs) throws SQLException {
        Producto producto = new Producto();

        producto.setId(rs.getLong("id"));
        producto.setCodigo(rs.getString("codigo"));
        producto.setNombre(rs.getString("nombre"));
        producto.setDescripcion(rs.getString("descripcion"));
        producto.setPrecio(rs.getBigDecimal("precio"));
        producto.setStock(rs.getInt("stock"));
        producto.setUnidadMedida(rs.getString("unidad_medida"));
        producto.setActivo(rs.getBoolean("activo"));
        producto.setFechaCreacion(rs.getTimestamp("fecha_creacion"));

        return producto;
    }
}