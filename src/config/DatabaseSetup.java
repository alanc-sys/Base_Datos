package config;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseSetup {
    public static void createDatabase() throws SQLException {
        try (Connection conn = DatabaseConnection.getConnectionForSetup();
             Statement stmt = conn.createStatement() ) {
            String createDB = "CREATE DATABASE IF NOT EXISTS libreria " +
                    "CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci";
            //USO DE EXECUTE PARA CON SQL NO DEVUELVE DATOS CON executeQuery() SI DEVUELVE DATOS
            stmt.execute(createDB);
            System.out.println("Base de datos creada");

        } catch (SQLException e) {
            System.err.println("Error: " + e.getMessage());
        }
    }
    public static void createTables() {
        try (Connection conn = DatabaseConnection.getConnection();
            Statement stmt = conn.createStatement()) {

            String createAutores = """
                    CREATE TABLE IF NOT EXISTS autores (
                        id INT PRIMARY KEY AUTO_INCREMENT,
                        nombre VARCHAR(100) NOT NULL,
                        apellido VARCHAR(100) NOT NULL,
                        nacionalidad VARCHAR(50),
                        fecha_nacimiento DATE,
                        created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
                    )
                    """;
            stmt.execute(createAutores);
            System.out.println("Tabla de autores creada");

            String createLibros = """
                    CREATE TABLE IF NOT EXISTS libros (
                        id INT PRIMARY KEY AUTO_INCREMENT,
                        titulo VARCHAR(100) NOT NULL,
                        isbn VARCHAR(13) UNIQUE,
                        genero VARCHAR(50),
                        ano_publicacion YEAR,
                        paginas INT,
                        autor_id INT NOT NULL,
                        created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                        FOREIGN KEY (autor_id) REFERENCES autores(id)
                    )
                    """;
            stmt.execute(createLibros);
            System.out.println("Tabla de libros creada");


        } catch (SQLException e) {
            System.err.println("Error creando tabla: " + e.getMessage());
        }
    }
    public static void insertSampleData() {
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement()) {

            // ¿Por qué INSERT IGNORE? No falla si ya existe
            String insertAutores = """
            INSERT IGNORE INTO autores (nombre, apellido, nacionalidad, fecha_nacimiento) 
            VALUES 
            ('Gabriel', 'García Márquez', 'Colombiana', '1927-03-06'),
            ('Jorge Luis', 'Borges', 'Argentina', '1899-08-24'),
            ('Isabel', 'Allende', 'Chilena', '1942-08-02')
            """;
            stmt.execute(insertAutores);
            System.out.println("Autores agregados");

            String insertLibros = """
            INSERT IGNORE INTO libros (titulo, isbn, genero, ano_publicacion, paginas, autor_id) 
            VALUES 
            ('Cien años de soledad', '9788497592208', 'Realismo mágico', 1967, 432, 1),
            ('Ficciones', '9788497592222', 'Cuentos', 1944, 256, 2),
            ('La casa de los espíritus', '9788497592246', 'Realismo mágico', 1982, 448, 3)
            """;
            stmt.execute(insertLibros);
            System.out.println("Libros agregados");

        } catch (SQLException e) {
            System.err.println("Error agregando datos: " + e.getMessage());
        }
    }

}
