package com.puertogames.biblioteca;

import com.puertogames.biblioteca.conexion.DatabaseConnection;
import com.puertogames.biblioteca.vista.VentanaLogin;

import javax.swing.*;
import java.sql.SQLException;

public class Main {

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ignored) { }

        // Verificar la conexion antes de mostrar el login
        try {
            DatabaseConnection.getInstance();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null,
                    "No se pudo conectar a la base de datos.\n"
                            + "Verifica que MySQL esté encendido y que el usuario y la contraseña\n"
                            + "en DatabaseConnection sean correctos.\n\nDetalle: " + e.getMessage(),
                    "Error de conexión", JOptionPane.ERROR_MESSAGE);
            return;
        }

        SwingUtilities.invokeLater(() -> new VentanaLogin().setVisible(true));
    }
}
