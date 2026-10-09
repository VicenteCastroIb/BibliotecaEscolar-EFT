package com.puertogames.biblioteca;

import com.puertogames.biblioteca.vista.VentanaLogin;

import javax.swing.*;

public class Main {
    public static void main(String[] args) {
        try {
            // Que las ventanas se vean como las de Windows
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ignored) { }

        SwingUtilities.invokeLater(() -> new VentanaLogin().setVisible(true));
    }
}