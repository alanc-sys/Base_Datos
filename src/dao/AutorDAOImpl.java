package dao;

import config.DatabaseConnection;
import models.Autor;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class AutorDAOImpl implements AutorDAO {

    @Override
    public void insertar(Autor autor) {
        String sql = "INSERT INTO autores (nombre, apellido, nacionalidad, fecha_nacimiento) VALUES (?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setString(1, autor.getNombre());
            pstmt.setString(2, autor.getApellido());
            pstmt.setString(3, autor.getNacionalidad());
            pstmt.setDate(4, autor.getFechaNacimiento() != null ?
                    Date.valueOf(autor.getFechaNacimiento()) : null);

            int filasAfectadas = pstmt.executeUpdate();
            if (filasAfectadas > 0) {
                try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        autor.setId(generatedKeys.getInt(1));
                        System.out.println("Autor insertado con ID: " + autor.getId());
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("Error agregando autor: " + e.getMessage());
            throw new RuntimeException("Error en base de datos", e);
        }
    }
    @Override
    public Autor buscarPorId(int id) {
        String sql = "SELECT id, nombre, apellido, nacionalidad, fecha_nacimiento FROM autores WHERE id = ?";
        try(Connection conn = DatabaseConnection.getConnection();
        PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            try(ResultSet rs = pstmt.executeQuery()) {
                if(rs.next()) {
                    return mapearResultSetAAutor(rs);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error buscando autor: " + e.getMessage());
            throw new RuntimeException("Error en base de datos", e);
        }
        return null;
    }

    @Override
    public List<Autor> buscarTodos() {
        List<Autor> autores = new ArrayList<>();
        String sql = "SELECT id, nombre, apellido, nacionalidad, fecha_nacimiento FROM autores ORDER BY apellido, nombre";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                autores.add(mapearResultSetAAutor(rs));
            }

        } catch (SQLException e) {
            System.err.println("Error listando autores: " + e.getMessage());
            throw new RuntimeException("Error en base de datos", e);
        }

        return autores;
    }

    @Override
    public List<Autor> buscarPorNombre(String nombre) {
        List<Autor> autores = new ArrayList<>();
        String sql = "SELECT id, nombre, apellido, nacionalidad, fecha_nacimiento " +
                "FROM autores WHERE nombre LIKE ? OR apellido LIKE ? ORDER BY apellido";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            String patron = "%" + nombre + "%";
            pstmt.setString(1, patron);
            pstmt.setString(2, patron);

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    autores.add(mapearResultSetAAutor(rs));
                }
            }

        } catch (SQLException e) {
            System.err.println("Error buscando por nombre: " + e.getMessage());
            throw new RuntimeException("Error en base de datos", e);
        }

        return autores;
    }

    @Override
    public List<Autor> buscarPorNacionalidad(String nacionalidad) {
        List<Autor> autores = new ArrayList<>();
        String sql = "SELECT id, nombre, apellido, nacionalidad, fecha_nacimiento " +
                "FROM autores WHERE nacionalidad = ? ORDER BY apellido";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, nacionalidad);

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    autores.add(mapearResultSetAAutor(rs));
                }
            }

        } catch (SQLException e) {
            System.err.println("Error buscando por nacionalidad: " + e.getMessage());
            throw new RuntimeException("Error en base de datos", e);
        }

        return autores;
    }

    @Override
    public void actualizar(Autor autor) {
        String sql = "UPDATE autores SET nombre = ?, apellido = ?, nacionalidad = ?, fecha_nacimiento = ? WHERE id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, autor.getNombre());
            pstmt.setString(2, autor.getApellido());
            pstmt.setString(3, autor.getNacionalidad());
            pstmt.setDate(4, autor.getFechaNacimiento() != null ?
                    Date.valueOf(autor.getFechaNacimiento()) : null);
            pstmt.setInt(5, autor.getId());

            int filasAfectadas = pstmt.executeUpdate();

            if (filasAfectadas > 0) {
                System.out.println("Autor actualizado: " + autor.getNombreCompleto());
            } else {
                System.out.println("No se encontró autor con ID: " + autor.getId());
            }

        } catch (SQLException e) {
            System.err.println("Error actualizando autor: " + e.getMessage());
            throw new RuntimeException("Error en base de datos", e);
        }
    }

    @Override
    public void eliminar(int id) {
        String sql = "DELETE FROM autores WHERE id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, id);

            int filasAfectadas = pstmt.executeUpdate();

            if (filasAfectadas > 0) {
                System.out.println("Autor eliminado con ID: " + id);
            } else {
                System.out.println("No se encontró autor con ID: " + id);
            }

        } catch (SQLException e) {
            System.err.println("Error eliminando autor: " + e.getMessage());

            // Manejar error de Foreign Key
            if (e.getMessage().contains("foreign key constraint")) {
                throw new RuntimeException("No se puede eliminar: el autor tiene libros asociados", e);
            } else {
                throw new RuntimeException("Error en base de datos", e);
            }
        }
    }

    @Override
    public boolean existe(int id) {
        String sql = "SELECT COUNT(*) FROM autores WHERE id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, id);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }

        } catch (SQLException e) {
            System.err.println("Error verificando existencia: " + e.getMessage());
        }

        return false;
    }

    private Autor mapearResultSetAAutor(ResultSet rs) throws SQLException {
        int id = rs.getInt("id");
        String nombre = rs.getString("nombre");
        String apellido = rs.getString("apellido");
        String nacionalidad = rs.getString("nacionalidad");

        // Manejar fecha que puede ser NULL
        Date fechaSQL = rs.getDate("fecha_nacimiento");
        LocalDate fechaNacimiento = fechaSQL != null ? fechaSQL.toLocalDate() : null;

        return new Autor(id, nombre, apellido, nacionalidad, fechaNacimiento);
    }
}
