package com.puertogames.biblioteca.dao;

import java.util.List;

// Usando Tipo generico, cada dao lo rellena
public interface DAO<T> {

    boolean insertar(T objeto);  // Create
    T obtenerPorId(int id);      // Read(uno)
    List<T> listarTodos();       // Read(todos)
    boolean actualizar(T objeto);// Update
    boolean eliminar(int id);    // Delete
}
