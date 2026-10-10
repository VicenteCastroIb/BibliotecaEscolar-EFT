package com.puertogames.biblioteca.vista;

import com.puertogames.biblioteca.controlador.LibroController;
import com.puertogames.biblioteca.modelo.Categoria;
import com.puertogames.biblioteca.modelo.Libro;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class VentanaLibros extends JFrame {

    private final LibroController controller = new LibroController();
    private final boolean soloLectura;

    private List<Libro> libros = new ArrayList<>();   // los libros que se ven en la tabla
    private int idSeleccionado = 0;                   // 0 = libro nuevo

    // Tabla (no editable con doble clic)
    private final DefaultTableModel modelo = new DefaultTableModel(
            new Object[]{"ID", "Título", "Autor", "ISBN", "Editorial", "Stock", "Categoría"}, 0) {
        @Override
        public boolean isCellEditable(int fila, int columna) {
            return false;
        }
    };
    private final JTable tabla = new JTable(modelo);

    // Formulario
    private final JTextField txtTitulo = new JTextField(20);
    private final JTextField txtAutor = new JTextField(20);
    private final JTextField txtIsbn = new JTextField(20);
    private final JTextField txtEditorial = new JTextField(20);
    private final JSpinner spnStock = new JSpinner(new SpinnerNumberModel(0, 0, 1000, 1));
    private final JComboBox<Categoria> cmbCategoria = new JComboBox<>();

    public VentanaLibros(boolean soloLectura) {
        this.soloLectura = soloLectura;

        setTitle(soloLectura ? "Catálogo de libros" : "Gestión de libros");
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout(10, 10));

        tabla.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        add(new JScrollPane(tabla), BorderLayout.CENTER);

        if (!soloLectura) {
            add(crearPanelInferior(), BorderLayout.SOUTH);

            // Al hacer clic en una fila, se carga en el formulario
            tabla.getSelectionModel().addListSelectionListener(e -> {
                if (!e.getValueIsAdjusting()) {
                    cargarSeleccion();
                }
            });
            cargarCategorias();
        }

        cargarTabla();
        setSize(900, 550);
        setLocationRelativeTo(null);
    }

    private JPanel crearPanelInferior() {
        JPanel formulario = new JPanel(new GridLayout(0, 4, 8, 8));   // 4 columnas: etiqueta, campo, etiqueta, campo
        formulario.add(new JLabel("Título:"));    formulario.add(txtTitulo);
        formulario.add(new JLabel("Autor:"));     formulario.add(txtAutor);
        formulario.add(new JLabel("ISBN:"));      formulario.add(txtIsbn);
        formulario.add(new JLabel("Editorial:")); formulario.add(txtEditorial);
        formulario.add(new JLabel("Stock:"));     formulario.add(spnStock);
        formulario.add(new JLabel("Categoría:")); formulario.add(cmbCategoria);

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

        JPanel panel = new JPanel(new BorderLayout(5, 5));
        panel.setBorder(new EmptyBorder(10, 10, 10, 10));
        panel.add(formulario, BorderLayout.CENTER);
        panel.add(botones, BorderLayout.SOUTH);
        return panel;
    }

    // Llena el combo con las categorias de la BD
    private void cargarCategorias() {
        cmbCategoria.removeAllItems();
        for (Categoria c : controller.listarCategorias()) {
            cmbCategoria.addItem(c);
        }
    }

    // Llena la tabla con los libros de la BD
    private void cargarTabla() {
        modelo.setRowCount(0);
        libros = controller.listar();
        for (Libro l : libros) {
            modelo.addRow(new Object[]{
                    l.getId(), l.getTitulo(), l.getAutor(), l.getIsbn(),
                    l.getEditorial(), l.getStock(), l.getCategoria()
            });
        }
    }

    // Pasa el libro de la fila seleccionada al formulario
    private void cargarSeleccion() {
        int fila = tabla.getSelectedRow();
        if (fila < 0) return;

        Libro l = libros.get(fila);
        idSeleccionado = l.getId();
        txtTitulo.setText(l.getTitulo());
        txtAutor.setText(l.getAutor());
        txtIsbn.setText(l.getIsbn());
        txtEditorial.setText(l.getEditorial());
        spnStock.setValue(l.getStock());

        // Seleccionar en el combo la categoria con el mismo id
        for (int i = 0; i < cmbCategoria.getItemCount(); i++) {
            if (cmbCategoria.getItemAt(i).getId() == l.getCategoria().getId()) {
                cmbCategoria.setSelectedIndex(i);
                break;
            }
        }
    }

    private void limpiar() {
        idSeleccionado = 0;
        txtTitulo.setText("");
        txtAutor.setText("");
        txtIsbn.setText("");
        txtEditorial.setText("");
        spnStock.setValue(0);
        if (cmbCategoria.getItemCount() > 0) cmbCategoria.setSelectedIndex(0);
        tabla.clearSelection();
    }

    private void guardar() {
        Libro libro = new Libro(
                idSeleccionado,                         // 0 = insertar, otro = actualizar
                txtTitulo.getText().trim(),
                txtAutor.getText().trim(),
                txtIsbn.getText().trim(),
                txtEditorial.getText().trim(),
                (int) spnStock.getValue(),
                (Categoria) cmbCategoria.getSelectedItem()
        );

        String error = controller.guardar(libro);
        if (error == null) {
            JOptionPane.showMessageDialog(this, "Libro guardado correctamente.");
            cargarTabla();
            limpiar();
        } else {
            JOptionPane.showMessageDialog(this, error, "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void eliminar() {
        if (idSeleccionado == 0) {
            JOptionPane.showMessageDialog(this, "Selecciona un libro de la tabla.");
            return;
        }
        int resp = JOptionPane.showConfirmDialog(this, "¿Eliminar el libro seleccionado?",
                "Confirmar", JOptionPane.YES_NO_OPTION);
        if (resp != JOptionPane.YES_OPTION) return;

        String error = controller.eliminar(idSeleccionado);
        if (error == null) {
            JOptionPane.showMessageDialog(this, "Libro eliminado.");
            cargarTabla();
            limpiar();
        } else {
            JOptionPane.showMessageDialog(this, error, "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}