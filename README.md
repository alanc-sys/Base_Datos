# Sistema de Gestión de Librería

Este es un sistema de gestión de librería simple desarrollado en Java. Permite gestionar autores y libros a través de una interfaz de línea de comandos (CLI).

## Características

*   Gestión de Autores (CRUD - Crear, Leer, Actualizar, Eliminar)
*   Gestión de Libros (CRUD - Crear, Leer, Actualizar, Eliminar)
*   Búsqueda de libros y autores por diferentes criterios.
*   Interfaz de línea de comandos interactiva.

## Prerrequisitos

*   Java 11 o superior
*   Docker y Docker Compose
*   Gradle

## Primeros Pasos

Sigue estas instrucciones para tener una copia del proyecto funcionando en tu máquina local para desarrollo y pruebas.

### 1. Configuración de la Base de Datos

El proyecto utiliza MySQL como base de datos. La forma más sencilla de levantar una instancia de la base de datos es utilizando Docker.

1.  **Crear el archivo `.env`:**
    Crea un archivo llamado `.env` en la raíz del proyecto con el siguiente contenido:
    ```
    MYSQL_DATABASE=libreria
    MYSQL_ROOT_PASSWORD=password
    ```

2.  **Levantar el contenedor de Docker:**
    Abre una terminal en la raíz del proyecto y ejecuta:
    ```bash
    docker-compose up -d
    ```
    Esto levantará un contenedor de MySQL con una base de datos llamada `libreria`.

### 2. Ejecutar la aplicación

Una vez que la base de datos esté corriendo, puedes ejecutar la aplicación utilizando Gradle.

1.  **Construir el proyecto:**
    ```bash
    gradle build
    ```

2.  **Inicializar la base de datos:**
    La primera vez que ejecutes la aplicación, necesitas inicializar la base de datos. Esto creará las tablas e insertará algunos datos de ejemplo.
    ```bash
    gradle runDbSetup
    ```

3.  **Ejecutar la aplicación de la librería:**
    Para iniciar la aplicación principal de la librería, ejecuta:
    ```bash
    gradle run
    ```

## Ejecutar los tests

Para ejecutar los tests, utiliza el siguiente comando de Gradle:
```bash
gradle test
```

## Construido con

*   [Java](https://www.java.com/) - El lenguaje de programación utilizado.
*   [Gradle](https://gradle.org/) - El sistema de automatización de compilación.
*   [MySQL](https://www.mysql.com/) - El sistema de gestión de bases de datos.
*   [Docker](https://www.docker.com/) - La plataforma de contenedores.
*   [JUnit 5](https://junit.org/junit5/) - El framework de testing.
