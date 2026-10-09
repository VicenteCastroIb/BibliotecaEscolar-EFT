package com.puertogames.biblioteca.vista;

import com.puertogames.biblioteca.controlador.UsuarioController;
import com.puertogames.biblioteca.modelo.Usuario;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class VentanaLogin extends JFrame {

    private final UsuarioController controller = new UsuarioController();
    private final JTextField txtCorreo = new JTextField(20);
    private final JPasswordField txtContrasena = new JPasswordField(20);

    public VentanaLogin() {
        setTitle("Biblioteca Escolar - Iniciar sesión");
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        JPanel panel = new JPanel(new GridLayout(3, 2, 10, 10));
        panel.setBorder(new EmptyBorder(20, 20, 20, 20));

        JButton btnIngresar = new JButton("Ingresar");
        btnIngresar.addActionListener(e -> ingresar());

        panel.add(new JLabel("Correo:"));
        panel.add(txtCorreo);
        panel.add(new JLabel("Contraseña:"));
        panel.add(txtContrasena);
        panel.add(new JLabel());
        panel.add(btnIngresar);

        setContentPane(panel);
        getRootPane().setDefaultButton(btnIngresar);
        pack();
        setResizable(false);
        setLocationRelativeTo(null);
    }

    private void ingresar() {
        String correo = txtCorreo.getText();
        String contrasena = new String(txtContrasena.getPassword());

        Usuario usuario = controller.iniciarSesion(correo, contrasena);

        if (usuario == null) {
            JOptionPane.showMessageDialog(this, "Correo o contraseña incorrectos.",
                    "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        new VentanaPrincipal(usuario).setVisible(true);
        dispose();   // cierra el login
    }
}