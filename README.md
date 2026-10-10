# Sistema de Gestión de Biblioteca Escolar

Evaluación Final Transversal (EFT) de la asignatura **Desarrollo Orientado a Objetos II** (DUOC UC), Semana 9.

Aplicación de escritorio en Java que permite gestionar la circulación de libros de una biblioteca escolar: registro, préstamo y devolución de libros, gestión de inventario y estudiantes, y reportes. Se construyó con **Java Swing**, **MySQL (JDBC)** y los patrones **MVC**, **DAO** y **Singleton**, con **hilos** y **sincronización** en el proceso de préstamo.

**Autor:** Vicente Castro Ibarra

---

## Funcionalidades

| Módulo | Bibliotecario | Estudiante |
|---|---|---|
| **Autenticación** por correo y contraseña, con menú según el rol | ✔ | ✔ |
| **Gestión de libros** (crear, modificar, eliminar y listar) | ✔ | Solo consulta del catálogo |
| **Gestión de categorías** (CRUD completo) | ✔ | — |
| **Gestión de estudiantes** (CRUD completo e historial individual) | ✔ | — |
| **Préstamos y devoluciones** (fecha de vencimiento automática y detección de atrasos) | ✔ | Solo sus propios préstamos |
| **Reportes**: libros más prestados, libros en préstamo e historial por estudiante | ✔ | Solo su historial |
| **Simulación de préstamos simultáneos** para validar el control de stock | ✔ | — |

**Reglas de negocio:**

- El plazo de préstamo es de **7 días**; la fecha de vencimiento se calcula automáticamente.
- Un préstamo está **atrasado** si no se ha devuelto y ya pasó su fecha de vencimiento.
- No se puede prestar un libro **sin stock**. El stock baja en 1 al prestar y sube en 1 al devolver.
- No se puede eliminar una categoría con libros, ni un libro o estudiante con préstamos registrados (integridad referencial).

---

## Requisitos

- **JDK 21** o superior
- **IntelliJ IDEA**
- **MySQL Server 8.4** y **MySQL Workbench**
- **Maven** (incluido en IntelliJ). El driver se descarga automáticamente: `com.mysql:mysql-connector-j:8.4.0`

---

## Instalación y ejecución

### 1. Crear la base de datos

En MySQL Workbench, ejecuta los scripts de la carpeta `database/` **en este orden**:

1. `PRY2203_EFT_S9_Script_crea_tablas_biblioteca.sql`: crea la base `biblioteca` y sus 5 tablas.
2. `PRY2203_EFT_S9_Script_poblado_tablas_biblioteca.sql`: carga los datos de prueba.

### 2. Configurar la conexión

En `src/main/java/com/puertogames/biblioteca/conexion/DatabaseConnection.java`, ajusta el usuario y la contraseña de tu MySQL:

```java
private static final String URL = "jdbc:mysql://localhost:3306/biblioteca";
private static final String USER = "root";
private static final String PASSWORD = "tu_contraseña";
```

### 3. Ejecutar

- **Desde IntelliJ:** abre el proyecto y ejecuta la clase `com.puertogames.biblioteca.Main`.
- **Desde el ejecutable:** `java -jar BibliotecaEscolar.jar`

### Usuarios de prueba

| Rol | Correo | Contraseña |
|---|---|---|
| Bibliotecario | `antonia@correo.cl` | `clave123` |
| Estudiante | `carlos@correo.cl` | `clave123` |

---

## Estructura del proyecto

