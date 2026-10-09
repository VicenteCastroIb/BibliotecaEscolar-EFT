package com.puertogames.biblioteca.dao;

import com.puertogames.biblioteca.conexion.DatabaseConnection;
import com.puertogames.biblioteca.modelo.Prestamo;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class PrestamoDAO {

    private Connection getConnection() throws SQLException {
        return DatabaseConnection.getInstance().getConnection();
    }

    // Solo un hilo a la vez puede usar el metodo
    public synchronized boolean registrarPrestamo(Prestamo prestamo) {
        int idLibro = prestamo.getLibro().getId();

        try {
            // hay stock?
            String sqlStock = "SELECT stock FROM libros WHERE id = ?";
            try (PreparedStatement ps = getConnection().prepareStatement(sqlStock)) {
                ps.setInt(1, idLibro);
                try (ResultSet rs = ps.executeQuery()) {
                    if (!rs.next() || rs.getInt("stock") <= 0) {
                        return false;   // no existe el libro o no hay stock
                    }
                }
            }

            // guardar el prestamo
            String sqlPrestamo = "INSERT INTO prestamos (id_estudiante, id_libro, fecha_prestamo, fecha_devolucion, devuelto) " +
                    "VALUES (?, ?, ?, ?, FALSE)";
            try (PreparedStatement ps = getConnection().prepareStatement(sqlPrestamo)) {
                ps.setInt(1, prestamo.getEstudiante().getId());
                ps.setInt(2, idLibro);
                ps.setDate(3, Date.valueOf(prestamo.getFechaPrestamo()));
                ps.setDate(4, Date.valueOf(prestamo.getFechaDevolucion()));
                ps.executeUpdate();
            }

            // restar 1 al stock
            String sqlRestar = "UPDATE libros SET stock=stock-1 WHERE id=?";
            try (PreparedStatement ps = getConnection().prepareStatement(sqlRestar)) {
                ps.setInt(1, idLibro);
                ps.executeUpdate();
            }

            return true;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public synchronized boolean registrarDevolucion(Prestamo prestamo) {
        try {
            // marcar como devuelto
            String sqlDevolver = "UPDATE prestamos SET devuelto = TRUE WHERE id = ? AND devuelto = FALSE";
            try (PreparedStatement ps = getConnection().prepareStatement(sqlDevolver)) {
                ps.setInt(1, prestamo.getId());
                if (ps.executeUpdate() == 0) {
                    return false;   // no existe o ya estaba devuelto
                }
            }

            // sumar 1 al stock
            String sqlSumar = "UPDATE libros SET stock = stock + 1 WHERE id = ?";
            try (PreparedStatement ps = getConnection().prepareStatement(sqlSumar)) {
                ps.setInt(1, prestamo.getLibro().getId());
                ps.executeUpdate();
            }

            return true;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}