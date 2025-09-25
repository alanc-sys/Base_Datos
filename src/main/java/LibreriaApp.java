
import dao.*;
import models.*;
import java.time.LocalDate;
import java.time.Year;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Scanner;

public class LibreriaApp {

    private static AutorDAO autorDAO;
    private static LibroDAO libroDAO;
    private static Scanner scanner;

    public static void main(String[] args) {
        inicializarSistema();

        mostrarBienvenida();

        // Menú principal
        boolean continuar = true;
        while (continuar) {
            mostrarMenuPrincipal();
            int opcion = leerOpcion();

            try {
                continuar = procesarOpcion(opcion);
            } catch (Exception e) {
                System.err.println("Error inesperado: " + e.getMessage());
                System.out.println("Presiona ENTER para continuar...");
                scanner.nextLine();
            }
        }

        mostrarDespedida();
    }

    private static void inicializarSistema() {
        System.out.println("🚀 Inicializando Sistema de Librería...");

        try {
            // Inicializar DAOs
            autorDAO = new AutorDAOImpl();
            libroDAO = new LibroDAOImpl(autorDAO);
            scanner = new Scanner(System.in);

            System.out.println("Sistema inicializado correctamente");

        } catch (Exception e) {
            System.err.println("Error inicializando sistema: " + e.getMessage());
            System.err.println("Verifica que MySQL esté corriendo y la base de datos configurada.");
            System.exit(1);
        }
    }

    private static void mostrarBienvenida() {
        limpiarPantalla();
        System.out.println("╔══════════════════════════════════════════════════════════════╗");
        System.out.println("║                    📚 SISTEMA DE LIBRERÍA 📚                 ║");
        System.out.println("║                                                              ║");
        System.out.println("║              Gestión de Autores y Libros                     ║");
        System.out.println("║                     Versión 1.0                              ║");
        System.out.println("╚══════════════════════════════════════════════════════════════╝");
        System.out.println();
        pausar();
    }

    private static void mostrarDespedida() {
        limpiarPantalla();
        System.out.println("╔══════════════════════════════════════════════════════════════╗");
        System.out.println("║                    👋 ¡HASTA PRONTO! 👋                      ║");
        System.out.println("║                                                              ║");
        System.out.println("║              Gracias por usar el Sistema de Librería        ║");
        System.out.println("║                                                              ║");
        System.out.println("╚══════════════════════════════════════════════════════════════╝");

        // Cerrar resources
        if (scanner != null) {
            scanner.close();
        }
    }

    // =================================================================
    // MENÚ PRINCIPAL
    // =================================================================

    private static void mostrarMenuPrincipal() {
        limpiarPantalla();
        System.out.println("═══════════════════════════════════════════════════════════════");
        System.out.println("                        📋 MENÚ PRINCIPAL");
        System.out.println("═══════════════════════════════════════════════════════════════");
        System.out.println();
        System.out.println("👥 GESTIÓN DE AUTORES:");
        System.out.println("  1. Ver todos los autores");
        System.out.println("  2. Buscar autor");
        System.out.println("  3. Agregar nuevo autor");
        System.out.println("  4. Modificar autor");
        System.out.println("  5. Eliminar autor");
        System.out.println();
        System.out.println("📚 GESTIÓN DE LIBROS:");
        System.out.println("  6. Ver todos los libros");
        System.out.println("  7. Buscar libro");
        System.out.println("  8. Agregar nuevo libro");
        System.out.println("  9. Modificar libro");
        System.out.println("  10. Eliminar libro");
        System.out.println();
        System.out.println("📊 REPORTES Y CONSULTAS:");
        System.out.println("  11. Libros por autor");
        System.out.println("  12. Libros por género");
        System.out.println("  13. Libros por año");
        System.out.println("  14. Estadísticas generales");
        System.out.println();
        System.out.println("🔧 SISTEMA:");
        System.out.println("  0. Salir");
        System.out.println();
        System.out.println("═══════════════════════════════════════════════════════════════");
    }

