package com.puertogames.biblioteca.controlador;

import com.puertogames.biblioteca.dao.EstudianteDAO;
import com.puertogames.biblioteca.dao.LibroDAO;
import com.puertogames.biblioteca.dao.PrestamoDAO;
import com.puertogames.biblioteca.modelo.Estudiante;
import com.puertogames.biblioteca.modelo.Libro;
import com.puertogames.biblioteca.modelo.Prestamo;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import javax.swing.SwingUtilities;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class PrestamoController {

    // static: UN SOLO PrestamoDAO para toda la aplicacion.
    // synchronized bloquea sobre ese objeto, asi que todas las ventanas
    // y todos los hilos comparten la misma "llave".
    private static final PrestamoDAO prestamoDAO = new PrestamoDAO();

    private final EstudianteDAO estudianteDAO = new EstudianteDAO();
    private final LibroDAO libroDAO = new LibroDAO();

    public List<Estudiante> listarEstudiantes() {
        return estudianteDAO.listarTodos();
    }

    // Busca la ficha de estudiante de un usuario (las tablas se unen por RUT)
    public Estudiante buscarEstudiantePorRut(String rut) {
        for (Estudiante e : estudianteDAO.listarTodos()) {
            if (e.getRut().equals(rut)) {
                return e;
            }
        }
        return null;
    }

    // Solo los libros que tienen stock
    public List<Libro> listarLibrosDisponibles() {
        List<Libro> disponibles = new ArrayList<>();
        for (Libro l : libroDAO.listarTodos()) {
            if (l.hayStock()) {
                disponibles.add(l);
            }
        }
        return disponibles;
    }

    public List<Prestamo> listarActivos() {
        return prestamoDAO.listarNoDevueltos();
    }

    // Registra el prestamo EN UN HILO APARTE para no congelar la ventana.
    // alTerminar recibe null si salio bien, o un mensaje de error.
    public void registrarPrestamoEnSegundoPlano(Estudiante estudiante, Libro libro,
                                                Consumer<String> alTerminar) {
        if (estudiante == null || libro == null) {
            alTerminar.accept("Selecciona un estudiante y un libro.");
            return;
        }

        Thread hilo = new Thread(() -> {
            try {
                Thread.sleep(1500);   // simula una conexion lenta, para ver que la ventana no se congela
            } catch (InterruptedException ignored) { }

            boolean ok = prestamoDAO.registrarPrestamo(new Prestamo(estudiante, libro));
            String resultado = ok ? null : "No hay stock disponible para \"" + libro.getTitulo() + "\".";

            // Volver al hilo de la ventana para mostrar el resultado
            SwingUtilities.invokeLater(() -> alTerminar.accept(resultado));
        }, "hilo-prestamo");

        hilo.start();
    }

    public String registrarDevolucion(Prestamo prestamo) {
        if (prestamo == null) {
            return "Selecciona un préstamo de la tabla.";
        }
        return prestamoDAO.registrarDevolucion(prestamo) ? null
                : "No se pudo registrar la devolución (quizás ya estaba devuelto).";
    }

    // Lanza 'cantidad' prestamos del msimo libro al mismo tiempo, cada uno en su propio hilo.
    public void simularPrestamosSimultaneos(Estudiante estudiante, Libro libro, int cantidad,
                                            Consumer<String> alTerminar) {
        if (estudiante == null || libro == null) {
            alTerminar.accept("Selecciona un estudiante y un libro.");
            return;
        }

        // corre en hilo aparte
        new Thread(() -> {
            int stockInicial = libroDAO.obtenerPorId(libro.getId()).getStock();
            AtomicInteger exitosos = new AtomicInteger(0);
            AtomicInteger rechazados = new AtomicInteger(0);

            // cantidad de hilos trabajando aparte
            ExecutorService pool = Executors.newFixedThreadPool(cantidad);
            for (int i = 0; i < cantidad; i++) {
                pool.submit(() -> {
                    boolean ok = prestamoDAO.registrarPrestamo(new Prestamo(estudiante, libro));
                    if (ok) {
                        exitosos.incrementAndGet();
                    } else {
                        rechazados.incrementAndGet();
                    }
                });
            }

            pool.shutdown();   // no se aceptan mas tareas
            try {
                pool.awaitTermination(30, TimeUnit.SECONDS);   // esperar a que terminen todas
            } catch (InterruptedException ignored) { }

            int stockFinal = libroDAO.obtenerPorId(libro.getId()).getStock();
            String resumen = "Libro: " + libro.getTitulo()
                    + "\nStock inicial: " + stockInicial
                    + "\nSolicitudes simultáneas: " + cantidad
                    + "\nPréstamos registrados: " + exitosos.get()
                    + "\nRechazados por falta de stock: " + rechazados.get()
                    + "\nStock final: " + stockFinal;

            SwingUtilities.invokeLater(() -> alTerminar.accept(resumen));
        }, "hilo-simulacion").start();
    }
}