package com.puertogames.biblioteca.modelo;

public class Usuario extends Persona {

    // Atributo propio y ENUM
    private String contrasena;
    private Rol rol;

    // Constructor, atributo propio + ENUM + supers
    public Usuario(int id, String nombre, String rut, String correo, String contrasena, Rol rol) {
        super(id, nombre, rut, correo);
        this.contrasena = contrasena;
        this.rol = rol;
    }

    // Sobreescritura de metodo
    @Override
    public String getTipo(){
        return rol == Rol.BIBLIOTECARIO ? "Bibliotecario" : "Estudiante";
    }

    // Getters - Setters
    public Rol getRol() {return rol;}
    public void setRol(Rol rol) {this.rol = rol;}
    public String getContrasena() {return contrasena;}
    public void setContrasena(String contrasena) {this.contrasena = contrasena;}
}