    private static boolean procesarOpcion(int opcion) {
        switch (opcion) {
            case 1:
                verTodosAutores();
                break;
            case 2:
                buscarAutor();
                break;
            case 3:
                agregarAutor();
                break;
            case 4:
                modificarAutor();
                break;
            case 5:
                eliminarAutor();
                break;

            case 6:
                verTodosLibros();
                break;
            case 7:
                buscarLibro();
                break;
            case 8:
                agregarLibro();
                break;
            case 9:
                modificarLibro();
                break;
            case 10:
                eliminarLibro();
                break;

            case 11:
                librosPorAutor();
                break;
            case 12:
                librosPorGenero();
                break;
            case 13:
                librosPorAño();
                break;
            case 14:
                estadisticasGenerales();
                break;

            // SISTEMA
            case 0:
                return false; // Salir

            default:
                System.out.println("Opción inválida. Por favor selecciona una opción del menú.");
                pausar();
        }
        return true; // Continuar
    }

    private static void verTodosAutores() {
        mostrarTitulo("👥 TODOS LOS AUTORES");

        try {
            List<Autor> autores = autorDAO.buscarTodos();

            if (autores.isEmpty()) {
                System.out.println("📭 No hay autores registrados en el sistema.");
            } else {
                System.out.println("Encontrados " + autores.size() + " autores:");
                System.out.println();
                System.out.printf("%-5s %-20s %-20s %-15s %-12s%n",
                        "ID", "NOMBRE", "APELLIDO", "NACIONALIDAD", "NACIMIENTO");
                System.out.println("─".repeat(75));

                for (Autor autor : autores) {
                    System.out.printf("%-5d %-20s %-20s %-15s %-12s%n",
                            autor.getId(),
                            truncar(autor.getNombre(), 20),
                            truncar(autor.getApellido(), 20),
                            truncar(autor.getNacionalidad(), 15),
                            autor.getFechaNacimiento() != null ? autor.getFechaNacimiento().toString() : "N/A"
                    );
                }
            }

        } catch (Exception e) {
            System.err.println("Error al obtener autores: " + e.getMessage());
        }

        pausar();
    }

    private static void buscarAutor() {
        mostrarTitulo("BUSCAR AUTOR");

        System.out.println("Opciones de búsqueda:");
        System.out.println("1. Por ID");
        System.out.println("2. Por nombre");
        System.out.println("3. Por nacionalidad");
        System.out.print("\nSelecciona una opción: ");

        int opcion = leerOpcion();

        try {
            switch (opcion) {
                case 1:
                    buscarAutorPorId();
                    break;
                case 2:
                    buscarAutorPorNombre();
                    break;
                case 3:
                    buscarAutorPorNacionalidad();
                    break;
                default:
                    System.out.println("Opción inválida.");
            }
        } catch (Exception e) {
            System.err.println("Error en búsqueda: " + e.getMessage());
        }

        pausar();
    }

    private static void buscarAutorPorId() {
        System.out.print("Ingresa el ID del autor: ");
        int id = leerEntero();

        Autor autor = autorDAO.buscarPorId(id);
        if (autor != null) {
            mostrarDetalleAutor(autor);
        } else {
            System.out.println("No se encontró autor con ID: " + id);
        }
    }

    private static void buscarAutorPorNombre() {
        System.out.print("Ingresa el nombre o apellido: ");
        String nombre = scanner.nextLine().trim();

        if (nombre.isEmpty()) {
            System.out.println("El nombre no puede estar vacío.");
            return;
        }

        List<Autor> autores = autorDAO.buscarPorNombre(nombre);
        mostrarListaAutores(autores, "con nombre '" + nombre + "'");
    }

    private static void buscarAutorPorNacionalidad() {
        System.out.print("Ingresa la nacionalidad: ");
        String nacionalidad = scanner.nextLine().trim();

        if (nacionalidad.isEmpty()) {
            System.out.println("La nacionalidad no puede estar vacía.");
            return;
        }

        List<Autor> autores = autorDAO.buscarPorNacionalidad(nacionalidad);
        mostrarListaAutores(autores, "de nacionalidad '" + nacionalidad + "'");
    }

