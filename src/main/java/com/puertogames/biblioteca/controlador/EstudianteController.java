package com.puertogames.biblioteca.controlador;

import com.puertogames.biblioteca.dao.EstudianteDAO;
import com.puertogames.biblioteca.dao.PrestamoDAO;
import com.puertogames.biblioteca.modelo.Estudiante;
import com.puertogames.biblioteca.modelo.Prestamo;

import java.util.List;

public class EstudianteController {

    private final EstudianteDAO estudianteDAO = new EstudianteDAO();
    private final PrestamoDAO prestamoDAO = new PrestamoDAO();

    public List<Estudiante> listar() {
        return estudianteDAO.listarTodos();
    }

    public String guardar(Estudiante estudiante) {
        if (estudiante.getNombre().isBlank() || estudiante.getRut().isBlank()) {
            return "Nombre y RUT son obligatorios.";
        }
        boolean ok = (estudiante.getId() == 0)
                ? estudianteDAO.insertar(estudiante)
                : estudianteDAO.actualizar(estudiante);
        return ok ? null : "No se pudo guardar. Revisa que el RUT no esté repetido.";
    }

    public String eliminar(int id) {
        return estudianteDAO.eliminar(id) ? null
                : "No se puede eliminar, el estudiante tiene préstamos registrados.";
    }

    // Historial individual
    public List<Prestamo> historial(int idEstudiante) {
        return prestamoDAO.listarPorEstudiante(idEstudiante);
    }
}