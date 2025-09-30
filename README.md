# 📚 Sistema de Gestión de Librería

> **Un viaje de aprendizaje desde JDBC básico hasta una aplicación profesional con Docker**

[![Java](https://img.shields.io/badge/Java-17+-orange?style=for-the-badge&logo=java)](https://www.oracle.com/java/)
[![MySQL](https://img.shields.io/badge/MySQL-8.0-blue?style=for-the-badge&logo=mysql)](https://www.mysql.com/)
[![Docker](https://img.shields.io/badge/Docker-Ready-2496ED?style=for-the-badge&logo=docker)](https://www.docker.com/)
[![Gradle](https://img.shields.io/badge/Gradle-8.x-02303A?style=for-the-badge&logo=gradle)](https://gradle.org/)

---

## 🎯 Sobre Este Proyecto

Este proyecto nació como un ejercicio de aprendizaje para demostrar conocimientos en bases de datos y Java. Sin embargo, evolucionó hacia una aplicación completa que implementa  buenas prácticas de desarrollo y despliegue con Docker.

**No es solo un CRUD más.** Es la documentación de mi proceso de aprendizaje, desde entender qué es un `PreparedStatement` hasta containerizar una aplicación completa.

---

## 📖 Tabla de Contenidos

- [Mi Viaje de Aprendizaje](#-mi-viaje-de-aprendizaje)
- [¿Qué hace esta aplicación?](#-qué-hace-esta-aplicación)
- [Arquitectura del Sistema](#-arquitectura-del-sistema)
- [Tecnologías y Decisiones Técnicas](#-tecnologías-y-decisiones-técnicas)
- [Conceptos Fundamentales que Aprendí](#-conceptos-fundamentales-que-aprendí)
- [Instalación y Uso](#-instalación-y-uso)
- [Estructura del Proyecto](#-estructura-del-proyecto)
- [Capturas de Pantalla](#-capturas-de-pantalla)
- [Roadmap Futuro](#-roadmap-futuro)
- [Reflexiones Finales](#-reflexiones-finales)

---

## 🚀 Mi Viaje de Aprendizaje

### **Fase 1: Los Fundamentos (Semana 1)**
#### *"¿Cómo diablos conecto Java con MySQL?"*

**Desafíos iniciales:**
- Entender qué es JDBC y por qué existe
- Primera conexión exitosa a MySQL (¡después de muchos errores!)
- Diferencia entre `Statement` y `PreparedStatement`

**Lo que construí:**
```java
// Mi primera consulta SQL desde Java 🎉
Connection conn = DriverManager.getConnection(url, user, pass);
Statement stmt = conn.createStatement();
ResultSet rs = stmt.executeQuery("SELECT * FROM autores");
```

**Lecciones clave:**
- ✅ Por qué `PreparedStatement` es crítico (SQL Injection 101)
- ✅ `try-with-resources` para evitar memory leaks
- ✅ La importancia de cerrar conexiones

**Commit relevante:** `Initial project structure`

---

### **Fase 2: Entendiendo Relaciones (Semana 2)**
#### *"Un libro tiene un autor... ¿cómo lo represento?"*

**Desafíos:**
- Primary Keys vs Foreign Keys: ¿cuál es la diferencia?
- ¿Por qué el ISBN es `VARCHAR` y no `INT`?
- Mi primer `INNER JOIN` exitoso

**Lo que construí:**
```sql
CREATE TABLE libros (
    id INT PRIMARY KEY AUTO_INCREMENT,
    titulo VARCHAR(200) NOT NULL,
    isbn VARCHAR(13) UNIQUE,  -- ¡Entendí por qué VARCHAR!
    autor_id INT NOT NULL,
    FOREIGN KEY (autor_id) REFERENCES autores(id)  -- ¡Mi primera FK!
);
```

**Momento "AHA":**
> "Las Foreign Keys no son solo para conectar tablas, son **guardianes de integridad de datos**. MySQL no me deja borrar un autor que tiene libros... ¡y eso está bien!"

**Lecciones clave:**
- ✅ Integridad referencial y por qué importa
- ✅ Tipos de datos: cuándo usar `VARCHAR` vs `INT`
- ✅ `UNIQUE` constraints para reglas de negocio

**Commit relevante:** `Add database schema and sample data`

---

### **Fase 3: El Patrón DAO (Semana 3)**
#### *"¿Por qué mis clases Java tienen tanto código SQL mezclado?"*

**Desafíos:**
- Entender separación de responsabilidades
- ¿Qué es un patrón de diseño y por qué usarlo?
- Interfaces vs Implementaciones: el concepto me costó

**Lo que construí:**
```java
// Antes: SQL mezclado por todos lados 😵
public void guardarLibro(String titulo, int autorId) {
    // SQL directo aquí...
}

// Después: Patrón DAO limpio y organizado 🎯
public interface LibroDAO {
    void insertar(Libro libro);
    Libro buscarPorId(int id);
    List<Libro> buscarTodos();
}

public class LibroDAOImpl implements LibroDAO {
    // Toda la lógica SQL encapsulada aquí
}
```

**Momento "AHA":**
> "El patrón DAO no es solo organización, es **cambiar la base de datos sin tocar el resto del código**. Si mañana cambio a PostgreSQL, solo modifico el DAO."

**Lecciones clave:**
- ✅ Separación de capas: Model, DAO, Controller
- ✅ Composición de DAOs (LibroDAO necesita AutorDAO)
- ✅ Mapeo objeto-relacional manual

---

### **Fase 4: Relaciones Complejas (Semana 4)**
#### *"Un libro pertenece a un autor... ¿cómo traigo ambos con una sola query?"*

**Desafíos:**
- Mi primer `INNER JOIN` con múltiples tablas
- Mapear `ResultSet` a objetos anidados (Libro contiene Autor)
- Validaciones en cascada

**Lo que construí:**
```java
// Query con JOIN para traer datos completos
String sql = """
    SELECT l.id, l.titulo, l.isbn,
           a.id as autor_id, a.nombre as autor_nombre, a.apellido as autor_apellido
    FROM libros l
    INNER JOIN autores a ON l.autor_id = a.id
    WHERE l.id = ?
    """;

// Mapeo complejo: ResultSet → Objeto Libro (con Autor dentro)
private Libro mapearResultSetALibroCompleto(ResultSet rs) throws SQLException {
    // Extraer datos del libro
    // Extraer datos del autor
    // Crear objeto Autor
    // Crear objeto Libro con Autor
    return new Libro(..., autor);
}
```

**Momento "AHA":**
> "Un JOIN no es magia, es simplemente **pegar** dos tablas por una columna común. Pero hacerlo bien (con aliases correctos) hace la diferencia entre código legible y un desastre."

**Lecciones clave:**
- ✅ JOINs: INNER vs LEFT y cuándo usar cada uno
- ✅ Aliases en SQL para evitar ambigüedad
- ✅ Composición de objetos en Java

**Commit relevante:** `Add complex joins and object mapping`

---

### **Fase 5: Profesionalización (Semana 5)**
#### *"¿Cómo hacen los profesionales para que esto sea reproducible?"*

**Desafíos:**
- Hardcodear passwords está mal... ¿pero cómo externalizarlo?
- ¿Docker? ¿Por qué todos hablan de Docker?
- Testing: todos dicen que es importante, ¿cómo empiezo?

**Lo que construí:**

#### **1. Externalización de configuración**
```java
// Antes: Hardcoded 😱
private static final String PASSWORD = "root123";

// Después: Properties file 🎯
// application.properties
db.url=jdbc:mysql://localhost:3306/libreria
db.user=root
db.password=${DB_PASSWORD}  // Variable de entorno
```

#### **2. Dockerización**
```yaml
# docker-compose.yml
services:
  mysql:
    image: mysql:8.0
    environment:
      MYSQL_DATABASE: libreria
      MYSQL_ROOT_PASSWORD: ${MYSQL_ROOT_PASSWORD}
    ports:
      - "3306:3306"
```

#### **3. Testing con JUnit**
```java
@Test
public void testCrearAutor() {
    Autor autor = new Autor("Gabriel", "García Márquez", "Colombiana", ...);
    assertEquals("Gabriel", autor.getNombre());
}
```

**Momento "AHA":**
> "Docker no es solo 'una moda'. Es **la diferencia entre 'en mi máquina funciona' y 'funciona en todas las máquinas'**. Un `docker-compose up` y cualquiera puede ejecutar mi proyecto."

**Lecciones clave:**
- ✅ Configuración externa vs hardcoded
- ✅ Containerización para reproducibilidad
- ✅ Testing automatizado desde el inicio
- ✅ Variables de entorno para secretos

**Commits relevantes:** 
- `Externalize database configuration`
- `Add Docker Compose setup`
- `Add JUnit tests`

---

## 💡 ¿Qué hace esta aplicación?

Sistema CRUD completo para gestionar una librería con:

### **Funcionalidades Principales:**

#### 👥 **Gestión de Autores**
- ➕ Agregar autores con validación de campos
- 🔍 Buscar por ID, nombre, nacionalidad
- ✏️ Modificar información existente
- 🗑️ Eliminar (con validación de libros asociados)

#### 📚 **Gestión de Libros**
- ➕ Agregar libros con relación a autor
- 🔍 Buscar por ID, título, ISBN, género, año
- ✏️ Actualizar incluyendo cambio de autor
- 🗑️ Eliminar con confirmación

#### 📊 **Reportes y Consultas**
- 📖 Libros por autor (con JOIN)
- 🎭 Libros por género
- 📅 Libros por año o rango de años
- 📈 Estadísticas generales

---

## 🏗️ Arquitectura del Sistema

### **Diagrama de Capas**
```
┌─────────────────────────────────────────────┐
│        CAPA DE PRESENTACIÓN                 │
│   (LibreriaApp - Interfaz de Consola)       │
└─────────────────────────────────────────────┘
                    ↓
┌─────────────────────────────────────────────┐
│         CAPA DE LÓGICA (DAO)                │
│   AutorDAOImpl    │    LibroDAOImpl         │
│      (CRUD)       │       (CRUD + JOINs)    │
└─────────────────────────────────────────────┘
                    ↓
┌─────────────────────────────────────────────┐
│          CAPA DE DATOS (JDBC)               │
│   DatabaseConnection (PreparedStatement)    │
└─────────────────────────────────────────────┘
                    ↓
┌─────────────────────────────────────────────┐
│      BASE DE DATOS (MySQL 8.0)              │
│   Tablas: autores, libros (con FK)          │
└─────────────────────────────────────────────┘
```

### **Flujo de una Operación (Ejemplo: Buscar Libro)**
```
Usuario → LibreriaApp.buscarLibro()
           ↓
        LibroDAO.buscarPorId(id)
           ↓
        DatabaseConnection.getConnection()
           ↓
        PreparedStatement con Query SQL
           ↓
        ResultSet con datos de MySQL
           ↓
        mapearResultSetALibroCompleto()
           ↓
        Objeto Libro (con Autor anidado)
           ↓
        Mostrar en pantalla
```

---

## 🛠️ Tecnologías y Decisiones Técnicas

### **Stack Tecnológico**

| Tecnología | Versión | ¿Por qué la elegí? |
|------------|---------|---------------------|
| **Java** | 17+ | LTS, moderno, con features útiles (text blocks) |
| **MySQL** | 8.0 | Base de datos relacional robusta, ampliamente usada |
| **JDBC** | Native | Para entender los fundamentos antes de ORMs |
| **Gradle** | 8.x | Build automation más moderno que Maven |
| **Docker** | Latest | Reproducibilidad y portabilidad |
| **JUnit** | 5 | Testing estándar de la industria |

### **Decisiones Técnicas Importantes**

#### **1. ¿Por qué JDBC puro y no Spring Boot?**

**Decisión:** Empezar con JDBC nativo.

**Razón:**
> "Antes de usar Spring Data JPA (que genera SQL automáticamente), necesitaba **entender cómo funciona SQL por debajo**. Es como aprender a conducir manual antes que automático."

**Trade-off:**
- ✅ **Pro:** Comprensión profunda de SQL, PreparedStatement, transacciones
- ❌ **Con:** Más código boilerplate que con un ORM

**Próximo paso:** Migrar a Spring Boot + JPA cuando entienda bien los fundamentos.

---

#### **2. ¿Por qué PreparedStatement en vez de Statement?**

**Decisión:** Usar `PreparedStatement` exclusivamente.

**Razón:**
```java
// ❌ Statement (vulnerable a SQL Injection):
String sql = "SELECT * FROM users WHERE email = '" + userInput + "'";
// Si userInput = "'; DROP TABLE users; --" → ¡Desastre!

// ✅ PreparedStatement (seguro):
PreparedStatement pstmt = conn.prepareStatement("SELECT * FROM users WHERE email = ?");
pstmt.setString(1, userInput);  // Escapado automáticamente
```

**Trade-off:**
- ✅ **Pro:** Seguridad contra SQL Injection, mejor performance (pre-compilado)
- ❌ **Con:** Sintaxis ligeramente más verbosa

---

#### **3. ¿Por qué el Patrón DAO?**

**Decisión:** Separar lógica de datos en clases DAO.

**Razón:**
> "Si mañana cambio de MySQL a PostgreSQL, solo modifico los DAOs. El resto de la aplicación no se entera del cambio."

**Estructura:**
```
Interface (contrato) → AutorDAO.java
    ↓
Implementación → AutorDAOImpl.java
```

**Trade-off:**
- ✅ **Pro:** Separación de responsabilidades, testeable, mantenible
- ❌ **Con:** Más clases que gestionar

---

#### **4. ¿Por qué Docker Compose?**

**Decisión:** Incluir `docker-compose.yml` para MySQL.

**Razón:**
> "Instalé MySQL en mi máquina y funcionó. Mi compañero lo intentó y tuvo 20 errores de versión. Docker soluciona esto."

**Resultado:**
```bash
# Antes: "Instala MySQL 8.0, crea usuario, configura puerto..."
# Ahora:
docker-compose up -d
# ¡Listo! MySQL corriendo en 30 segundos.
```

---

## 📚 Conceptos Fundamentales que Aprendí

### **1. JDBC y Conexiones**

#### **¿Qué es JDBC?**
> Java Database Connectivity - El puente entre Java y bases de datos relacionales.

**Concepto clave: Connection Pool**
```java
// Cada conexión es costosa
Connection conn = DriverManager.getConnection(url, user, pass);  // ⏱️ 100ms

// En producción se usa un pool:
// - Mantiene conexiones abiertas
// - Las reutiliza en lugar de crear nuevas
// - Configuración: HikariCP, Apache DBCP
```

**Lo que aprendí:**
- Abrir/cerrar conexiones es costoso
- `try-with-resources` evita memory leaks
- Las conexiones deben cerrarse SIEMPRE

---

### **2. SQL Injection y PreparedStatement**

#### **El ataque más común del mundo**

```java
// ❌ Código vulnerable:
String email = request.getParameter("email");  // Del usuario
String sql = "SELECT * FROM usuarios WHERE email = '" + email + "'";

// 🔥 Si usuario ingresa: ' OR '1'='1
// SQL resultante: SELECT * FROM usuarios WHERE email = '' OR '1'='1'
// Resultado: Devuelve TODOS los usuarios (bypass de autenticación)
```

**Solución: PreparedStatement**
```java
PreparedStatement pstmt = conn.prepareStatement(
    "SELECT * FROM usuarios WHERE email = ?"
);
pstmt.setString(1, email);  // ← MySQL escapa caracteres peligrosos
```

---

### **3. Primary Key vs Foreign Key**

#### **Primary Key (Clave Primaria)**
```sql
CREATE TABLE autores (
    id INT PRIMARY KEY AUTO_INCREMENT,  -- ← Identificador único
    nombre VARCHAR(100)
);
```

**Características:**
- ✅ Identifica UNA fila única
- ✅ No puede ser NULL
- ✅ No puede repetirse
- ✅ Solo UNA por tabla

#### **Foreign Key (Clave Foránea)**
```sql
CREATE TABLE libros (
    id INT PRIMARY KEY,
    titulo VARCHAR(200),
    autor_id INT,  -- ← Referencia a autores.id
    FOREIGN KEY (autor_id) REFERENCES autores(id)
);
```

**Características:**
- ✅ Referencia a PRIMARY KEY de otra tabla
- ✅ Puede repetirse (muchos libros, un autor)
- ✅ Garantiza integridad referencial
- ✅ Puede ser NULL (si es opcional)

**Analogía:**
> Primary Key = DNI (identifica persona única)
> Foreign Key = "Mi jefe es Juan" (referencia a otra persona)

---

### **4. INNER JOIN - Conectando Tablas**

#### **El problema:**
```
Tabla libros:
| id | titulo           | autor_id |
|----|------------------|----------|
| 1  | Cien años...     | 1        |

Tabla autores:
| id | nombre          |
|----|-----------------|
| 1  | García Márquez  |
```

**Pregunta:** ¿Cómo obtengo título Y nombre del autor en una sola query?

**Respuesta: INNER JOIN**
```sql
SELECT l.titulo, a.nombre
FROM libros l
INNER JOIN autores a ON l.autor_id = a.id;

-- Resultado:
-- "Cien años de soledad" | "García Márquez"
```

**Visualización:**
```
libros.autor_id = 1  ←→  autores.id = 1
       ↓                        ↓
  "Cien años..."        "García Márquez"
       ↓                        ↓
    Se "pegan" en la query resultante
```

---

### **5. Tipos de Datos: ¿Cuándo usar qué?**

#### **ISBN: ¿VARCHAR o INT?**

**Decisión:** `VARCHAR(13)`

**Razón:**
```sql
-- ISBN real: 978-0-7475-3269-6
-- Contiene:
--   - Guiones (no numérico)
--   - Puede tener "X" como check digit
--   - Ceros iniciales importantes (0-7475)

-- Con INT: 7475326906 (❌ perdió el 0 inicial)
-- Con VARCHAR: "978-0-7475-3269-6" (✅ formato correcto)
```

**Regla de oro:**
> Si NO vas a hacer matemática con el número → VARCHAR

**Ejemplos:**
- Teléfono: `VARCHAR` (tiene +, -, espacios)
- Código postal: `VARCHAR` (algunos países usan letras)
- Edad: `INT` (sumas, promedios, comparaciones)
- Precio: `DECIMAL` (matemática con precisión)

---

### **6. Patrón DAO (Data Access Object)**

#### **¿Qué problema resuelve?**

**Sin DAO (caos):**
```java
public class LibroService {
    public void guardarLibro(Libro libro) {
        // SQL mezclado con lógica de negocio
        Connection conn = ...;
        PreparedStatement pstmt = ...;
        // 50 líneas de código JDBC aquí
    }
    
    public Libro buscarLibro(int id) {
        // Más SQL mezclado
        // Otra vez: Connection, PreparedStatement, ResultSet...
    }
}
```

**Con DAO (organizado):**
```java
// Interface: Define QUÉ operaciones hay
public interface LibroDAO {
    void insertar(Libro libro);
    Libro buscarPorId(int id);
    List<Libro> buscarTodos();
}

// Implementación: Define CÓMO se hacen
public class LibroDAOImpl implements LibroDAO {
    // TODO el código JDBC encapsulado aquí
}

// Uso:
LibroDAO libroDAO = new LibroDAOImpl();
Libro libro = libroDAO.buscarPorId(1);  // ← Limpio y claro
```

**Beneficios:**
- 🎯 **Separación:** Lógica de negocio vs Acceso a datos
- 🔄 **Cambiable:** Cambias implementación sin tocar el resto
- 🧪 **Testeable:** Puedes mockear el DAO para tests

---

### **7. try-with-resources**

#### **El problema:**
```java
// ❌ Forma antigua (peligrosa):
Connection conn = null;
try {
    conn = DatabaseConnection.getConnection();
    // ... usar conexión ...
} catch (SQLException e) {
    // ...
} finally {
    if (conn != null) {
        conn.close();  // ¿Y si esto también falla?
    }
}
```

#### **La solución:**
```java
// ✅ try-with-resources (Java 7+):
try (Connection conn = DatabaseConnection.getConnection();
     PreparedStatement pstmt = conn.prepareStatement(sql)) {
    
    // ... usar conexión ...
    
} // ← Se cierran automáticamente, incluso si hay excepción
```

**¿Qué pasa si NO cierras conexiones?**
1. **Memory leak** - RAM se consume gradualmente
2. **Connection pool exhausted** - MySQL rechaza nuevas conexiones
3. **Aplicación se cuelga** - No puede hacer más queries

---

## 🚀 Instalación y Uso

### **Prerequisitos**
- Java 17 o superior
- Docker y Docker Compose
- Git

### **Opción 1: Con Docker (Recomendado)**

```bash
# 1. Clonar el repositorio
git clone https://github.com/tu-usuario/libreria-bootcamp.git
cd libreria-bootcamp

# 2. Configurar variables de entorno
cp .env.example .env
# Editar .env con tus credenciales

# 3. Levantar MySQL con Docker
docker-compose up -d

# 4. Inicializar base de datos
./gradlew run --args="setup"

# 5. Ejecutar aplicación
./gradlew run
```

### **Opción 2: MySQL Local**

```bash
# 1. Clonar repositorio
git clone https://github.com/tu-usuario/libreria-bootcamp.git
cd libreria-bootcamp

# 2. Configurar application.properties
# Editar src/main/resources/application.properties

# 3. Crear base de datos manualmente
mysql -u root -p
CREATE DATABASE libreria;

# 4. Ejecutar aplicación
./gradlew run
```

### **Ejecutar Tests**

```bash
# Todos los tests
./gradlew test

# Tests específicos
./gradlew test --tests TestAutorDAO
```
---
```

---

## 💭 Reflexiones Finales

### **¿Qué aprendí realmente?**

Este proyecto no es solo sobre CRUD. Es sobre:

1. **Fundamentos sólidos:** Entendí SQL, no solo lo copié.
2. **Pensamiento arquitectural:** Por qué separar en capas.
3. **Seguridad desde el inicio:** SQL Injection no es solo teoría.
4. **Herramientas profesionales:** Docker, Gradle, JUnit.
5. **Evolución iterativa:** De simple a complejo, paso a paso.

### **¿Qué haría diferente?**

- **Empezar con TDD:** Tests desde el día 1, no al final.
- **Logging desde el inicio:** Menos `System.out.println()`, más logging estructurado.
- **Documentar decisiones:** Llevar un diario de desarrollo.

### **Mensaje para reclutadores**

> "No busco impresionar con frameworks complejos. Busco demostrar que **entiendo los fundamentos**. Antes de usar Spring Data JPA (que genera SQL automáticamente), quise entender **cómo funciona SQL por debajo**.
>
> Este proyecto muestra mi proceso de aprendizaje: de conexiones manuales a Docker, de Statement a PreparedStatement, de código espagueti a arquitectura en capas.
>
> **No es el proyecto más grande, pero es el proyecto donde más aprendí.**"

---

## 🤝 Contacto

**Desarrollador:** Alan
- 📧 Email: alancruz.sys@gmail.com
- 💼 LinkedIn: https://www.linkedin.com/in/alancruz-sys/
- 🐙 GitHub: https://github.com/alanc-sys

---

<div align="center">

**⭐ Si este proyecto te ayudó a entender JDBC, dale una estrella ⭐**

Hecho con ☕ y muchas horas de debugging

</div>
---

## 🐛 Debugging y Problemas Comunes

### **Problema 1: "No suitable driver found for jdbc:mysql"**

**Síntoma:**
```
SQLException: No suitable driver found for jdbc:mysql://localhost:3306/libreria
```

**Solución:**
```bash
# Verificar que el driver MySQL esté en dependencias
# En build.gradle:
dependencies {
    implementation 'mysql:mysql-connector-java:8.0.33'
}

# Recargar dependencias
./gradlew clean build
```

**Lección aprendida:** El driver JDBC debe estar en el classpath.

---

### **Problema 2: "Communications link failure"**

**Síntoma:**
```
The last packet sent successfully to the server was 0 milliseconds ago
```

**Posibles causas y soluciones:**

1. **MySQL no está corriendo**
```bash
# Verificar
docker ps  # ¿Está el contenedor mysql_libreria?

# Iniciar
docker-compose up -d
```

2. **Puerto incorrecto**
```properties
# application.properties
db.url=jdbc:mysql://localhost:3306/libreria  # ← Puerto correcto
```

3. **Firewall bloqueando**
```bash
# Verificar conectividad
telnet localhost 3306
```

---

### **Problema 3: "Access denied for user 'root'@'localhost'"**

**Síntoma:**
```
java.sql.SQLException: Access denied for user 'root'@'localhost' (using password: YES)
```

**Solución:**
```bash
# Verificar credenciales en .env
MYSQL_ROOT_PASSWORD=tu_password_correcta

# Recrear contenedor con nueva contraseña
docker-compose down -v  # ← -v elimina volúmenes
docker-compose up -d
```

**Lección aprendida:** Las variables de entorno deben coincidir entre Docker y application.properties.

---

### **Problema 4: Foreign Key constraint fails**

**Síntoma:**
```
Cannot add or update a child row: a foreign key constraint fails
```

**Causa:** Intentas insertar un libro con `autor_id` que no existe.

**Solución:**
```java
// Validar antes de insertar
if (!autorDAO.existe(libro.getAutor().getId())) {
    throw new IllegalArgumentException("El autor no existe");
}
```
---

## 🎯 Preguntas Frecuentes (FAQ)

### **¿Por qué no usar Spring Boot?**

**Respuesta corta:** Para entender los fundamentos primero.

**Respuesta larga:**
Spring Boot es increíble, pero abstrae mucho:
```java
// Con Spring Data JPA:
@Repository
public interface LibroRepository extends JpaRepository<Libro, Long> {
    // ¡Eso es todo! Spring genera todo el SQL automáticamente
}

// Con JDBC (lo que aprendí):
public class LibroDAOImpl implements LibroDAO {
    public Libro buscarPorId(int id) {
        String sql = "SELECT * FROM libros WHERE id = ?";
        // Entiendo exactamente QUÉ SQL se ejecuta
        // Entiendo CÓMO funciona PreparedStatement
        // Entiendo el mapeo ResultSet → Objeto
    }
}
```

Después de este proyecto, **entiendo qué hace Spring por debajo**. Ahora cuando use Spring, sabré qué magia está haciendo.

---

### **¿Por qué usar interfaces (DAO) si solo tengo una implementación?**

**Pregunta válida:** "Si solo tengo `LibroDAOImpl`, ¿para qué la interface `LibroDAO`?"

**Respuesta:**
```java
// Ventaja 1: Testing
public class LibroServiceTest {
    @Test
    public void testBuscarLibro() {
        // Puedo crear un "fake" DAO para tests
        LibroDAO fakeDAO = new LibroDAOFake();  // ← No usa BD real
        LibroService service = new LibroService(fakeDAO);
        // Test sin depender de MySQL
    }
}

// Ventaja 2: Cambiar implementación
LibroDAO libroDAO;
if (usePostgreSQL) {
    libroDAO = new LibroDAOPostgreSQLImpl();
} else {
    libroDAO = new LibroDAOMySQLImpl();
}
```

**Principio:** Program to an interface, not an implementation.

---

### **¿Cómo manejo transacciones?**

**Escenario:** Quiero insertar autor Y libro en una sola operación atómica.

```java
public void insertarAutorConLibros(Autor autor, List<Libro> libros) {
    Connection conn = null;
    try {
        conn = DatabaseConnection.getConnection();
        conn.setAutoCommit(false);  // ← Iniciar transacción
        
        // 1. Insertar autor
        autorDAO.insertar(autor, conn);  // ← Usar misma conexión
        
        // 2. Insertar cada libro
        for (Libro libro : libros) {
            libro.setAutor(autor);
            libroDAO.insertar(libro, conn);
        }
        
        conn.commit();  // ← Todo bien, confirmar cambios
        System.out.println("✅ Transacción exitosa");
        
    } catch (SQLException e) {
        if (conn != null) {
            try {
                conn.rollback();  // ← Error, revertir TODO
                System.out.println("❌ Transacción revertida");
            } catch (SQLException ex) {
                // Log error
            }
        }
    } finally {
        if (conn != null) {
            try {
                conn.setAutoCommit(true);  // Restaurar
                conn.close();
            } catch (SQLException e) {
                // Log error
            }
        }
    }
}
```

**Lección:** Transacciones = TODO o NADA.

---

### **¿Cómo escalo esto a producción?**

**Cambios necesarios:**

1. **Connection Pooling**
```java
// En lugar de crear conexiones cada vez:
HikariConfig config = new HikariConfig();
config.setJdbcUrl("jdbc:mysql://localhost:3306/libreria");
config.setUsername("root");
config.setPassword("password");
config.setMaximumPoolSize(10);  // ← Pool de 10 conexiones

HikariDataSource dataSource = new HikariDataSource(config);
```

2. **Logging Estructurado**
```java
// En lugar de System.out.println:
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

private static final Logger logger = LoggerFactory.getLogger(LibroDAOImpl.class);
logger.info("Libro insertado con ID: {}", libro.getId());
logger.error("Error insertando libro", e);
```

3. **Configuración por Ambiente**
```yaml
# application-dev.yml
db.url: jdbc:mysql://localhost:3306/libreria_dev

# application-prod.yml
db.url: jdbc:mysql://prod-server:3306/libreria_prod
```

4. **Monitoreo y Métricas**
- Tiempo de respuesta de queries
- Número de conexiones activas
- Errores por minuto

---

## 🌟 Casos de Uso Reales

### **Caso 1: Librería Independiente**
"Tengo una librería física y quiero digitalizar mi inventario"

**Solución con este sistema:**
- ✅ Registro de todos los libros con autor
- ✅ Búsqueda rápida por título/ISBN
- ✅ Ver qué libros tiene cada autor
- ✅ Estadísticas de inventario

**Extensión futura:** Sistema de préstamos y ventas.

---

### **Caso 2: Bootcamp/Academia**
"Quiero enseñar bases de datos a mis estudiantes"

**Por qué este proyecto es ideal:**
- ✅ Código bien documentado y explicado
- ✅ Commits progresivos (historia de aprendizaje)
- ✅ Ejemplos de errores comunes y soluciones
- ✅ No usa frameworks "mágicos"

**Para instructores:** Pueden usar los commits como "lecciones" secuenciales.

---

### **Caso 3: Portfolio para Junior Developer**
"Necesito demostrar que sé trabajar con bases de datos"

**Qué demuestra este proyecto:**
- ✅ Fundamentos sólidos (JDBC, SQL, OOP)
- ✅ Buenas prácticas (PreparedStatement, DAO, testing)
- ✅ Herramientas modernas (Docker, Gradle)
- ✅ Capacidad de aprendizaje (evolución del código)

**Diferenciador:** No es solo código, es documentación del proceso de aprendizaje.

---

## 🔬 Comparación: JDBC vs Spring Data JPA

### **Mismo caso de uso, dos enfoques**

#### **Con JDBC (este proyecto):**
```java
// LibroDAOImpl.java - ~300 líneas de código
public Libro buscarPorId(int id) {
    String sql = """
        SELECT l.id, l.titulo, l.isbn,
               a.id as autor_id, a.nombre as autor_nombre
        FROM libros l
        INNER JOIN autores a ON l.autor_id = a.id
        WHERE l.id = ?
        """;
    
    try (Connection conn = DatabaseConnection.getConnection();
         PreparedStatement pstmt = conn.prepareStatement(sql)) {
        
        pstmt.setInt(1, id);
        ResultSet rs = pstmt.executeQuery();
        
        if (rs.next()) {
            Autor autor = new Autor(
                rs.getInt("autor_id"),
                rs.getString("autor_nombre"),
                ...
            );
            
            return new Libro(
                rs.getInt("id"),
                rs.getString("titulo"),
                rs.getString("isbn"),
                ...
                autor
            );
        }
    } catch (SQLException e) {
        throw new RuntimeException("Error en BD", e);
    }
    return null;
}
```

#### **Con Spring Data JPA (futuro):**
```java
// LibroRepository.java - ~5 líneas de código
@Repository
public interface LibroRepository extends JpaRepository<Libro, Long> {
    // Spring genera automáticamente:
    // - findById()
    // - findAll()
    // - save()
    // - delete()
    // Y hace el JOIN automáticamente porque Libro tiene @ManyToOne Autor
}

// Uso:
Libro libro = libroRepository.findById(1L).orElse(null);
```

### **Tabla Comparativa**

| Aspecto | JDBC | Spring Data JPA |
|---------|------|-----------------|
| **Líneas de código** | ~300 | ~5 |
| **SQL manual** | Sí | No (generado) |
| **Control fino** | Total | Limitado |
| **Curva de aprendizaje** | Moderada | Alta (entender JPA) |
| **Performance** | Óptimo (manual) | Bueno (lazy loading) |
| **Boilerplate** | Mucho | Mínimo |
| **Debugging** | Fácil (ves el SQL) | Complejo (SQL generado) |

### **¿Cuál usar?**

- **JDBC:** Queries complejas, performance crítico, control total
- **JPA:** CRUD estándar, prototipado rápido, proyectos grandes

**Mi conclusión:** Aprende JDBC primero, luego JPA. Así entiendes qué hace JPA por ti.

---

## 🎨 Diagramas Técnicos

### **Diagrama Entidad-Relación (ER)**

```
┌─────────────────────┐
│      AUTORES        │
├─────────────────────┤
│ 🔑 id (PK)          │
│ nombre              │
│ apellido            │
│ nacionalidad        │
│ fecha_nacimiento    │
│ created_at          │
└─────────────────────┘
           │ 1
           │
           │ tiene
           │
           │ N
┌─────────────────────┐
│      LIBROS         │
├─────────────────────┤
│ 🔑 id (PK)          │
│ titulo              │
│ isbn (UNIQUE)       │
│ genero              │
│ ano_publicacion     │
│ paginas             │
│ 🔗 autor_id (FK)    │────┐
│ created_at          │    │
└─────────────────────┘    │
                           │
                           └──→ REFERENCES autores(id)
```

**Cardinalidad:** 1:N (Un autor → Muchos libros)

---

### **Diagrama de Flujo: Insertar Libro**

```
[Usuario ingresa datos del libro]
            ↓
[LibreriaApp valida entrada]
            ↓
    ┌───────────────┐
    │ ¿Título vacío?├─→ Sí → [Mostrar error] → [Volver]
    └───────┬───────┘
            │ No
            ↓
[Crear objeto Libro con Autor]
            ↓
[LibroDAO.insertar(libro)]
            ↓
    ┌───────────────┐
    │ ¿Autor existe?├─→ No → [IllegalArgumentException] → [Error]
    └───────┬───────┘
            │ Sí
            ↓
    ┌───────────────┐
    │ ¿ISBN único?  ├─→ No → [IllegalArgumentException] → [Error]
    └───────┬───────┘
            │ Sí
            ↓
[PreparedStatement.setString/setInt]
            ↓
[executeUpdate()]
            ↓
[getGeneratedKeys() → ID]
            ↓
[libro.setId(generatedId)]
            ↓
[✅ Libro insertado con éxito]
            ↓
[Mostrar confirmación al usuario]
```

---

## 🏆 Logros y Aprendizajes Clave

### **Técnicos**
- [x] Conexión JDBC exitosa desde cero
- [x] Implementación completa de patrón DAO
- [x] Manejo de relaciones con Foreign Keys
- [x] JOINs complejos con múltiples tablas
- [x] PreparedStatement para prevenir SQL Injection
- [x] try-with-resources para gestión de recursos
- [x] Dockerización de la aplicación
- [x] Tests unitarios con JUnit
- [x] Configuración externalizada

### **Conceptuales**
- [x] Entendimiento profundo de SQL
- [x] Diferencia entre Statement y PreparedStatement
- [x] Importancia de cerrar conexiones
- [x] Primary Key vs Foreign Key
- [x] Integridad referencial
- [x] Tipos de datos en SQL
- [x] Separación de responsabilidades (capas)
- [x] Por qué interfaces y no solo clases

### **Soft Skills**
- [x] Documentación exhaustiva del código
- [x] Resolución de problemas por cuenta propia
- [x] Iteración y mejora continua
- [x] Comunicación técnica (este README)

---

## 📚 Glosario de Términos

Para alguien nuevo en el tema, estos son los términos clave:

- **JDBC:** Java Database Connectivity - API para conectar Java con BD
- **DAO:** Data Access Object - Patrón para separar lógica de BD
- **POJO:** Plain Old Java Object - Clase simple sin frameworks
- **PreparedStatement:** Query SQL pre-compilado con parámetros seguros
- **ResultSet:** Conjunto de resultados de una query SELECT
- **Primary Key:** Identificador único de una fila
- **Foreign Key:** Referencia a Primary Key de otra tabla
- **INNER JOIN:** Combinar tablas por columna común
- **SQL Injection:** Ataque que explota queries mal construidos
- **Connection Pool:** Conjunto reutilizable de conexiones BD
- **ORM:** Object-Relational Mapping - Mapeo automático objeto↔tabla

---

## 📞 Soporte y Contribuciones

### **¿Encontraste un bug?**
Abre un issue en GitHub con:
- Descripción del problema
- Pasos para reproducir
- Logs de error
- Tu entorno (OS, Java version, etc.)

### **¿Quieres contribuir?**
Pull requests son bienvenidos! Para cambios grandes:
1. Abre un issue primero para discutir
2. Fork el repositorio
3. Crea una rama feature (`git checkout -b feature/AmazingFeature`)
4. Commit tus cambios (`git commit -m 'Add some AmazingFeature'`)
5. Push a la rama (`git push origin feature/AmazingFeature`)
6. Abre un Pull Request

### **¿Tienes preguntas?**
- 📧 Email: alancruz.sys@gmail.com
- 💬 Discussions en GitHub

---

<div align="center">

## ⭐ Si este proyecto te ayudó a entender JDBC, dale una estrella ⭐

**Hecho con ☕

```
     _______________
    |               |
    |  JDBC PURO    |
    |  ≠            |
    |  MAGIA        |
    |_______________|
         ||  ||
        _||  ||_
```

### 📖 **"Antes de usar frameworks que hacen magia, entendí cómo funciona la magia"**

---
</div>