    private static void agregarAutor() {
        mostrarTitulo("➕ AGREGAR NUEVO AUTOR");

        try {
            System.out.print("Nombre: ");
            String nombre = scanner.nextLine().trim();

            System.out.print("Apellido: ");
            String apellido = scanner.nextLine().trim();

            System.out.print("Nacionalidad: ");
            String nacionalidad = scanner.nextLine().trim();

            System.out.print("Fecha de nacimiento (YYYY-MM-DD) o ENTER para omitir: ");
            String fechaStr = scanner.nextLine().trim();
            LocalDate fechaNacimiento = null;

            if (!fechaStr.isEmpty()) {
                try {
                    fechaNacimiento = LocalDate.parse(fechaStr);
                } catch (DateTimeParseException e) {
                    System.out.println("Fecha inválida, se guardará sin fecha de nacimiento.");
                }
            }

            // Validaciones básicas
            if (nombre.isEmpty() || apellido.isEmpty()) {
                System.out.println("Nombre y apellido son obligatorios.");
                return;
            }

            // Crear y guardar autor
            Autor nuevoAutor = new Autor(nombre, apellido, nacionalidad, fechaNacimiento);
            autorDAO.insertar(nuevoAutor);

            System.out.println("Autor agregado exitosamente:");
            mostrarDetalleAutor(nuevoAutor);

        } catch (Exception e) {
            System.err.println("Error agregando autor: " + e.getMessage());
        }

        pausar();
    }

    private static void modificarAutor() {
        mostrarTitulo("MODIFICAR AUTOR");

        System.out.print("Ingresa el ID del autor a modificar: ");
        int id = leerEntero();

        try {
            Autor autor = autorDAO.buscarPorId(id);
            if (autor == null) {
                System.out.println("No se encontró autor con ID: " + id);
                return;
            }

            System.out.println("Autor actual:");
            mostrarDetalleAutor(autor);
            System.out.println();
            System.out.println("Ingresa los nuevos datos (ENTER para mantener actual):");

            System.out.print("Nombre [" + autor.getNombre() + "]: ");
            String nombre = scanner.nextLine().trim();
            if (!nombre.isEmpty()) {
                autor.setNombre(nombre);
            }

            System.out.print("Apellido [" + autor.getApellido() + "]: ");
            String apellido = scanner.nextLine().trim();
            if (!apellido.isEmpty()) {
                autor.setApellido(apellido);
            }

            System.out.print("Nacionalidad [" + autor.getNacionalidad() + "]: ");
            String nacionalidad = scanner.nextLine().trim();
            if (!nacionalidad.isEmpty()) {
                autor.setNacionalidad(nacionalidad);
            }

            String fechaActual = autor.getFechaNacimiento() != null ?
                    autor.getFechaNacimiento().toString() : "N/A";
            System.out.print("Fecha nacimiento [" + fechaActual + "] (YYYY-MM-DD): ");
            String fechaStr = scanner.nextLine().trim();
            if (!fechaStr.isEmpty()) {
                try {
                    autor.setFechaNacimiento(LocalDate.parse(fechaStr));
                } catch (DateTimeParseException e) {
                    System.out.println("Fecha inválida, se mantiene la anterior.");
                }
            }

            autorDAO.actualizar(autor);

            System.out.println("Autor modificado exitosamente:");
            mostrarDetalleAutor(autor);

        } catch (Exception e) {
            System.err.println("Error modificando autor: " + e.getMessage());
        }

        pausar();
    }

    private static void eliminarAutor() {
        mostrarTitulo("🗑️ ELIMINAR AUTOR");

        System.out.print("Ingresa el ID del autor a eliminar: ");
        int id = leerEntero();

        try {
            Autor autor = autorDAO.buscarPorId(id);
            if (autor == null) {
                System.out.println("No se encontró autor con ID: " + id);
                return;
            }

            System.out.println("Autor a eliminar:");
            mostrarDetalleAutor(autor);

            // Verificar si tiene libros
            List<Libro> libros = libroDAO.buscarPorAutor(autor);
            if (!libros.isEmpty()) {
                System.out.println("⚠ADVERTENCIA: Este autor tiene " + libros.size() + " libro(s) asociados:");
                for (Libro libro : libros) {
                    System.out.println("  - " + libro.getTitulo());
                }
                System.out.println("No se puede eliminar hasta que se eliminen o reasignen los libros.");
                return;
            }

            System.out.print("¿Estás seguro de eliminar este autor? (S/N): ");
            String confirmacion = scanner.nextLine().trim().toLowerCase();

            if (confirmacion.equals("s") || confirmacion.equals("si")) {
                autorDAO.eliminar(id);
                System.out.println("Autor eliminado exitosamente.");
            } else {
                System.out.println("Eliminación cancelada.");
            }

        } catch (Exception e) {
            System.err.println("Error eliminando autor: " + e.getMessage());
        }

        pausar();
    }


