package com.puertogames.biblioteca.vista;

import com.puertogames.biblioteca.controlador.EstudianteController;
import com.puertogames.biblioteca.modelo.Estudiante;
import com.puertogames.biblioteca.modelo.Prestamo;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class VentanaEstudiantes extends JFrame {

    private final EstudianteController controller = new EstudianteController();
    private List<Estudiante> estudiantes = new ArrayList<>();
    private int idSeleccionado = 0;

    private final DefaultTableModel modelo = new DefaultTableModel(
            new Object[]{"ID", "Nombre", "RUT", "Curso", "Correo"}, 0) {
        @Override
        public boolean isCellEditable(int fila, int columna) {
            return false;
        }
    };
    private final JTable tabla = new JTable(modelo);

    private final JTextField txtNombre = new JTextField(20);
    private final JTextField txtRut = new JTextField(20);
    private final JTextField txtCurso = new JTextField(20);
    private final JTextField txtCorreo = new JTextField(20);

    public VentanaEstudiantes() {
        setTitle("Gestión de estudiantes");
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout(10, 10));

        tabla.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tabla.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) cargarSeleccion();
        });
        add(new JScrollPane(tabla), BorderLayout.CENTER);

        JPanel formulario = new JPanel(new GridLayout(0, 4, 8, 8));
        formulario.add(new JLabel("Nombre:")); formulario.add(txtNombre);
        formulario.add(new JLabel("RUT:"));    formulario.add(txtRut);
        formulario.add(new JLabel("Curso:"));  formulario.add(txtCurso);
        formulario.add(new JLabel("Correo:")); formulario.add(txtCorreo);

        JButton btnNuevo = new JButton("Nuevo");
        JButton btnGuardar = new JButton("Guardar");
        JButton btnEliminar = new JButton("Eliminar");
        JButton btnHistorial = new JButton("Ver historial");
        btnNuevo.addActionListener(e -> limpiar());
        btnGuardar.addActionListener(e -> guardar());
        btnEliminar.addActionListener(e -> eliminar());
        btnHistorial.addActionListener(e -> verHistorial());

        JPanel botones = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        botones.add(btnHistorial);
        botones.add(btnNuevo);
        botones.add(btnGuardar);
        botones.add(btnEliminar);

        JPanel inferior = new JPanel(new BorderLayout(5, 5));
        inferior.setBorder(new EmptyBorder(10, 10, 10, 10));
        inferior.add(formulario, BorderLayout.CENTER);
        inferior.add(botones, BorderLayout.SOUTH);
        add(inferior, BorderLayout.SOUTH);

        cargarTabla();
        setSize(850, 500);
        setLocationRelativeTo(null);
    }

    private void cargarTabla() {
        modelo.setRowCount(0);
        estudiantes = controller.listar();
        for (Estudiante e : estudiantes) {
            modelo.addRow(new Object[]{e.getId(), e.getNombre(), e.getRut(), e.getCurso(), e.getCorreo()});
        }
    }

    private void cargarSeleccion() {
        int fila = tabla.getSelectedRow();
        if (fila < 0) return;
        Estudiante e = estudiantes.get(fila);
        idSeleccionado = e.getId();
        txtNombre.setText(e.getNombre());
        txtRut.setText(e.getRut());
        txtCurso.setText(e.getCurso());
        txtCorreo.setText(e.getCorreo());
    }

    private void limpiar() {
        idSeleccionado = 0;
        txtNombre.setText("");
        txtRut.setText("");
        txtCurso.setText("");
        txtCorreo.setText("");
        tabla.clearSelection();
    }

    private void guardar() {
        // Orden de TU constructor: id, nombre, rut, curso, correo
        Estudiante estudiante = new Estudiante(
                idSeleccionado,
                txtNombre.getText().trim(),
                txtRut.getText().trim(),
                txtCurso.getText().trim(),
                txtCorreo.getText().trim()
        );
        String error = controller.guardar(estudiante);
        if (error == null) {
            JOptionPane.showMessageDialog(this, "Estudiante guardado correctamente.");
            cargarTabla();
            limpiar();
        } else {
            JOptionPane.showMessageDialog(this, error, "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void eliminar() {
        if (idSeleccionado == 0) {
            JOptionPane.showMessageDialog(this, "Selecciona un estudiante de la tabla.");
            return;
        }
        int resp = JOptionPane.showConfirmDialog(this, "¿Eliminar el estudiante seleccionado?",
                "Confirmar", JOptionPane.YES_NO_OPTION);
        if (resp != JOptionPane.YES_OPTION) return;

        String error = controller.eliminar(idSeleccionado);
        if (error == null) {
            JOptionPane.showMessageDialog(this, "Estudiante eliminado.");
            cargarTabla();
            limpiar();
        } else {
            JOptionPane.showMessageDialog(this, error, "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    // Muestra en una tabla todos los prestamos del estudiante seleccionado
    private void verHistorial() {
        if (idSeleccionado == 0) {
            JOptionPane.showMessageDialog(this, "Selecciona un estudiante de la tabla.");
            return;
        }
        DefaultTableModel modeloHist = new DefaultTableModel(
                new Object[]{"Libro", "Fecha préstamo", "Vence", "Estado"}, 0);
        for (Prestamo p : controller.historial(idSeleccionado)) {
            String estado = p.isDevuelto() ? "Devuelto"
                    : (p.estaAtrasado() ? "ATRASADO" : "En préstamo");
            modeloHist.addRow(new Object[]{p.getLibro(), p.getFechaPrestamo(), p.getFechaDevolucion(), estado});
        }
        JTable tablaHist = new JTable(modeloHist);
        JScrollPane scroll = new JScrollPane(tablaHist);
        scroll.setPreferredSize(new Dimension(550, 200));
        JOptionPane.showMessageDialog(this, scroll, "Historial de " + txtNombre.getText(),
                JOptionPane.PLAIN_MESSAGE);
    }
}