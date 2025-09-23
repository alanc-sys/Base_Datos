package dao;

import models.Autor;
import java.util.List;

public interface AutorDAO {

    // CREATE - Insertar autor nuevo
    void insertar(Autor autor);

    // READ - Consultas
    Autor buscarPorId(int id);
    List<Autor> buscarTodos();
    List<Autor> buscarPorNombre(String nombre);
    List<Autor> buscarPorNacionalidad(String nacionalidad);

    // UPDATE - Modificar autor existente
    void actualizar(Autor autor);

    // DELETE - Eliminar autor
    void eliminar(int id);
    boolean existe(int id);
}