package com.puertogames.biblioteca.vista;

import com.puertogames.biblioteca.modelo.Rol;
import com.puertogames.biblioteca.modelo.Usuario;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class VentanaPrincipal extends JFrame {

    private final Usuario usuario;

    public VentanaPrincipal(Usuario usuario) {
        this.usuario = usuario;

        setTitle("Biblioteca Escolar - Menú principal");
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        JPanel panel = new JPanel(new GridLayout(0, 1, 10, 10));
        panel.setBorder(new EmptyBorder(20, 30, 20, 30));

        panel.add(new JLabel("Bienvenido/a, " + usuario.getNombre() + " (" + usuario.getTipo() + ")"));

        boolean esBibliotecario = usuario.getRol() == Rol.BIBLIOTECARIO;

        if (esBibliotecario) {
            panel.add(crearBoton("Gestión de libros", () -> new VentanaLibros(false).setVisible(true)));
            panel.add(crearBoton("Gestión de categorías", () -> new VentanaCategorias().setVisible(true)));
            panel.add(crearBoton("Gestión de estudiantes", () -> new VentanaEstudiantes().setVisible(true)));
        } else {
            panel.add(crearBoton("Consultar libros", () -> new VentanaLibros(true).setVisible(true)));
        }

        panel.add(crearBoton("Préstamos y devoluciones", () -> new VentanaPrestamos(usuario).setVisible(true)));
        panel.add(crearBoton("Reportes", () -> enConstruccion("Reportes")));

        panel.add(crearBoton("Cerrar sesión", () -> {
            new VentanaLogin().setVisible(true);
            dispose();
        }));

        setContentPane(panel);
        pack();
        setResizable(false);
        setLocationRelativeTo(null);
    }

    // Crea un boton que ejecuta la accion recibida
    private JButton crearBoton(String texto, Runnable accion) {
        JButton boton = new JButton(texto);
        boton.addActionListener(e -> accion.run());
        return boton;
    }

    private void enConstruccion(String modulo) {
        JOptionPane.showMessageDialog(this, "Módulo \"" + modulo + "\" en construcción.");
    }
}