    private static void verTodosLibros() {
        mostrarTitulo("TODOS LOS LIBROS");

        try {
            List<Libro> libros = libroDAO.buscarTodos();

            if (libros.isEmpty()) {
                System.out.println("📭 No hay libros registrados en el sistema.");
            } else {
                System.out.println("Encontrados " + libros.size() + " libros:");
                System.out.println();
                System.out.printf("%-5s %-30s %-25s %-15s %-6s%n",
                        "ID", "TÍTULO", "AUTOR", "GÉNERO", "AÑO");
                System.out.println("─".repeat(85));

                for (Libro libro : libros) {
                    System.out.printf("%-5d %-30s %-25s %-15s %-6s%n",
                            libro.getId(),
                            truncar(libro.getTitulo(), 30),
                            truncar(libro.getAutor().getNombreCompleto(), 25),
                            truncar(libro.getGenero(), 15),
                            libro.getAnoPublicacion() != null ? libro.getAnoPublicacion().toString() : "N/A"
                    );
                }
            }

        } catch (Exception e) {
            System.err.println("Error al obtener libros: " + e.getMessage());
        }

        pausar();
    }

    private static void buscarLibro() {
        mostrarTitulo("🔍 BUSCAR LIBRO");

        System.out.println("Opciones de búsqueda:");
        System.out.println("1. Por ID");
        System.out.println("2. Por título");
        System.out.println("3. Por ISBN");
        System.out.println("4. Por género");
        System.out.println("5. Por año");
        System.out.print("\nSelecciona una opción: ");

        int opcion = leerOpcion();

        try {
            switch (opcion) {
                case 1:
                    buscarLibroPorId();
                    break;
                case 2:
                    buscarLibroPorTitulo();
                    break;
                case 3:
                    buscarLibroPorISBN();
                    break;
                case 4:
                    buscarLibroPorGenero();
                    break;
                case 5:
                    buscarLibroPorAño();
                    break;
                default:
                    System.out.println("Opción inválida.");
            }
        } catch (Exception e) {
            System.err.println("Error en búsqueda: " + e.getMessage());
        }

        pausar();
    }

    private static void buscarLibroPorId() {
        System.out.print("Ingresa el ID del libro: ");
        int id = leerEntero();

        Libro libro = libroDAO.buscarPorId(id);
        if (libro != null) {
            mostrarDetalleLibro(libro);
        } else {
            System.out.println("No se encontró libro con ID: " + id);
        }
    }

    private static void buscarLibroPorTitulo() {
        System.out.print("Ingresa parte del título: ");
        String titulo = scanner.nextLine().trim();

        if (titulo.isEmpty()) {
            System.out.println("❌ El título no puede estar vacío.");
            return;
        }

        List<Libro> libros = libroDAO.buscarPorTitulo(titulo);
        mostrarListaLibros(libros, "con título '" + titulo + "'");
    }

    private static void buscarLibroPorISBN() {
        System.out.print("Ingresa el ISBN: ");
        String isbn = scanner.nextLine().trim();

        if (isbn.isEmpty()) {
            System.out.println("❌ El ISBN no puede estar vacío.");
            return;
        }

        Libro libro = libroDAO.buscarPorIsbn(isbn);
        if (libro != null) {
            mostrarDetalleLibro(libro);
        } else {
            System.out.println("❌ No se encontró libro con ISBN: " + isbn);
        }
    }

    private static void buscarLibroPorGenero() {
        System.out.print("Ingresa el género: ");
        String genero = scanner.nextLine().trim();

        if (genero.isEmpty()) {
            System.out.println("El género no puede estar vacío.");
            return;
        }

        List<Libro> libros = libroDAO.buscarPorGenero(genero);
        mostrarListaLibros(libros, "de género '" + genero + "'");
    }

