package com.puertogames.biblioteca.controlador;

import com.puertogames.biblioteca.dao.CategoriaDAO;
import com.puertogames.biblioteca.dao.LibroDAO;
import com.puertogames.biblioteca.modelo.Categoria;
import com.puertogames.biblioteca.modelo.Libro;

import java.util.List;

public class LibroController {

    private final LibroDAO libroDAO = new LibroDAO();
    private final CategoriaDAO categoriaDAO = new CategoriaDAO();

    public List<Libro> listar() {
        return libroDAO.listarTodos();
    }

    // Para llenar el combo de categorias en el formulario de libros
    public List<Categoria> listarCategorias() {
        return categoriaDAO.listarTodos();
    }

    public String guardar(Libro libro) {
        if (libro.getTitulo().isBlank() || libro.getAutor().isBlank() || libro.getIsbn().isBlank()) {
            return "Título, autor e ISBN son obligatorios.";
        }
        if (libro.getStock() < 0) {
            return "El stock no puede ser negativo.";
        }
        if (libro.getCategoria() == null) {
            return "Selecciona una categoría.";
        }
        boolean ok = (libro.getId() == 0)
                ? libroDAO.insertar(libro)
                : libroDAO.actualizar(libro);
        return ok ? null : "No se pudo guardar. Revisa que el ISBN no esté repetido.";
    }

    public String eliminar(int id) {
        return libroDAO.eliminar(id) ? null
                : "No se puede eliminar, el libro tiene préstamos registrados.";
    }
}