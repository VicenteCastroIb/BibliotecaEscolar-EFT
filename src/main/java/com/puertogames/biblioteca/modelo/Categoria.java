package com.puertogames.biblioteca.modelo;

public class Categoria {

    // Atributos
    private int id;
    private String nombre;

    // Constructor
    public Categoria(int id, String nombre) {
        this.id = id;
        this.nombre = nombre;
    }

    // Getters - Setters
    public int getId() {return id;}
    public void setId(int id) {this.id = id;}
    public String getNombre() {return nombre;}
    public void setNombre(String nombre) {this.nombre = nombre;}

    @Override
    public String toString(){return nombre;}
}
