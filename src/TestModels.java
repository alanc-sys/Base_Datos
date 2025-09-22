import models.Autor;
import java.time.LocalDate;

public class TestModels {
    public static void main(String[] args) {
        // Crear autor nuevo (sin ID)
        Autor autorNuevo = new Autor(
                "Gabriel",
                "García Márquez",
                "Colombiana",
                LocalDate.of(1927, 3, 6)
        );

        System.out.println("models.Autor nuevo: " + autorNuevo);

        // Simular autor traído de BD (con ID)
        Autor autorExistente = new Autor(
                1,
                "Jorge Luis",
                "Borges",
                "Argentina",
                LocalDate.of(1899, 8, 24)
        );

        System.out.println("models.Autor existente: " + autorExistente);

        // Modificar datos
        autorExistente.setNacionalidad("Argentino");
        System.out.println("Después del cambio: " + autorExistente.getNacionalidad());
    }
}