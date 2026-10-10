package com.puertogames.biblioteca.vista;

import com.puertogames.biblioteca.controlador.PrestamoController;
import com.puertogames.biblioteca.modelo.Estudiante;
import com.puertogames.biblioteca.modelo.Libro;
import com.puertogames.biblioteca.modelo.Prestamo;
import com.puertogames.biblioteca.modelo.Rol;
import com.puertogames.biblioteca.modelo.Usuario;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class VentanaPrestamos extends JFrame {

    private final PrestamoController controller = new PrestamoController();
    private final Estudiante estudianteFijo;   // null si es bibliotecario

    private final JComboBox<Estudiante> cmbEstudiante = new JComboBox<>();
    private final JComboBox<Libro> cmbLibro = new JComboBox<>();
    private final JButton btnPrestar = new JButton("Registrar préstamo");
    private final JProgressBar barra = new JProgressBar();
    private final JLabel lblEstado = new JLabel(" ");

    private List<Prestamo> activos = new ArrayList<>();
    private final DefaultTableModel modelo = new DefaultTableModel(
            new Object[]{"ID", "Estudiante", "Libro", "Fecha préstamo", "Vence", "Estado"}, 0) {
        @Override
        public boolean isCellEditable(int fila, int columna) {
            return false;
        }
    };
    private final JTable tabla = new JTable(modelo);

    public VentanaPrestamos(Usuario usuario) {
        setTitle("Préstamos y devoluciones");
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout(10, 10));

        // Si es estudiante, solo puede operar con su propia ficha
        if (usuario.getRol() == Rol.ESTUDIANTE) {
            estudianteFijo = controller.buscarEstudiantePorRut(usuario.getRut());
        } else {
            estudianteFijo = null;
        }

        // --- Panel superior: registrar prestamo ---
        JPanel superior = new JPanel(new GridLayout(0, 2, 8, 8));
        superior.setBorder(new EmptyBorder(10, 10, 0, 10));
        superior.add(new JLabel("Estudiante:"));
        superior.add(cmbEstudiante);
        superior.add(new JLabel("Libro (con stock):"));
        superior.add(cmbLibro);
        superior.add(lblEstado);
        superior.add(btnPrestar);
        superior.add(new JLabel());
        superior.add(barra);
        barra.setVisible(false);
        add(superior, BorderLayout.NORTH);

        // --- Centro: prestamos activos ---
        tabla.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        add(new JScrollPane(tabla), BorderLayout.CENTER);

        // --- Inferior: devolucion ---
        JButton btnDevolver = new JButton("Registrar devolución del préstamo seleccionado");
        JPanel inferior = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        inferior.add(btnDevolver);
        add(inferior, BorderLayout.SOUTH);

        btnPrestar.addActionListener(e -> prestar());
        btnDevolver.addActionListener(e -> devolver());

        cargarCombos();
        cargarTabla();
        setSize(850, 550);
        setLocationRelativeTo(null);
    }

    private void cargarCombos() {
        cmbEstudiante.removeAllItems();
        if (estudianteFijo != null) {
            cmbEstudiante.addItem(estudianteFijo);
            cmbEstudiante.setEnabled(false);
        } else {
            for (Estudiante e : controller.listarEstudiantes()) {
                cmbEstudiante.addItem(e);
            }
        }

        cmbLibro.removeAllItems();
        for (Libro l : controller.listarLibrosDisponibles()) {
            cmbLibro.addItem(l);
        }
    }

    private void cargarTabla() {
        modelo.setRowCount(0);
        activos = new ArrayList<>();
        for (Prestamo p : controller.listarActivos()) {
            // El estudiante solo ve sus propios prestamos
            if (estudianteFijo != null && p.getEstudiante().getId() != estudianteFijo.getId()) {
                continue;
            }
            activos.add(p);
            modelo.addRow(new Object[]{
                    p.getId(), p.getEstudiante().getNombre(), p.getLibro(),
                    p.getFechaPrestamo(), p.getFechaDevolucion(),
                    p.estaAtrasado() ? "ATRASADO" : "En plazo"
            });
        }
    }

    private void prestar() {
        Estudiante estudiante = (Estudiante) cmbEstudiante.getSelectedItem();
        Libro libro = (Libro) cmbLibro.getSelectedItem();

        // Mientras el hilo trabaja: boton desactivado y barra de progreso
        btnPrestar.setEnabled(false);
        barra.setIndeterminate(true);
        barra.setVisible(true);
        lblEstado.setText("Procesando préstamo...");

        controller.registrarPrestamoEnSegundoPlano(estudiante, libro, error -> {
            // Esto se ejecuta cuando el hilo termina (ya de vuelta en el hilo de la ventana)
            btnPrestar.setEnabled(true);
            barra.setVisible(false);
            lblEstado.setText(" ");

            if (error == null) {
                JOptionPane.showMessageDialog(this, "Préstamo registrado. Vence en 7 días.");
                cargarCombos();
                cargarTabla();
            } else {
                JOptionPane.showMessageDialog(this, error, "Error", JOptionPane.ERROR_MESSAGE);
            }
        });
    }

    private void devolver() {
        int fila = tabla.getSelectedRow();
        Prestamo p = (fila >= 0) ? activos.get(fila) : null;

        String error = controller.registrarDevolucion(p);
        if (error == null) {
            String extra = p.estaAtrasado() ? "\nATENCIÓN: la devolución se realizó con atraso." : "";
            JOptionPane.showMessageDialog(this, "Devolución registrada." + extra);
            cargarCombos();
            cargarTabla();
        } else {
            JOptionPane.showMessageDialog(this, error, "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}