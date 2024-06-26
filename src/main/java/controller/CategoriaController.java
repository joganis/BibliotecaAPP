package controller;


import com.dao.CategoriaDAO;
import com.dao.LibroDAO;
import com.dao.PrestamoDAO;
import com.dao.UsuarioDAO;
import model.Categoria;
import views.CategoriaView;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableModel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;
import views.PrestamoView;

public class CategoriaController {

    private CategoriaView view;
    private CategoriaDAO dao;

    public CategoriaController(CategoriaView view, CategoriaDAO dao) {
        this.view = view;
        this.dao = dao;
        this.view.btnVolver.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JFrame frame = (JFrame) SwingUtilities.getWindowAncestor(view);
                frame.dispose(); // Cierra el JFrame actual

                // Abrir la nueva vista UsuarioView
                JFrame libroFrame = new JFrame("Gestión de Usuarios");
                PrestamoView view = new PrestamoView();
                PrestamoDAO dao = new PrestamoDAO();
                LibroDAO librosDao = new LibroDAO();
                UsuarioDAO usuarioDao = new UsuarioDAO();
                PrestamoController prestamo = new PrestamoController(view, dao, librosDao, usuarioDao);
                libroFrame.getContentPane().add(view);

                // Establecer el tamaño del nuevo marco, 
                // hacerlo visible y centrarlo en la pantalla
                libroFrame.pack();
                libroFrame.setVisible(true);
                libroFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); // Ajusta esto según tu lógica de cierre
                libroFrame.setLocationRelativeTo(null);
            }
        });
        this.view.btncrear.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                crearCategoria();
            }
        });
        this.view.btnMostrar.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                mostrarCategorias();
            }
        });
        this.view.btnActualizar.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (view.txtId.getText().isEmpty() || view.txtNombre.getText().isEmpty()) {
                    JOptionPane.showMessageDialog(view, "Por favor, seleccione una categoría de la tabla para actualizar.");
                } else {
                    actualizarCategoria();
                }
            }
        });
        this.view.btnEliminar.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (view.txtId.getText().isEmpty() || view.txtNombre.getText().isEmpty()) {
                    JOptionPane.showMessageDialog(view, "Por favor, seleccione una categoría de la tabla para eliminar.");
                } else {
                    int response = JOptionPane.showConfirmDialog(
                            view,
                            "¿Está seguro de que desea eliminar esta categoría?",
                            "Confirmar eliminación",
                            JOptionPane.YES_NO_OPTION,
                            JOptionPane.QUESTION_MESSAGE
                    );
                    if (response == JOptionPane.YES_OPTION) {
                        eliminarCategoria();
                    }
                }
            }
        });
        this.view.tablaCategoria.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                if (!e.getValueIsAdjusting() && view.tablaCategoria.getSelectedRow() != -1) {
                    mostrarDatosSeleccionados();
                }
            }
        });

        mostrarCategorias();
    }

    private void crearCategoria() {
        String nombre = view.txtNombre.getText();

        Categoria categoria = new Categoria(nombre);
        dao.crearCategoria(categoria);

        JOptionPane.showMessageDialog(view, "Categoría creada exitosamente.");
        limpiarCampos();
        mostrarCategorias();
    }

    private void mostrarCategorias() {
        List<Categoria> listaCategorias = dao.leerCategoria();
        DefaultTableModel model = (DefaultTableModel) view.tablaCategoria.getModel();
        model.setRowCount(0);

        for (Categoria categoria : listaCategorias) {
            model.addRow(new Object[]{categoria.getId(), categoria.getNombre()});
        }
    }

    private void actualizarCategoria() {
        int id = Integer.parseInt(view.txtId.getText());
        String nombre = view.txtNombre.getText();

        Categoria categoria = new Categoria(nombre, id);
        dao.actualizarCategoria(categoria);

        JOptionPane.showMessageDialog(view, "Categoría actualizada exitosamente.");
        limpiarCampos();
        mostrarCategorias();
    }

    private void eliminarCategoria() {
        int id = Integer.parseInt(view.txtId.getText());
        dao.eliminarCategoria(id);

        JOptionPane.showMessageDialog(view, "Categoría eliminada exitosamente.");
        limpiarCampos();
        mostrarCategorias();
    }

    private void limpiarCampos() {
        view.txtId.setText("");
        view.txtNombre.setText("");
    }

    private void mostrarDatosSeleccionados() {
        int row = view.tablaCategoria.getSelectedRow();
        view.txtId.setText(view.tablaCategoria.getValueAt(row, 0).toString());
        view.txtNombre.setText(view.tablaCategoria.getValueAt(row, 1).toString());
    }
}