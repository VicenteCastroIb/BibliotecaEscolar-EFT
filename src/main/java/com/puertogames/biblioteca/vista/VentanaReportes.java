package com.puertogames.biblioteca.vista;

import com.puertogames.biblioteca.controlador.ReporteController;
import com.puertogames.biblioteca.modelo.Estudiante;
import com.puertogames.biblioteca.modelo.Prestamo;
import com.puertogames.biblioteca.modelo.Rol;
import com.puertogames.biblioteca.modelo.Usuario;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

public class VentanaReportes extends JFrame {

    private final ReporteController controller = new ReporteController();

    public VentanaReportes(Usuario usuario) {
        setTitle("Reportes");
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        JTabbedPane pestanas = new JTabbedPane();
        pestanas.addTab("Libros más prestados", crearMasPrestados());
        pestanas.addTab("Libros en préstamo", crearEnPrestamo());
        pestanas.addTab("Historial por estudiante", crearHistorial(usuario));
        add(pestanas);

        setSize(800, 500);
        setLocationRelativeTo(null);
    }

    // Crea una tabla de solo lectura con las columnas indicadas
    private DefaultTableModel crearModelo(String... columnas) {
        return new DefaultTableModel(columnas, 0) {
            @Override
            public boolean isCellEditable(int fila, int columna) {
                return false;
            }
        };
    }

    private JComponent crearMasPrestados() {
        DefaultTableModel modelo = crearModelo("Título", "Cantidad de préstamos");
        for (Object[] fila : controller.librosMasPrestados()) {
            modelo.addRow(fila);
        }
        return new JScrollPane(new JTable(modelo));
    }

    private JComponent crearEnPrestamo() {
        DefaultTableModel modelo = crearModelo("Libro", "Estudiante", "RUT", "Vence", "Estado");
        for (Prestamo p : controller.librosEnPrestamo()) {
            modelo.addRow(new Object[]{
                    p.getLibro(), p.getEstudiante().getNombre(), p.getEstudiante().getRut(),
                    p.getFechaDevolucion(), p.estaAtrasado() ? "ATRASADO" : "En plazo"
            });
        }
        return new JScrollPane(new JTable(modelo));
    }

    private JComponent crearHistorial(Usuario usuario) {
        JComboBox<Estudiante> cmbEstudiante = new JComboBox<>();
        if (usuario.getRol() == Rol.ESTUDIANTE) {
            Estudiante propio = controller.buscarEstudiantePorRut(usuario.getRut());
            if (propio != null) cmbEstudiante.addItem(propio);
            cmbEstudiante.setEnabled(false);   // el estudiante solo ve su historial
        } else {
            for (Estudiante e : controller.listarEstudiantes()) cmbEstudiante.addItem(e);
        }

        DefaultTableModel modelo = crearModelo("Libro", "Fecha préstamo", "Vence", "Estado");
        JButton btnVer = new JButton("Ver historial");
        btnVer.addActionListener(e -> {
            modelo.setRowCount(0);
            Estudiante est = (Estudiante) cmbEstudiante.getSelectedItem();
            if (est == null) return;
            for (Prestamo p : controller.historialEstudiante(est.getId())) {
                String estado = p.isDevuelto() ? "Devuelto"
                        : (p.estaAtrasado() ? "ATRASADO" : "En préstamo");
                modelo.addRow(new Object[]{p.getLibro(), p.getFechaPrestamo(), p.getFechaDevolucion(), estado});
            }
        });

        JPanel superior = new JPanel(new FlowLayout(FlowLayout.LEFT));
        superior.add(new JLabel("Estudiante:"));
        superior.add(cmbEstudiante);
        superior.add(btnVer);

        JPanel panel = new JPanel(new BorderLayout());
        panel.add(superior, BorderLayout.NORTH);
        panel.add(new JScrollPane(new JTable(modelo)), BorderLayout.CENTER);
        return panel;
    }
}