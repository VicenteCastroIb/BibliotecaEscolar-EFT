package com.puertogames.biblioteca;


import com.puertogames.biblioteca.dao.PrestamoDAO;
import com.puertogames.biblioteca.modelo.Prestamo;

public class PruebaConexion {
    public static void main(String[] args) {
        PrestamoDAO prestamoDao = new PrestamoDAO();
        for (Prestamo p : prestamoDao.listarNoDevueltos()) {
            System.out.println("#" + p.getId() + " " + p.getEstudiante().getNombre()
                    + " - " + p.getLibro() + " - vence " + p.getFechaDevolucion()
                    + (p.estaAtrasado() ? " ⚠ ATRASADO" : ""));
        }
    }
}