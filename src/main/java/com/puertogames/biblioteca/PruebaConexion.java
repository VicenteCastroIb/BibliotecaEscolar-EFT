package com.puertogames.biblioteca;

import com.puertogames.biblioteca.conexion.DatabaseConnection;
import java.sql.SQLException;

public class PruebaConexion {
    public static void main(String[] args) {
        try {
            DatabaseConnection a = DatabaseConnection.getInstance();
            DatabaseConnection b = DatabaseConnection.getInstance();
            System.out.println("Conexion exitosa a biblioteca");
            System.out.println("¿Son la misma instancia? " + (a == b));
        } catch (SQLException e) {
            System.err.println("Error al conectar: " + e.getMessage());
        }
    }
}