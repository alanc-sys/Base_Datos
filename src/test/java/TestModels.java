import models.Autor;
import org.junit.jupiter.api.Test;
import java.time.LocalDate;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class TestModels {

    @Test
    public void testCrearAutor() {
        Autor autorNuevo = new Autor(
                "Gabriel",
                "García Márquez",
                "Colombiana",
                LocalDate.of(1927, 3, 6)
        );

        assertEquals("Gabriel", autorNuevo.getNombre());
        assertEquals("García Márquez", autorNuevo.getApellido());
        assertEquals("Colombiana", autorNuevo.getNacionalidad());
        assertEquals(LocalDate.of(1927, 3, 6), autorNuevo.getFechaNacimiento());
    }

    @Test
    public void testModificarAutor() {
        Autor autorExistente = new Autor(
                1,
                "Jorge Luis",
                "Borges",
                "Argentina",
                LocalDate.of(1899, 8, 24)
        );

        autorExistente.setNacionalidad("Argentino");
        assertEquals("Argentino", autorExistente.getNacionalidad());
    }
}
