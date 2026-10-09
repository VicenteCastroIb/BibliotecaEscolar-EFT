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

        JLabel lblBienvenida = new JLabel("Bienvenido/a, " + usuario.getNombre() + " (" + usuario.getTipo() + ")");
        panel.add(lblBienvenida);

        boolean esBibliotecario = usuario.getRol() == Rol.BIBLIOTECARIO;

        // Solo bibliotecario
        if (esBibliotecario) {
            panel.add(crearBoton("Gestión de libros"));
            panel.add(crearBoton("Gestión de categorías"));
            panel.add(crearBoton("Gestión de estudiantes"));
        }

        // Ambos roles, prestamo, devolucion y consulta
        panel.add(crearBoton("Préstamos y devoluciones"));
        panel.add(crearBoton("Reportes"));

        JButton btnSalir = new JButton("Cerrar sesión");
        btnSalir.addActionListener(e -> {
            new VentanaLogin().setVisible(true);
            dispose();
        });
        panel.add(btnSalir);

        setContentPane(panel);
        pack();
        setResizable(false);
        setLocationRelativeTo(null);
    }

    private JButton crearBoton(String texto) {
        JButton boton = new JButton(texto);
        boton.addActionListener(e ->
                JOptionPane.showMessageDialog(this, "Módulo \"" + texto + "\" en construcción."));
        return boton;
    }
}