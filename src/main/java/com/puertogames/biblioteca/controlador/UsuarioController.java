package com.puertogames.biblioteca.controlador;

import com.puertogames.biblioteca.dao.UsuarioDAO;
import com.puertogames.biblioteca.modelo.Usuario;

public class UsuarioController {

    private final UsuarioDAO usuarioDAO = new UsuarioDAO();

    // Devuelve el usuario si correo y contra son correctos
    public Usuario iniciarSesion(String correo, String contrasena) {
        if (correo.isBlank() || contrasena.isBlank()) {
            return null;
        }
        return usuarioDAO.autenticar(correo.trim(), contrasena);
    }
}