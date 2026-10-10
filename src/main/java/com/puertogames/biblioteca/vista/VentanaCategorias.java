package com.puertogames.biblioteca.vista;

import com.puertogames.biblioteca.controlador.CategoriaController;
import com.puertogames.biblioteca.modelo.Categoria;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class VentanaCategorias extends JFrame {

    private final CategoriaController controller = new CategoriaController();
    private List<Categoria> categorias = new ArrayList<>();
    private int idSeleccionado = 0;

    private final DefaultTableModel modelo = new DefaultTableModel(new Object[]{"ID", "Nombre"}, 0) {
        @Override
        public boolean isCellEditable(int fila, int columna) {
            return false;
        }
    };
    private final JTable tabla = new JTable(modelo);
    private final JTextField txtNombre = new JTextField(20);

    public VentanaCategorias() {
        setTitle("Gestión de categorías");
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout(10, 10));

        tabla.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tabla.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) cargarSeleccion();
        });
        add(new JScrollPane(tabla), BorderLayout.CENTER);

        JPanel formulario = new JPanel(new FlowLayout(FlowLayout.LEFT));
        formulario.add(new JLabel("Nombre:"));
        formulario.add(txtNombre);

        JButton btnNuevo = new JButton("Nuevo");
        JButton btnGuardar = new JButton("Guardar");
        JButton btnEliminar = new JButton("Eliminar");
        btnNuevo.addActionListener(e -> limpiar());
        btnGuardar.addActionListener(e -> guardar());
        btnEliminar.addActionListener(e -> eliminar());

        JPanel botones = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        botones.add(btnNuevo);
        botones.add(btnGuardar);
        botones.add(btnEliminar);

        JPanel inferior = new JPanel(new BorderLayout());
        inferior.setBorder(new EmptyBorder(10, 10, 10, 10));
        inferior.add(formulario, BorderLayout.CENTER);
        inferior.add(botones, BorderLayout.SOUTH);
        add(inferior, BorderLayout.SOUTH);

        cargarTabla();
        setSize(500, 400);
        setLocationRelativeTo(null);
    }

    private void cargarTabla() {
        modelo.setRowCount(0);
        categorias = controller.listar();
        for (Categoria c : categorias) {
            modelo.addRow(new Object[]{c.getId(), c.getNombre()});
        }
    }

    private void cargarSeleccion() {
        int fila = tabla.getSelectedRow();
        if (fila < 0) return;
        Categoria c = categorias.get(fila);
        idSeleccionado = c.getId();
        txtNombre.setText(c.getNombre());
    }

    private void limpiar() {
        idSeleccionado = 0;
        txtNombre.setText("");
        tabla.clearSelection();
    }

    private void guardar() {
        Categoria categoria = new Categoria(idSeleccionado, txtNombre.getText().trim());
        String error = controller.guardar(categoria);
        if (error == null) {
            JOptionPane.showMessageDialog(this, "Categoría guardada correctamente.");
            cargarTabla();
            limpiar();
        } else {
            JOptionPane.showMessageDialog(this, error, "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void eliminar() {
        if (idSeleccionado == 0) {
            JOptionPane.showMessageDialog(this, "Selecciona una categoría de la tabla.");
            return;
        }
        int resp = JOptionPane.showConfirmDialog(this, "¿Eliminar la categoría seleccionada?",
                "Confirmar", JOptionPane.YES_NO_OPTION);
        if (resp != JOptionPane.YES_OPTION) return;

        String error = controller.eliminar(idSeleccionado);
        if (error == null) {
            JOptionPane.showMessageDialog(this, "Categoría eliminada.");
            cargarTabla();
            limpiar();
        } else {
            JOptionPane.showMessageDialog(this, error, "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}