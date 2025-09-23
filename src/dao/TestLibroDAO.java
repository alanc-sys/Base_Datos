package dao;

import models.Autor;
import models.Libro;

import java.util.List;

public class TestLibroDAO {
    public static void main(String[] args) {
        AutorDAO autorDAO = new AutorDAOImpl();
        LibroDAO libroDAO = new LibroDAOImpl(autorDAO);

        System.out.println("=== TEST LIBRO DAO ===");

        System.out.println("\n1. LIBROS DE GARCÍA MÁRQUEZ:");
        List<Libro> librosGarcia = libroDAO.buscarPorAutor(1); // Asumiendo ID 1
        librosGarcia.forEach(libro ->
                System.out.println("- " + libro.getTitulo() + " (" + libro.getAnoPublicacion() + ")"));

        System.out.println("\n2. BUSCAR CON OBJETO AUTOR:");
        Autor autor = autorDAO.buscarPorId(1);
        if (autor != null) {
            List<Libro> libros = libroDAO.buscarPorAutor(autor);
            System.out.println(autor.getNombreCompleto() + " tiene " + libros.size() + " libros");
        }

        System.out.println("\n3. LIBROS 1960-1970:");
        List<Libro> años60 = libroDAO.buscarPorRangoAños(1960, 1970);
        años60.forEach(libro ->
                System.out.println("- " + libro.getTitulo() + " (" + libro.getAnoPublicacion() + ")"));

        System.out.println("\n4. ESTADÍSTICAS POR GÉNERO:");
        int cuentos = libroDAO.contarPorGenero("Cuentos");
        int novelas = libroDAO.contarPorGenero("Novela");
        System.out.println("Cuentos: " + cuentos);
        System.out.println("Novelas: " + novelas);

        System.out.println("\nTest completado!");
    }
}