    private static void buscarLibroPorAño() {
        System.out.print("Ingresa el año: ");
        int año = leerEntero();

        List<Libro> libros = libroDAO.buscarPorAño(año);
        mostrarListaLibros(libros, "del año " + año);
    }

    private static void agregarLibro() {
        mostrarTitulo("➕ AGREGAR NUEVO LIBRO");

        try {
            // Primero mostrar autores disponibles
            List<Autor> autores = autorDAO.buscarTodos();
            if (autores.isEmpty()) {
                System.out.println("No hay autores registrados. Primero debes agregar un autor.");
                return;
            }

            System.out.println("Autores disponibles:");
            for (Autor autor : autores) {
                System.out.println("  " + autor.getId() + ". " + autor.getNombreCompleto());
            }
            System.out.println();

            System.out.print("Título: ");
            String titulo = scanner.nextLine().trim();

            System.out.print("ISBN (opcional): ");
            String isbn = scanner.nextLine().trim();
            if (isbn.isEmpty()) isbn = null;

            System.out.print("Género: ");
            String genero = scanner.nextLine().trim();

            System.out.print("Año de publicación (opcional): ");
            String añoStr = scanner.nextLine().trim();
            Year anoPublicacion = null;
            if (!añoStr.isEmpty()) {
                try {
                    anoPublicacion = Year.of(Integer.parseInt(añoStr));
                } catch (NumberFormatException e) {
                    System.out.println("Año inválido, se guardará sin año.");
                }
            }

            System.out.print("Número de páginas (opcional): ");
            String paginasStr = scanner.nextLine().trim();
            int paginas = 0;
            if (!paginasStr.isEmpty()) {
                try {
                    paginas = Integer.parseInt(paginasStr);
                } catch (NumberFormatException e) {
                    System.out.println("Número de páginas inválido, se usará 0.");
                }
            }

            System.out.print("ID del autor: ");
            int autorId = leerEntero();

            Autor autor = autorDAO.buscarPorId(autorId);
            if (autor == null) {
                System.out.println("No existe autor con ID: " + autorId);
                return;
            }

            if (titulo.isEmpty()) {
                System.out.println("El título es obligatorio.");
                return;
            }

            Libro nuevoLibro = new Libro(titulo, isbn, genero, anoPublicacion, paginas, null, autor);
            libroDAO.insertar(nuevoLibro);

            System.out.println("Libro agregado exitosamente:");
            mostrarDetalleLibro(nuevoLibro);

        } catch (Exception e) {
            System.err.println("Error agregando libro: " + e.getMessage());
        }

        pausar();
    }

