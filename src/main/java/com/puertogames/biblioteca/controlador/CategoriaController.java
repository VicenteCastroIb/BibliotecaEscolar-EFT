package com.puertogames.biblioteca.controlador;

import com.puertogames.biblioteca.dao.CategoriaDAO;
import com.puertogames.biblioteca.modelo.Categoria;

import java.util.List;

public class CategoriaController {

    private final CategoriaDAO categoriaDAO = new CategoriaDAO();

    public List<Categoria> listar() {
        return categoriaDAO.listarTodos();
    }

    // null = exito | String = mensaje de error
    public String guardar(Categoria categoria) {
        if (categoria.getNombre().isBlank()) {
            return "El nombre de la categoría es obligatorio.";
        }
        boolean ok = (categoria.getId() == 0)
                ? categoriaDAO.insertar(categoria)     // nueva
                : categoriaDAO.actualizar(categoria);  // existente
        return ok ? null : "No se pudo guardar la categoría.";
    }

    public String eliminar(int id) {
        return categoriaDAO.eliminar(id) ? null
                : "No se puede eliminar, la categoría tiene libros asociados.";
    }
}