package com.puertogames.biblioteca.modelo;

public class Estudiante extends Persona {

    // Atributo propio
    private String curso;

    // Constructor
    public Estudiante(int id, String nombre, String rut, String correo, String curso) {
        super(id, nombre, rut, correo);
        this.curso = curso;
    }

    // Sobreescritura
    @Override
    public String getTipo() {
        return "Estudiante";
    }

    // Getters - Setters
    public String getCurso() {return curso;}
    public void setCurso(String curso) {this.curso = curso;}
}
