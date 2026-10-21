package vallegrande.edu.pe.misistema.model;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class UsuarioDAO {

    public List<Usuario> listar() {
        List<Usuario> lista = new ArrayList<>();

        String sql = """
                SELECT id, nombre_organizacion, tipo_documento,
                       identificacion_fiscal, telefono, direccion_empresa,
                       nombre_responsable, correo_electronico, descripcion,
                       fecha_creacion, estado
                FROM consulta
                ORDER BY fecha_creacion DESC
                """;

        try (Connection conn = Conexion.conectar();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                lista.add(mapearConsulta(rs));
            }

        } catch (SQLException e) {
            System.err.println("Error al listar consultas: " + e.getMessage());
        }

        return lista;
    }

    public Usuario buscarPorId(int id) {
        String sql = """
                SELECT id, nombre_organizacion, tipo_documento,
                       identificacion_fiscal, telefono, direccion_empresa,
                       nombre_responsable, correo_electronico, descripcion,
                       fecha_creacion, estado
                FROM consulta
                WHERE id = ?
                """;

        try (Connection conn = Conexion.conectar();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapearConsulta(rs);
                }
            }

        } catch (SQLException e) {
            System.err.println("Error al buscar consulta: " + e.getMessage());
        }

        return null;
    }

    public boolean insertar(Usuario consulta) {
        String sql = """
                INSERT INTO consulta (
                    nombre_organizacion, tipo_documento,
                    identificacion_fiscal, telefono, direccion_empresa,
                    nombre_responsable, correo_electronico, descripcion
                )
                VALUES (?, ?, ?, ?, ?, ?, ?, ?)
                """;

        try (Connection conn = Conexion.conectar();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            asignarDatos(stmt, consulta);
            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("Error al registrar consulta: " + e.getMessage());
            return false;
        }
    }

    public boolean actualizar(Usuario consulta) {
        String sql = """
                UPDATE consulta
                SET nombre_organizacion = ?,
                    tipo_documento = ?,
                    identificacion_fiscal = ?,
                    telefono = ?,
                    direccion_empresa = ?,
                    nombre_responsable = ?,
                    correo_electronico = ?,
                    descripcion = ?
                WHERE id = ?
                """;

        try (Connection conn = Conexion.conectar();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            asignarDatos(stmt, consulta);
            stmt.setInt(9, consulta.getId());

            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("Error al actualizar consulta: " + e.getMessage());
            return false;
        }
    }

    public boolean eliminar(int id) {
        String sql = "DELETE FROM consulta WHERE id = ?";

        try (Connection conn = Conexion.conectar();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("Error al eliminar consulta: " + e.getMessage());
            return false;
        }
    }

    public boolean actualizarEstado(int id, String estado) {
        if (!"Pendiente".equals(estado) && !"Atendido".equals(estado)) {
            System.err.println("Estado no válido.");
            return false;
        }

        String sql = "UPDATE consulta SET estado = ? WHERE id = ?";

        try (Connection conn = Conexion.conectar();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, estado);
            stmt.setInt(2, id);
            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("Error al actualizar estado: " + e.getMessage());
            return false;
        }
    }

    private void asignarDatos(PreparedStatement stmt, Usuario consulta)
            throws SQLException {
        stmt.setString(1, consulta.getNombreOrganizacion());
        stmt.setString(2, consulta.getTipoDocumento());
        stmt.setString(3, consulta.getIdentificacionFiscal());
        stmt.setString(4, consulta.getTelefono());
        stmt.setString(5, consulta.getDireccion());
        stmt.setString(6, consulta.getResponsable());
        stmt.setString(7, consulta.getCorreo());
        stmt.setString(8, consulta.getDescripcion());
    }

    private Usuario mapearConsulta(ResultSet rs) throws SQLException {
        Usuario consulta = new Usuario();

        consulta.setId(rs.getInt("id"));
        consulta.setNombreOrganizacion(rs.getString("nombre_organizacion"));
        consulta.setTipoDocumento(rs.getString("tipo_documento"));
        consulta.setIdentificacionFiscal(rs.getString("identificacion_fiscal"));
        consulta.setTelefono(rs.getString("telefono"));
        consulta.setDireccion(rs.getString("direccion_empresa"));
        consulta.setResponsable(rs.getString("nombre_responsable"));
        consulta.setCorreo(rs.getString("correo_electronico"));
        consulta.setDescripcion(rs.getString("descripcion"));
        consulta.setFechaRegistro(rs.getTimestamp("fecha_creacion"));
        consulta.setEstado(rs.getString("estado"));

        return consulta;
    }
}