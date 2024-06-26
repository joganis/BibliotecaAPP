package controller;

import com.dao.CategoriaDAO;
import com.dao.LibroDAO;
import com.dao.PrestamoDAO;
import model.Usuarios;
import com.dao.UsuarioDAO;
import controller.CategoriaController;
import java.awt.Frame;
import views.UsuarioView;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import views.CategoriaView;
import views.PrestamoView;

public class UsuarioController {

    private UsuarioView view;
    private UsuarioDAO dao;

    public UsuarioController(UsuarioView view, UsuarioDAO dao) {
        this.view = view;
        this.dao = dao;
        
        this.view.btnVolver.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                
                JFrame frame = (JFrame) SwingUtilities.getWindowAncestor(view);
                frame.dispose(); // Cierra el JFrame actual

                // Abrir la nueva vista UsuarioView
                JFrame prestamoFrame = new JFrame("Gestión de Usuarios");
                PrestamoView view = new PrestamoView();
                PrestamoDAO dao = new PrestamoDAO();
                LibroDAO librosDao = new LibroDAO();
                UsuarioDAO usuarioDao = new UsuarioDAO();
                PrestamoController prestamo = new PrestamoController(view, dao, librosDao, usuarioDao);
                prestamoFrame.getContentPane().add(view);

                // Establecer el tamaño del nuevo marco, 
                // hacerlo visible y centrarlo en la pantalla
                prestamoFrame.pack();
                prestamoFrame.setVisible(true);
                prestamoFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); // Ajusta esto según tu lógica de cierre
                prestamoFrame.setLocationRelativeTo(null);
            }
        });
        this.view.btnCrear.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                crearUsuario();
            }
        });
        this.view.btnMostrar.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                mostrarUsuarios();
            }
        });

        this.view.btnActualizar.addActionListener(new ActionListener() {
            @Override

            public void actionPerformed(ActionEvent e) {
                mostrarUsuarios();
                if (view.txtId.getText().isEmpty()
                        || view.txtNombre.getText().isEmpty()
                        || view.txtEmail.getText().isEmpty()
                        || view.txtTelefono.getText().isEmpty()) {
                    JOptionPane.showMessageDialog(view, "Por favor, seleccione un usuario de la tabla para actualizar.");
                } else {
                    actualizarUsuario();
                }

            }
        }
        );

        this.view.btnEliminar.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                mostrarUsuarios();
                if (view.txtId.getText().isEmpty()
                        || view.txtNombre.getText().isEmpty()
                        || view.txtEmail.getText().isEmpty()
                        || view.txtTelefono.getText().isEmpty()) {
                    JOptionPane.showMessageDialog(view, "Por favor, seleccione un usuario de la tabla para eliminar.");
                } else {
                    int response = JOptionPane.showConfirmDialog(
                            view,
                            "¿Está seguro de que desea eliminar este usuario?",
                            "Confirmar eliminación",
                            JOptionPane.YES_NO_OPTION,
                            JOptionPane.QUESTION_MESSAGE
                    );
                    if (response == JOptionPane.YES_OPTION) {
                        eliminarUsuario();
                    }
                }
            }
        }
        );
      //lisner dentro se la tabla- seleccionar una fila
        this.view.tablaUsario.getSelectionModel()
                .addListSelectionListener(new ListSelectionListener() {
                    @Override
                    public void valueChanged(ListSelectionEvent e
                    ) {
                        if (!e.getValueIsAdjusting() && view.tablaUsario.getSelectedRow() != -1) {
                               mostrarDatosSeleccionados();

                        }
                    }
                }
                );

        /*mostrarUsuarios();*/
    }

    private void crearUsuario() {
        String nombre = view.txtNombre.getText();
        String email = view.txtEmail.getText();
        String telefono = view.txtTelefono.getText();

        Usuarios usuario = new Usuarios(nombre, email, telefono);
        dao.crearUsuario(usuario);

        JOptionPane.showMessageDialog(view, "Usuario creado exitosamente.");
        limpiarCampos();
        /*mostrarUsuarios();*/ // 
    }
    
    private void mostrarUsuarios() {
        List<Usuarios> listaUsuarios = dao.leerUsuario();
        DefaultTableModel model = (DefaultTableModel) view.tablaUsario.getModel(); 
        model.setRowCount(0);

        for (Usuarios usuario : listaUsuarios) {
            model.addRow(new Object[]{usuario.getId(), usuario.getNombre(), usuario.getEmail(), usuario.getTelefono()});
        }
    }

    private void actualizarUsuario() {
        int id = Integer.parseInt(view.txtId.getText());
        String nombre = view.txtNombre.getText();
        String email = view.txtEmail.getText();
        String telefono = view.txtTelefono.getText();

        Usuarios usuario = new Usuarios(id, nombre, email, telefono);
        dao.actualizarUsuario(usuario);

        JOptionPane.showMessageDialog(view, "Usuario actualizado exitosamente.");
        limpiarCampos();
        mostrarUsuarios(); // Actualizar la tabla después de actualizar el usuario
    }

    private void eliminarUsuario() {
        int id = Integer.parseInt(view.txtId.getText());
        dao.eliminarUsuario(id);

        JOptionPane.showMessageDialog(view, "Usuario eliminado exitosamente.");
        limpiarCampos();
        mostrarUsuarios(); 
    }

    private void limpiarCampos() {
        view.txtId.setText("");
        view.txtNombre.setText("");
        view.txtEmail.setText("");
        view.txtTelefono.setText("");
    }

    private void mostrarDatosSeleccionados() {
        int row = view.tablaUsario.getSelectedRow();
        view.txtId.setText(view.tablaUsario.getValueAt(row, 0).toString());
        view.txtNombre.setText(view.tablaUsario.getValueAt(row, 1).toString());
        view.txtEmail.setText(view.tablaUsario.getValueAt(row, 2).toString());
        view.txtTelefono.setText(view.tablaUsario.getValueAt(row, 3).toString());
    }
}
