package dao;

import config.DatabaseConnection;
import models.Libro;
import models.Autor;

import java.sql.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Year;
import java.util.ArrayList;
import java.util.List;

public class LibroDAOImpl implements LibroDAO {

    private AutorDAO autorDAO;

    public LibroDAOImpl() {
        this.autorDAO = new AutorDAOImpl();  // ← Composición de DAOs
    }

    public LibroDAOImpl(AutorDAO autorDAO) {
        this.autorDAO = autorDAO;
    }
    @Override
    public void insertar(Libro libro) {
        if (libro.getAutor() == null) {
            throw new IllegalArgumentException("El libro debe tener un autor");
        }

        if (libro.getAutor().getId() <= 0) {
            throw new IllegalArgumentException("El autor debe tener un ID válido");
        }

        if (!autorDAO.existe(libro.getAutor().getId())) {
            throw new IllegalArgumentException("El autor con ID " + libro.getAutor().getId() + " no existe");
        }

        if (libro.getIsbn() != null && !libro.getIsbn().trim().isEmpty()) {
            if (existeIsbn(libro.getIsbn())) {
                throw new IllegalArgumentException("Ya existe un libro con ISBN: " + libro.getIsbn());
            }
        }

        String sql = "INSERT INTO libros (titulo, isbn, genero, ano_publicacion, paginas, autor_id, created_at) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            pstmt.setString(1, libro.getTitulo());
            pstmt.setString(2, libro.getIsbn());
            pstmt.setString(3, libro.getGenero());

            if (libro.getAnoPublicacion() != null) {
                pstmt.setInt(4, libro.getAnoPublicacion().getValue());
            } else {
                pstmt.setNull(4, Types.INTEGER);
            }

            pstmt.setInt(5, libro.getPaginas());
            pstmt.setInt(6, libro.getAutor().getId());  // ← Foreign Key

            LocalDateTime now = libro.getCreatedAt() != null ? libro.getCreatedAt() : LocalDateTime.now();
            pstmt.setTimestamp(7, Timestamp.valueOf(now));

            int filasAfectadas = pstmt.executeUpdate();

            if (filasAfectadas > 0) {
                try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        libro.setId(generatedKeys.getInt(1));
                        System.out.println("Libro insertado: " + libro.getTitulo() + " (ID: " + libro.getId() + ")");
                    }
                }
            } else {
                throw new SQLException("Inserción falló, no se afectaron filas");
            }

        } catch (SQLException e) {
            System.err.println("Error insertando libro: " + e.getMessage());
            throw new RuntimeException("Error en base de datos al insertar libro", e);
        }
    }
    @Override
    public Libro buscarPorId(int id) {
        String sql = """
        SELECT l.id, l.titulo, l.isbn, l.genero, l.ano_publicacion, l.paginas, l.created_at,
               a.id as autor_id, a.nombre as autor_nombre, a.apellido as autor_apellido, 
               a.nacionalidad as autor_nacionalidad, a.fecha_nacimiento as autor_fecha_nacimiento
        FROM libros l
        INNER JOIN autores a ON l.autor_id = a.id
        WHERE l.id = ?
        """;

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, id);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return mapearResultSetALibroCompleto(rs);
                }
            }

        } catch (SQLException e) {
            System.err.println("Error buscando libro por ID: " + e.getMessage());
            throw new RuntimeException("Error en base de datos", e);
        }

        return null;
    }

    @Override
    public List<Libro> buscarTodos() {
        List<Libro> libros = new ArrayList<>();

        String sql = """
        SELECT l.id, l.titulo, l.isbn, l.genero, l.ano_publicacion, l.paginas, l.created_at,
               a.id as autor_id, a.nombre as autor_nombre, a.apellido as autor_apellido, 
               a.nacionalidad as autor_nacionalidad, a.fecha_nacimiento as autor_fecha_nacimiento
        FROM libros l
        INNER JOIN autores a ON l.autor_id = a.id
        ORDER BY l.titulo
        """;

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                libros.add(mapearResultSetALibroCompleto(rs));
            }

            System.out.println("Encontrados " + libros.size() + " libros");

        } catch (SQLException e) {
            System.err.println("Error listando todos los libros: " + e.getMessage());
            throw new RuntimeException("Error en base de datos", e);
        }

        return libros;
    }

    @Override
    public Libro buscarPorIsbn(String isbn) {
        if (isbn == null || isbn.trim().isEmpty()) {
            return null; // ISBN inválido
        }

        String sql = """
        SELECT l.id, l.titulo, l.isbn, l.genero, l.ano_publicacion, l.paginas, l.created_at,
               a.id as autor_id, a.nombre as autor_nombre, a.apellido as autor_apellido, 
               a.nacionalidad as autor_nacionalidad, a.fecha_nacimiento as autor_fecha_nacimiento
        FROM libros l
        INNER JOIN autores a ON l.autor_id = a.id
        WHERE l.isbn = ?
        """;

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, isbn.trim());

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return mapearResultSetALibroCompleto(rs);
                }
            }

        } catch (SQLException e) {
            System.err.println("Error buscando libro por ISBN: " + e.getMessage());
            throw new RuntimeException("Error en base de datos", e);
        }

        return null;
    }

    @Override
    public List<Libro> buscarPorTitulo(String titulo) {
        List<Libro> libros = new ArrayList<>();

        if (titulo == null || titulo.trim().isEmpty()) {
            return libros;
        }

        String sql = """
        SELECT l.id, l.titulo, l.isbn, l.genero, l.ano_publicacion, l.paginas, l.created_at,
               a.id as autor_id, a.nombre as autor_nombre, a.apellido as autor_apellido, 
               a.nacionalidad as autor_nacionalidad, a.fecha_nacimiento as autor_fecha_nacimiento
        FROM libros l
        INNER JOIN autores a ON l.autor_id = a.id
        WHERE l.titulo LIKE ?
        ORDER BY l.titulo
        """;

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            String patron = "%" + titulo.trim() + "%";
            pstmt.setString(1, patron);

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    libros.add(mapearResultSetALibroCompleto(rs));
                }
            }

            System.out.println("Encontrados " + libros.size() + " libros con título '" + titulo + "'");

        } catch (SQLException e) {
            System.err.println("Error buscando libros por título: " + e.getMessage());
            throw new RuntimeException("Error en base de datos", e);
        }

        return libros;
    }

    @Override
    public List<Libro> buscarPorGenero(String genero) {
        List<Libro> libros = new ArrayList<>();

        if (genero == null || genero.trim().isEmpty()) {
            return libros;
        }

        String sql = """
        SELECT l.id, l.titulo, l.isbn, l.genero, l.ano_publicacion, l.paginas, l.created_at,
               a.id as autor_id, a.nombre as autor_nombre, a.apellido as autor_apellido, 
               a.nacionalidad as autor_nacionalidad, a.fecha_nacimiento as autor_fecha_nacimiento
        FROM libros l
        INNER JOIN autores a ON l.autor_id = a.id
        WHERE l.genero = ?
        ORDER BY l.ano_publicacion DESC, l.titulo
        """;

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, genero.trim());

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    libros.add(mapearResultSetALibroCompleto(rs));
                }
            }

            System.out.println("Encontrados " + libros.size() + " libros de género '" + genero + "'");

        } catch (SQLException e) {
            System.err.println("Error buscando libros por género: " + e.getMessage());
            throw new RuntimeException("Error en base de datos", e);
        }

        return libros;
    }

    @Override
    public List<Libro> buscarPorAutor(int autorId) {
        List<Libro> libros = new ArrayList<>();

        // VALIDACIÓN: Verificar que el autor existe
        if (autorId <= 0) {
            System.out.println("ID de autor inválido: " + autorId);
            return libros;
        }

        if (!autorDAO.existe(autorId)) {
            System.out.println("Autor con ID " + autorId + " no existe");
            return libros; // Lista vacía
        }

        String sql = """
        SELECT l.id, l.titulo, l.isbn, l.genero, l.ano_publicacion, l.paginas, l.created_at,
               a.id as autor_id, a.nombre as autor_nombre, a.apellido as autor_apellido, 
               a.nacionalidad as autor_nacionalidad, a.fecha_nacimiento as autor_fecha_nacimiento
        FROM libros l
        INNER JOIN autores a ON l.autor_id = a.id
        WHERE l.autor_id = ?
        ORDER BY l.ano_publicacion DESC, l.titulo
        """;

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, autorId);

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    libros.add(mapearResultSetALibroCompleto(rs));
                }
            }

            // Obtener nombre del autor para logging
            String nombreAutor = "ID " + autorId;
            if (!libros.isEmpty()) {
                nombreAutor = libros.get(0).getAutor().getNombreCompleto();
            }

            System.out.println("Encontrados " + libros.size() + " libros de " + nombreAutor);

        } catch (SQLException e) {
            System.err.println("Error buscando libros por autor ID: " + e.getMessage());
            throw new RuntimeException("Error en base de datos", e);
        }

        return libros;
    }

    @Override
    public List<Libro> buscarPorAutor(Autor autor) {
        // Validaciones
        if (autor == null) {
            System.out.println("Autor es null");
            return new ArrayList<>();
        }

        if (autor.getId() <= 0) {
            System.out.println("Autor sin ID válido: " + autor.getNombreCompleto());
            return new ArrayList<>();
        }

        return buscarPorAutor(autor.getId());
    }

    @Override
    public List<Libro> buscarPorRangoAños(int añoInicio, int añoFin) {
        List<Libro> libros = new ArrayList<>();

        // Validaciones
        if (añoInicio > añoFin) {
            System.out.println("Rango de años inválido: " + añoInicio + " - " + añoFin);
            return libros;
        }

        if (añoInicio < 1000 || añoFin > 3000) {
            System.out.println("Años fuera de rango válido: " + añoInicio + " - " + añoFin);
            return libros;
        }

        String sql = """
        SELECT l.id, l.titulo, l.isbn, l.genero, l.ano_publicacion, l.paginas, l.created_at,
               a.id as autor_id, a.nombre as autor_nombre, a.apellido as autor_apellido, 
               a.nacionalidad as autor_nacionalidad, a.fecha_nacimiento as autor_fecha_nacimiento
        FROM libros l
        INNER JOIN autores a ON l.autor_id = a.id
        WHERE l.ano_publicacion BETWEEN ? AND ?
        ORDER BY l.ano_publicacion DESC, l.titulo
        """;

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, añoInicio);
            pstmt.setInt(2, añoFin);

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    libros.add(mapearResultSetALibroCompleto(rs));
                }
            }

            System.out.println("Encontrados " + libros.size() + " libros entre " + añoInicio + " y " + añoFin);

        } catch (SQLException e) {
            System.err.println("Error buscando libros por rango de años: " + e.getMessage());
            throw new RuntimeException("Error en base de datos", e);
        }

        return libros;
    }

    @Override
    public int contarPorGenero(String genero) {
        if (genero == null || genero.trim().isEmpty()) {
            System.out.println("Género inválido o vacío");
            return 0;
        }

        String sql = "SELECT COUNT(*) FROM libros WHERE genero = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, genero.trim());

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    int cantidad = rs.getInt(1);
                    System.out.println("Encontrados " + cantidad + " libros de género '" + genero + "'");
                    return cantidad;
                }
            }

        } catch (SQLException e) {
            System.err.println("Error contando libros por género: " + e.getMessage());
            throw new RuntimeException("Error en base de datos", e);
        }

        return 0;
    }
    @Override
    public List<Libro> buscarPorAño(int año) {
        List<Libro> libros = new ArrayList<>();

        if (año < 1000 || año > 3000) {
            System.out.println("Año inválido: " + año);
            return libros;
        }

        String sql = """
        SELECT l.id, l.titulo, l.isbn, l.genero, l.ano_publicacion, l.paginas, l.created_at,
               a.id as autor_id, a.nombre as autor_nombre, a.apellido as autor_apellido, 
               a.nacionalidad as autor_nacionalidad, a.fecha_nacimiento as autor_fecha_nacimiento
        FROM libros l
        INNER JOIN autores a ON l.autor_id = a.id
        WHERE l.ano_publicacion = ?
        ORDER BY l.titulo
        """;

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, año);

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    libros.add(mapearResultSetALibroCompleto(rs));
                }
            }

            System.out.println("Encontrados " + libros.size() + " libros del año " + año);

        } catch (SQLException e) {
            System.err.println("Error buscando libros por año: " + e.getMessage());
            throw new RuntimeException("Error en base de datos", e);
        }

        return libros;
    }

    @Override
    public void actualizar(Libro libro) {
        if (libro == null || libro.getId() <= 0) {
            throw new IllegalArgumentException("Libro inválido o sin ID para actualizar");
        }

        if (libro.getAutor() == null || libro.getAutor().getId() <= 0) {
            throw new IllegalArgumentException("El libro debe tener un autor válido");
        }

        if (!existe(libro.getId())) {
            throw new IllegalArgumentException("No existe libro con ID: " + libro.getId());
        }

        if (!autorDAO.existe(libro.getAutor().getId())) {
            throw new IllegalArgumentException("No existe autor con ID: " + libro.getAutor().getId());
        }

        if (libro.getIsbn() != null && !libro.getIsbn().trim().isEmpty()) {
            Libro libroConMismoISBN = buscarPorIsbn(libro.getIsbn());
            if (libroConMismoISBN != null && libroConMismoISBN.getId() != libro.getId()) {
                throw new IllegalArgumentException("Ya existe otro libro con ISBN: " + libro.getIsbn());
            }
        }

        String sql = "UPDATE libros SET titulo = ?, isbn = ?, genero = ?, ano_publicacion = ?, paginas = ?, autor_id = ? WHERE id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, libro.getTitulo());
            pstmt.setString(2, libro.getIsbn());
            pstmt.setString(3, libro.getGenero());

            if (libro.getAnoPublicacion() != null) {
                pstmt.setInt(4, libro.getAnoPublicacion().getValue());
            } else {
                pstmt.setNull(4, Types.INTEGER);
            }

            pstmt.setInt(5, libro.getPaginas());
            pstmt.setInt(6, libro.getAutor().getId());
            pstmt.setInt(7, libro.getId()); // WHERE clause

            int filasAfectadas = pstmt.executeUpdate();

            if (filasAfectadas > 0) {
                System.out.println("Libro actualizado: " + libro.getTitulo());
            } else {
                System.out.println("No se actualizó ningún libro con ID: " + libro.getId());
            }

        } catch (SQLException e) {
            System.err.println("Error actualizando libro: " + e.getMessage());
            throw new RuntimeException("Error en base de datos al actualizar libro", e);
        }
    }

    @Override
    public void eliminar(int id) {
        if (id <= 0) {
            throw new IllegalArgumentException("ID inválido para eliminar: " + id);
        }

        if (!existe(id)) {
            System.out.println("No existe libro con ID: " + id);
            return;
        }
        String sql = "DELETE FROM libros WHERE id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, id);

            int filasAfectadas = pstmt.executeUpdate();

            if (filasAfectadas > 0) {
                System.out.println("Libro eliminado con ID: " + id);
            } else {
                System.out.println("No se eliminó ningún libro con ID: " + id);
            }

        } catch (SQLException e) {
            System.err.println("Error eliminando libro: " + e.getMessage());

            if (e.getMessage().contains("foreign key constraint")) {
                throw new RuntimeException("No se puede eliminar: el libro tiene préstamos asociados", e);
            } else {
                throw new RuntimeException("Error en base de datos al eliminar libro", e);
            }
        }
    }

    @Override
    public boolean existe(int id) {
        if (id <= 0) {
            return false;
        }

        String sql = "SELECT COUNT(*) FROM libros WHERE id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, id);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }

        } catch (SQLException e) {
            System.err.println("Error verificando existencia de libro: " + e.getMessage());
        }

        return false;
    }

    @Override
    public boolean existeIsbn(String isbn) {
        if (isbn == null || isbn.trim().isEmpty()) {
            return false;
        }

        String sql = "SELECT COUNT(*) FROM libros WHERE isbn = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, isbn.trim());

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }

        } catch (SQLException e) {
            System.err.println("Error verificando existencia de ISBN: " + e.getMessage());
        }

        return false;
    }

    private Libro mapearResultSetALibroCompleto(ResultSet rs) throws SQLException {

        int libroId = rs.getInt("id");
        String titulo = rs.getString("titulo");
        String isbn = rs.getString("isbn");
        String genero = rs.getString("genero");

        int anoInt = rs.getInt("ano_publicacion");
        Year anoPublicacion = rs.wasNull() ? null : Year.of(anoInt);

        int paginas = rs.getInt("paginas");

        Timestamp createdTimestamp = rs.getTimestamp("created_at");
        LocalDateTime createdAt = createdTimestamp != null ? createdTimestamp.toLocalDateTime() : null;

        int autorId = rs.getInt("autor_id");
        String autorNombre = rs.getString("autor_nombre");
        String autorApellido = rs.getString("autor_apellido");
        String autorNacionalidad = rs.getString("autor_nacionalidad");

        Date autorFechaNacimientoSQL = rs.getDate("autor_fecha_nacimiento");
        LocalDate autorFechaNacimiento = autorFechaNacimientoSQL != null ?
                autorFechaNacimientoSQL.toLocalDate() : null;

        Autor autor = new Autor(autorId, autorNombre, autorApellido,
                autorNacionalidad, autorFechaNacimiento);

        return new Libro(libroId, titulo, isbn, genero, anoPublicacion,
                paginas, createdAt, autor);
    }
}
