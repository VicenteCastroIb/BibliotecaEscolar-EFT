package com.puertogames.biblioteca.modelo;

import java.time.LocalDate;

public class Prestamo {

    public static final int DIAS_PRESTADOS = 7;

    private int id;
    private Estudiante estudiante;
    private Libro libro;
    private LocalDate fechaPrestamo;
    private LocalDate fechaDevolucion;
    private boolean devuelto;

    // Constructor para Prestamo nuevo
    public Prestamo(Estudiante estudiante, Libro libro) {
        this.estudiante = estudiante;
        this.libro = libro;
        this.fechaPrestamo = LocalDate.now();
        this.fechaDevolucion = LocalDate.now().plusDays(DIAS_PRESTADOS);
        this.devuelto = false;
    }

    // Constructor para los prestamos que vienen desde la BD
    public Prestamo(int id, Estudiante estudiante, Libro libro, LocalDate fechaPrestamo, LocalDate fechaDevolucion, boolean devuelto) {
        this.id = id;
        this.estudiante = estudiante;
        this.libro = libro;
        this.fechaPrestamo = fechaPrestamo;
        this.fechaDevolucion = fechaDevolucion;
        this.devuelto = devuelto;
    }

    // Metodo para ver atrasados
    public boolean estaAtrasado() {
        return !devuelto && LocalDate.now().isAfter(this.fechaDevolucion);
    }

    // Getters - Setters
    public boolean isDevuelto() {return devuelto;}
    public void setDevuelto(boolean devuelto) {this.devuelto = devuelto;}
    public LocalDate getFechaDevolucion() {return fechaDevolucion;}
    public void setFechaDevolucion(LocalDate fechaDevolucion) {this.fechaDevolucion = fechaDevolucion;}
    public LocalDate getFechaPrestamo() {return fechaPrestamo;}
    public void setFechaPrestamo(LocalDate fechaPrestamo) {this.fechaPrestamo = fechaPrestamo;}
    public Libro getLibro() {return libro;}
    public void setLibro(Libro libro) {this.libro = libro;}
    public Estudiante getEstudiante() {return estudiante;}
    public void setEstudiante(Estudiante estudiante) {this.estudiante = estudiante;}
    public int getId() {return id;}
    public void setId(int id) {this.id = id;}
}
