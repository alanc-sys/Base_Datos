package dao;

import models.Libro;
import models.Autor;
import java.util.List;

public interface LibroDAO {

    void insertar(Libro libro);

    Libro buscarPorId(int id);
    List<Libro> buscarTodos();
    Libro buscarPorIsbn(String isbn);
    List<Libro> buscarPorTitulo(String titulo);
    List<Libro> buscarPorGenero(String genero);
    List<Libro> buscarPorAño(int año);
    List<Libro> buscarPorAutor(int autorId);
    List<Libro> buscarPorAutor(Autor autor);

    void actualizar(Libro libro);

    void eliminar(int id);

    boolean existe(int id);
    boolean existeIsbn(String isbn);

    List<Libro> buscarPorRangoAños(int añoInicio, int añoFin);
    int contarPorGenero(String genero);
}