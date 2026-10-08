package com.puertogames.biblioteca.modelo;

public abstract class Persona {

    // Atributos de clase padre
    private int id;
    private String nombre;
    private String rut;
    private String correo;

    // Constructor clase padre
    public Persona(int id, String nombre, String rut, String correo) {
        this.id = id;
        this.nombre = nombre;
        this.rut = rut;
        this.correo = correo;
    }

    // Metodo abstracto
    public abstract String getTipo();

    // Getters - Setters
    public String getCorreo() {return correo;}
    public void setCorreo(String correo) {this.correo = correo;}
    public String getRut() {return rut;}
    public void setRut(String rut) {this.rut = rut;}
    public String getNombre() {return nombre;}
    public void setNombre(String nombre) {this.nombre = nombre;}
    public int getId() {return id;}
    public void setId(int id) {this.id = id;}

    // Polimosfismo con el toString y metodo abstracto
    @Override
    public String toString() {
        return "| " + getTipo() + " | - |" + nombre + " | - " + rut;
    }
}