    private static void modificarLibro() {
        mostrarTitulo("✏️ MODIFICAR LIBRO");

        System.out.print("Ingresa el ID del libro a modificar: ");
        int id = leerEntero();

        try {
            Libro libro = libroDAO.buscarPorId(id);
            if (libro == null) {
                System.out.println("No se encontró libro con ID: " + id);
                return;
            }

            System.out.println("Libro actual:");
            mostrarDetalleLibro(libro);
            System.out.println();
            System.out.println("Ingresa los nuevos datos (ENTER para mantener actual):");

            System.out.print("Título [" + libro.getTitulo() + "]: ");
            String titulo = scanner.nextLine().trim();
            if (!titulo.isEmpty()) {
                libro.setTitulo(titulo);
            }

            System.out.print("ISBN [" + (libro.getIsbn() != null ? libro.getIsbn() : "N/A") + "]: ");
            String isbn = scanner.nextLine().trim();
            if (!isbn.isEmpty()) {
                libro.setIsbn(isbn);
            }

            System.out.print("Género [" + libro.getGenero() + "]: ");
            String genero = scanner.nextLine().trim();
            if (!genero.isEmpty()) {
                libro.setGenero(genero);
            }

            String añoActual = libro.getAnoPublicacion() != null ?
                    libro.getAnoPublicacion().toString() : "N/A";
            System.out.print("Año [" + añoActual + "]: ");
            String añoStr = scanner.nextLine().trim();
            if (!añoStr.isEmpty()) {
                try {
                    libro.setAnoPublicacion(Year.of(Integer.parseInt(añoStr)));
                } catch (NumberFormatException e) {
                    System.out.println("Año inválido, se mantiene el anterior.");
                }
            }

            System.out.print("Páginas [" + libro.getPaginas() + "]: ");
            String paginasStr = scanner.nextLine().trim();
            if (!paginasStr.isEmpty()) {
                try {
                    libro.setPaginas(Integer.parseInt(paginasStr));
                } catch (NumberFormatException e) {
                    System.out.println("Número inválido, se mantiene el anterior.");
                }
            }

            // Cambiar autor (opcional)
            System.out.print("¿Cambiar autor? (S/N): ");
            String cambiarAutor = scanner.nextLine().trim().toLowerCase();
            if (cambiarAutor.equals("s") || cambiarAutor.equals("si")) {
                List<Autor> autores = autorDAO.buscarTodos();
                System.out.println("Autores disponibles:");
                for (Autor autor : autores) {
                    System.out.println("  " + autor.getId() + ". " + autor.getNombreCompleto());
                }
                System.out.print("ID del nuevo autor: ");
                int autorId = leerEntero();

                Autor nuevoAutor = autorDAO.buscarPorId(autorId);
                if (nuevoAutor != null) {
                    libro.setAutor(nuevoAutor);
                } else {
                    System.out.println("⚠Autor inválido, se mantiene el anterior.");
                }
            }

            libroDAO.actualizar(libro);

            System.out.println("Libro modificado exitosamente:");
            mostrarDetalleLibro(libro);

        } catch (Exception e) {
            System.err.println("Error modificando libro: " + e.getMessage());
        }

        pausar();
    }

    private static void eliminarLibro() {
        mostrarTitulo("🗑️ ELIMINAR LIBRO");

        System.out.print("Ingresa el ID del libro a eliminar: ");
        int id = leerEntero();

        try {
            Libro libro = libroDAO.buscarPorId(id);
            if (libro == null) {
                System.out.println("No se encontró libro con ID: " + id);
                return;
            }

            System.out.println("Libro a eliminar:");
            mostrarDetalleLibro(libro);

            System.out.print("¿Estás seguro de eliminar este libro? (S/N): ");
            String confirmacion = scanner.nextLine().trim().toLowerCase();

            if (confirmacion.equals("s") || confirmacion.equals("si")) {
                libroDAO.eliminar(id);
                System.out.println("Libro eliminado exitosamente.");
            } else {
                System.out.println("Eliminación cancelada.");
            }

        } catch (Exception e) {
            System.err.println("Error eliminando libro: " + e.getMessage());
        }

        pausar();
    }

    private static void librosPorAutor() {
        mostrarTitulo("📖 LIBROS POR AUTOR");

        List<Autor> autores = autorDAO.buscarTodos();
        if (autores.isEmpty()) {
            System.out.println("📭 No hay autores registrados.");
            pausar();
            return;
        }

        System.out.println("Selecciona un autor por ID:");
        for (Autor autor : autores) {
            System.out.println("  " + autor.getId() + ". " + autor.getNombreCompleto());
        }
        System.out.print("ID del autor: ");
        int id = leerEntero();

        Autor autor = autorDAO.buscarPorId(id);
        if (autor == null) {
            System.out.println("Autor no encontrado.");
            pausar();
            return;
        }

        List<Libro> libros = libroDAO.buscarPorAutor(autor);
        mostrarListaLibros(libros, "del autor " + autor.getNombreCompleto());
        pausar();
    }

    private static void librosPorGenero() {
        mostrarTitulo("📚 LIBROS POR GÉNERO");
        System.out.print("Ingresa el género: ");
        String genero = scanner.nextLine().trim();

        if (genero.isEmpty()) {
            System.out.println("El género no puede estar vacío.");
            pausar();
            return;
        }

        List<Libro> libros = libroDAO.buscarPorGenero(genero);
        mostrarListaLibros(libros, "del género '" + genero + "'");
        pausar();
    }