```
src/main/java/com/puertogames/biblioteca/
├── Main.java                  Punto de entrada: abre la ventana de login
├── conexion/
│   └── DatabaseConnection     Conexión única a MySQL (Singleton)
├── modelo/
│   ├── Persona                Clase abstracta (id, nombre, rut, correo)
│   ├── Usuario                Hereda de Persona; agrega contraseña y rol
│   ├── Estudiante             Hereda de Persona; agrega curso
│   ├── Rol                    Enum: BIBLIOTECARIO, ESTUDIANTE
│   ├── Libro, Categoria       Inventario
│   └── Prestamo               Calcula vencimiento y detecta atrasos
├── dao/
│   ├── DAO<T>                 Interfaz genérica CRUD
│   ├── CategoriaDAO           CRUD de categorías
│   ├── LibroDAO               CRUD de libros (JOIN con categorías)
│   ├── EstudianteDAO          CRUD de estudiantes
│   ├── UsuarioDAO             Autenticación
│   └── PrestamoDAO            Préstamos y devoluciones sincronizados, y reportes
├── controlador/
│   ├── UsuarioController, CategoriaController, LibroController, EstudianteController
│   ├── PrestamoController     Préstamo en hilo independiente y simulación concurrente
│   └── ReporteController
└── vista/
    ├── VentanaLogin, VentanaPrincipal
    ├── VentanaLibros, VentanaCategorias, VentanaEstudiantes
    ├── VentanaPrestamos
    └── VentanaReportes
database/                      Scripts SQL de creación y poblado
```

---

## Diseño y decisiones técnicas

### Programación orientada a objetos

- **Clase abstracta:** `Persona` reúne los datos comunes de `Usuario` y `Estudiante` y declara el método abstracto `getTipo()`.
- **Herencia:** `Usuario` y `Estudiante` extienden `Persona` y reutilizan sus atributos mediante `super(...)`.
- **Polimorfismo:** `Persona.toString()` llama a `getTipo()`, que cada subclase implementa a su manera. El mismo código produce "Bibliotecario", "Estudiante" o "Usuario estudiante" según el objeto real.
- **Interfaz:** `DAO<T>` define el contrato CRUD (`insertar`, `obtenerPorId`, `listarTodos`, `actualizar`, `eliminar`), que implementan `CategoriaDAO`, `LibroDAO` y `EstudianteDAO`.
- **Encapsulamiento:** atributos privados con getters y setters.

### Patrones de diseño

- **MVC:** las vistas (`vista`) solo muestran datos y capturan eventos; los controladores (`controlador`) validan y deciden; los modelos (`modelo`) representan las entidades. Las vistas nunca acceden directamente a la base de datos.
- **DAO:** cada entidad tiene una clase que se encarga exclusivamente del acceso a datos con JDBC.
- **Singleton:** `DatabaseConnection` tiene un constructor privado y un método `getInstance()` sincronizado, lo que garantiza una única conexión compartida. Si la conexión se cierra, se crea una nueva.

### Concurrencia y consistencia

- **Hilo independiente:** al registrar un préstamo, `PrestamoController` ejecuta el proceso (verificar stock, guardar el préstamo y actualizar el stock) en un `Thread` aparte. La interfaz muestra una barra de progreso y **no se congela**. Al terminar, el resultado vuelve a la ventana con `SwingUtilities.invokeLater`, porque solo el hilo de eventos de Swing puede modificar componentes.
- **Sincronización:** `registrarPrestamo` y `registrarDevolucion` son métodos `synchronized`, por lo que un solo hilo a la vez puede modificar el stock. Esto evita **condiciones de carrera**, como que dos préstamos simultáneos del último ejemplar dejen el stock en -1. El controlador comparte una única instancia `static` de `PrestamoDAO`, de modo que todos los hilos usan el mismo bloqueo.
- **Prueba de concurrencia:** el botón *Simular 5 préstamos simultáneos* lanza 5 préstamos del mismo libro en paralelo con un `ExecutorService` y cuenta los resultados con `AtomicInteger`. Con un stock de 2, el resultado es siempre 2 préstamos registrados, 3 rechazados y stock final 0.

### Seguridad y manejo de errores

- Todas las consultas usan `PreparedStatement` con parámetros `?`, lo que **previene la inyección SQL**.
- Los recursos JDBC (`PreparedStatement` y `ResultSet`) se cierran con `try-with-resources`.
- Las `SQLException` se registran en consola y se traducen a mensajes claros para el usuario, por ejemplo al intentar eliminar registros relacionados o al ingresar un RUT o ISBN duplicado.

---

## Mejoras futuras

- Guardar las contraseñas con hash (por ejemplo, BCrypt). Hoy se comparan en texto plano porque así vienen en el script de datos entregado.
- Usar transacciones (`commit` y `rollback`) para que el préstamo y la actualización del stock se confirmen juntos.
- Buscadores y filtros en las tablas.
