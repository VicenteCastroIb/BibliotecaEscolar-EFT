package com.puertogames.biblioteca.controlador;

import com.puertogames.biblioteca.dao.EstudianteDAO;
import com.puertogames.biblioteca.dao.PrestamoDAO;
import com.puertogames.biblioteca.modelo.Estudiante;
import com.puertogames.biblioteca.modelo.Prestamo;

import java.util.List;

public class ReporteController {

    private final PrestamoDAO prestamoDAO = new PrestamoDAO();   // solo lectura, no necesita ser compartido
    private final EstudianteDAO estudianteDAO = new EstudianteDAO();

    public List<Object[]> librosMasPrestados() {
        return prestamoDAO.librosMasPrestados();
    }

    public List<Prestamo> librosEnPrestamo() {
        return prestamoDAO.listarNoDevueltos();
    }

    public List<Prestamo> historialEstudiante(int idEstudiante) {
        return prestamoDAO.listarPorEstudiante(idEstudiante);
    }

    public List<Estudiante> listarEstudiantes() {
        return estudianteDAO.listarTodos();
    }

    public Estudiante buscarEstudiantePorRut(String rut) {
        for (Estudiante e : estudianteDAO.listarTodos()) {
            if (e.getRut().equals(rut)) return e;
        }
        return null;
    }
}