    private static void librosPorAño() {
        mostrarTitulo("📅 LIBROS POR AÑO");
        System.out.print("Ingresa el año: ");
        int año = leerEntero();

        List<Libro> libros = libroDAO.buscarPorAño(año);
        mostrarListaLibros(libros, "del año " + año);
        pausar();
    }

    private static void estadisticasGenerales() {
        mostrarTitulo("📊 ESTADÍSTICAS GENERALES");

        int totalAutores = autorDAO.buscarTodos().size();
        int totalLibros = libroDAO.buscarTodos().size();

        System.out.println("👥 Total autores: " + totalAutores);
        System.out.println("📚 Total libros: " + totalLibros);
        // Podés extender con más consultas: promedio de páginas, autores por país, etc.

        pausar();
    }

    // =================================================================
    // MÉTODOS AUXILIARES
    // =================================================================

    private static void mostrarDetalleAutor(Autor autor) {
        System.out.println("ID: " + autor.getId());
        System.out.println("Nombre: " + autor.getNombre());
        System.out.println("Apellido: " + autor.getApellido());
        System.out.println("Nacionalidad: " + autor.getNacionalidad());
        System.out.println("Fecha de nacimiento: " +
                (autor.getFechaNacimiento() != null ? autor.getFechaNacimiento() : "N/A"));
    }

    private static void mostrarDetalleLibro(Libro libro) {
        System.out.println("ID: " + libro.getId());
        System.out.println("Título: " + libro.getTitulo());
        System.out.println("ISBN: " + (libro.getIsbn() != null ? libro.getIsbn() : "N/A"));
        System.out.println("Género: " + libro.getGenero());
        System.out.println("Año: " + (libro.getAnoPublicacion() != null ? libro.getAnoPublicacion() : "N/A"));
        System.out.println("Páginas: " + libro.getPaginas());
        System.out.println("Autor: " + (libro.getAutor() != null ? libro.getAutor().getNombreCompleto() : "N/A"));
    }

    private static void mostrarListaAutores(List<Autor> autores, String criterio) {
        if (autores.isEmpty()) {
            System.out.println("📭 No se encontraron autores " + criterio + ".");
        } else {
            System.out.println("Encontrados " + autores.size() + " autores " + criterio + ":");
            for (Autor autor : autores) {
                System.out.println("  [" + autor.getId() + "] " + autor.getNombreCompleto());
            }
        }
    }

    private static void mostrarListaLibros(List<Libro> libros, String criterio) {
        if (libros.isEmpty()) {
            System.out.println("📭 No se encontraron libros " + criterio + ".");
        } else {
            System.out.println("Encontrados " + libros.size() + " libros " + criterio + ":");
            for (Libro libro : libros) {
                System.out.println("  [" + libro.getId() + "] " + libro.getTitulo() +
                        " (" + (libro.getAutor() != null ? libro.getAutor().getNombreCompleto() : "Autor desconocido") + ")");
            }
        }
    }

    private static void mostrarTitulo(String titulo) {
        limpiarPantalla();
        System.out.println("═══════════════════════════════════════════════════════════════");
        System.out.println(" " + titulo);
        System.out.println("═══════════════════════════════════════════════════════════════");
    }

    private static void limpiarPantalla() {
        System.out.print("\033[H\033[2J");
        System.out.flush();
    }

    public static void pausar() {
        System.out.println("Presiona ENTER para continuar...");
        try {
            if (System.console() != null) {
                System.console().readLine();
            } else {
                new Scanner(System.in).nextLine();
            }
        } catch (Exception e) {
        }
    }

    private static int leerOpcion() {
        try {
            if (scanner.hasNextLine()) {
                return Integer.parseInt(scanner.nextLine().trim());
            } else {
                // No hay entrada disponible (ej. ejecutando con Gradle run)
                return -1;
            }
        } catch (NumberFormatException e) {
            return -1;// Entrada inválida (ej. letras en vez de número)
        }
    }

    private static int leerEntero() {
        while (true) {
            try {
                return Integer.parseInt(scanner.nextLine().trim());
            } catch (NumberFormatException e) {
                System.out.print("Ingresa un número válido: ");
            }
        }
    }

    private static String truncar(String texto, int max) {
        if (texto == null) return "";
        return texto.length() <= max ? texto : texto.substring(0, max - 3) + "...";
    }
}