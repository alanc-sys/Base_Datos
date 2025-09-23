
import dao.AutorDAOImpl;
import models.Autor;

import java.time.LocalDate;
import java.util.List;

public class TestAutorDAO {
    public static void main(String[] args) {
        AutorDAOImpl autorDAO = new AutorDAOImpl();

        System.out.println("=== TEST DAO AUTORES ===");

        // 1. INSERTAR autor nuevo
        System.out.println("\n1. INSERTANDO autor...");
        Autor nuevoAutor = new Autor("Roberto", "Bolaño", "Chilena",
                LocalDate.of(1953, 4, 28));
        autorDAO.insertar(nuevoAutor);
        System.out.println("ID asignado: " + nuevoAutor.getId());

        // 2. BUSCAR por ID
        System.out.println("\n2. BUSCANDO por ID...");
        Autor encontrado = autorDAO.buscarPorId(nuevoAutor.getId());
        if (encontrado != null) {
            System.out.println("Encontrado: " + encontrado.getNombreCompleto());
        }

        // 3. LISTAR todos
        System.out.println("\n3. LISTANDO todos los autores...");
        List<Autor> todos = autorDAO.buscarTodos();
        for (Autor autor : todos) {
            System.out.println("- " + autor.getNombreCompleto() + " (" + autor.getNacionalidad() + ")");
        }

        // 4. BUSCAR por nacionalidad
        System.out.println("\n4. BUSCANDO autores argentinos...");
        List<Autor> argentinos = autorDAO.buscarPorNacionalidad("Argentina");
        for (Autor autor : argentinos) {
            System.out.println("- " + autor.getNombreCompleto());
        }

        // 5. ACTUALIZAR
        System.out.println("\n5. ACTUALIZANDO autor...");
        if (encontrado != null) {
            encontrado.setNacionalidad("Espana"); // Cambiar género
            autorDAO.actualizar(encontrado);
        }

        // 6. VERIFICAR cambio
        System.out.println("\n6. VERIFICANDO cambio...");
        Autor verificar = autorDAO.buscarPorId(nuevoAutor.getId());
        if (verificar != null) {
            System.out.println("Nueva nacionalidad: " + verificar.getNacionalidad());
        }

        System.out.println("\nTest completado!");
    }
}