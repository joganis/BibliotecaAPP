package controller;

import com.dao.CategoriaDAO;
import com.dao.LibroDAO;
import com.dao.LibrosCategoriaDAO;
import com.dao.PrestamoDAO;
import com.dao.UsuarioDAO;
import model.Prestamo;
import model.Libro;
import model.Usuarios;
import views.PrestamoView;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import views.CategoriaView;
import views.LibroView;
import views.UsuarioView;

public class PrestamoController {

    private PrestamoView view;
    private LibroDAO libroDao;
    private UsuarioDAO usuarioDao;
    private PrestamoDAO prestamoDao;

    public PrestamoController(PrestamoView view, PrestamoDAO prestamoDao, LibroDAO libroDao, UsuarioDAO usuarioDao) {
        this.view = view;
        this.libroDao = libroDao;
        this.usuarioDao = usuarioDao;
        this.prestamoDao = prestamoDao;

        // cargar datos
        cargarLibros();
        cargarUsuarios();
        
        //boto para ir usuarios
        this.view.btnGestionUsuarios.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Cerrar la vista actual
                JFrame frame = (JFrame) SwingUtilities.getWindowAncestor(view);
                frame.dispose(); // Cierra el JFrame actual

                // Abrir la nueva vista UsuarioView
                JFrame usuarioFrame = new JFrame("Gestión de Usuarios");
                UsuarioView userView = new UsuarioView();
                UsuarioDAO userDao = new UsuarioDAO();// Suponiendo que UsuarioView es la vista que quieres abrir
                UsuarioController user = new UsuarioController (userView, userDao );
                usuarioFrame.getContentPane().add(userView);

                // Establecer el tamaño del nuevo marco, 
                // hacerlo visible y centrarlo en la pantalla
                usuarioFrame.pack();
                usuarioFrame.setVisible(true);
                usuarioFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); // Ajusta esto según tu lógica de cierre
                usuarioFrame.setLocationRelativeTo(null);
            }
        });
        //boton para ir a libros
        this.view.btnGestionLibros.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Cerrar la vista actual
                JFrame frame = (JFrame) SwingUtilities.getWindowAncestor(view);
                frame.dispose(); // Cierra el JFrame actual

                // Abrir la nueva vista UsuarioView
                JFrame libroFrame = new JFrame("Gestión de Usuarios");
                LibroView libroView = new LibroView(); // Suponiendo que UsuarioView es la vista que quieres abrir
                LibroDAO librodao = new LibroDAO();
                CategoriaDAO catDao = new CategoriaDAO();
                LibrosCategoriaDAO lbCaDao = new LibrosCategoriaDAO();
                LibroController libController = new LibroController(libroView, librodao, catDao, lbCaDao );
                libroFrame.getContentPane().add(libroView);

                // Establecer el tamaño del nuevo marco, 
                // hacerlo visible y centrarlo en la pantalla
                libroFrame.pack();
                libroFrame.setVisible(true);
                libroFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); // Ajusta esto según tu lógica de cierre
                libroFrame.setLocationRelativeTo(null);
            }
        });
        //boton para ia a categorias
        this.view.btnGestionarCategorias.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Cerrar la vista actual
                 JFrame frame = (JFrame) SwingUtilities.getWindowAncestor(view);
                frame.dispose(); // Cierra el JFrame actual

                // Abrir la nueva vista UsuarioView
                JFrame libroFrame = new JFrame("Gestión de Usuarios");
                CategoriaDAO catDao = new CategoriaDAO();
                CategoriaView view = new CategoriaView ();
                CategoriaController cont = new CategoriaController(view, catDao);
                libroFrame.getContentPane().add(view);

                // Establecer el tamaño del nuevo marco, 
                // hacerlo visible y centrarlo en la pantalla
                libroFrame.pack();
                libroFrame.setVisible(true);
                libroFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); // Ajusta esto según tu lógica de cierre
                libroFrame.setLocationRelativeTo(null);
            }
        });
        // boton  para crear
        this.view.btnCrear.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                crearPrestamo();
            }
        });

        // boton para editar
        this.view.btnEditar.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                mostrarPrestamos();
            }
        });

        // boton para actualizar
        this.view.btnActualizar.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (camposPrestamoVacios()) {
                    JOptionPane.showMessageDialog(view, "Por favor, seleccione un préstamo de la tabla para actualizar.");
                } else {
                    actualizarPrestamo();
                }
            }
        });

        //boton para eliminar
        this.view.btnEliminar.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (camposPrestamoVacios()) {
                    JOptionPane.showMessageDialog(view, "Por favor, seleccione un préstamo de la tabla para eliminar.");
                } else {
                    int response = JOptionPane.showConfirmDialog(
                            view,
                            "¿Está seguro de que desea eliminar este préstamo?",
                            "Confirmar eliminación",
                            JOptionPane.YES_NO_OPTION,
                            JOptionPane.QUESTION_MESSAGE
                    );
                    if (response == JOptionPane.YES_OPTION) {
                        eliminarPrestamo();
                    }
                }
            }
        });

        // accion de la tabla
        this.view.tablaEventos.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && view.tablaEventos.getSelectedRow() != -1) {
                mostrarDatosSeleccionados();
            }
        });

        // mostrar los datos en tabla
        mostrarPrestamos();
    }

    private void crearPrestamo() {
        try {
            Libro libroSeleccionado = (Libro) view.cmbLibros.getSelectedItem();
            Usuarios usuarioSeleccionado = (Usuarios) view.cmbUsuarios.getSelectedItem();

            if (libroSeleccionado == null || usuarioSeleccionado == null
                    || view.jDatePrestamo.getDate() == null || view.jDateDevolucion.getDate() == null) {
                JOptionPane.showMessageDialog(view, "Por favor, complete todos los campos.");
                return;
            }

            LocalDate fechaPrestamo = convertToLocalDate(view.jDatePrestamo.getDate());
            LocalDate fechaDevolucion = convertToLocalDate(view.jDateDevolucion.getDate());

            if (fechaDevolucion.isBefore(fechaPrestamo)) {
                JOptionPane.showMessageDialog(view, "La fecha de devolución no puede ser anterior a la fecha de préstamo.");
                return;
            }

            Prestamo prestamo = new Prestamo(libroSeleccionado.getId(), usuarioSeleccionado.getId(), fechaPrestamo, fechaDevolucion);

            prestamoDao.crearPrestamo(prestamo);

            JOptionPane.showMessageDialog(view, "Préstamo creado exitosamente.");
            limpiarCamposPrestamo();
            mostrarPrestamos();

        } catch (DateTimeParseException e) {
            JOptionPane.showMessageDialog(view, "Formato de fecha incorrecto. Por favor, use el formato dd/MM/yyyy.");
        } catch (Exception  e) {
            JOptionPane.showMessageDialog(view, "Ocurrió un error al crear el préstamo: " + e.getMessage());
        }
    }

    private void mostrarPrestamos() {
        try {
            List<Prestamo> listaPrestamos = prestamoDao.leerPrestamos();
            DefaultTableModel model = (DefaultTableModel) view.tablaEventos.getModel();
            model.setRowCount(0);

            for (Prestamo prestamo : listaPrestamos) {
                model.addRow(new Object[]{prestamo.getId(), prestamo.getLibroId(), prestamo.getUsuarioId(), prestamo.getFechaPrestamo(), prestamo.getFechaDevolucion()});
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(view, "Ocurrió un error al mostrar los préstamos: " + e.getMessage());
        }
    }

    private void actualizarPrestamo() {
        try {
            int id = Integer.parseInt(view.txtId.getText());
            Libro libroSeleccionado = (Libro) view.cmbLibros.getSelectedItem();
            Usuarios usuarioSeleccionado = (Usuarios) view.cmbUsuarios.getSelectedItem();

            if (libroSeleccionado == null || usuarioSeleccionado == null
                    || view.jDatePrestamo.getDate() == null || view.jDateDevolucion.getDate() == null) {
                JOptionPane.showMessageDialog(view, "Por favor, complete todos los campos.");
                return;
            }

            LocalDate fechaPrestamo = convertToLocalDate(view.jDatePrestamo.getDate());
            LocalDate fechaDevolucion = convertToLocalDate(view.jDateDevolucion.getDate());

            if (fechaDevolucion.isBefore(fechaPrestamo)) {
                JOptionPane.showMessageDialog(view, "La fecha de devolución no puede ser anterior a la fecha de préstamo.");
                return;
            }

            Prestamo prestamo = new Prestamo(id, libroSeleccionado.getId(), usuarioSeleccionado.getId(), fechaPrestamo, fechaDevolucion);

            prestamoDao.actualizarPrestamo(prestamo);

            JOptionPane.showMessageDialog(view, "Préstamo actualizado exitosamente.");
            limpiarCamposPrestamo();
            mostrarPrestamos();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(view, "Ocurrió un error al actualizar el préstamo: " + e.getMessage());
        }
    }

    private void eliminarPrestamo() {
        try {
            int id = Integer.parseInt(view.txtId.getText());

            // Lógica para eliminar el préstamo de la base de datos
            prestamoDao.eliminarPrestamo(id);

            JOptionPane.showMessageDialog(view, "Préstamo eliminado exitosamente.");
            limpiarCamposPrestamo();
            mostrarPrestamos();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(view, "Ocurrió un error al eliminar el préstamo: " + e.getMessage());
        }
    }

    private void limpiarCamposPrestamo() {
        view.txtId.setText("");
        view.cmbLibros.setSelectedIndex(-1); // Limpiar selección de libro
        view.cmbUsuarios.setSelectedIndex(-1); // Limpiar selección de usuario
        view.jDatePrestamo.setDate(null);
        view.jDateDevolucion.setDate(null);
    }
   /* private void mostrarDatosSeleccionados() {
        List<Prestamo> listaPrestamo = prestamoDao.leerPrestamos();
        DefaultTableModel model = (DefaultTableModel) view.tablaEventos.getModel(); 
        model.setRowCount(0);

        for (Prestamo prestamo : listaPrestamo) {
            model.addRow(new Object[]{prestamo.getId(), prestamo.getLibroId(), prestamo.getUsuarioId(), prestamo.getFechaPrestamo(), prestamo.getFechaDevolucion()});
        }
    }*/

    private void mostrarDatosSeleccionados() {
        int row = view.tablaEventos.getSelectedRow();
        if (row != -1) {
            view.txtId.setText(view.tablaEventos.getValueAt(row, 0).toString());
            String nombreLibroSeleccionado = view.tablaEventos.getValueAt(row, 1).toString();

            // Buscar el libro correspondiente en el JComboBox de libros
            for (int i = 0; i < view.cmbLibros.getItemCount(); i++) {
                String nombreLibroEnComboBox = (String) view.cmbLibros.getItemAt(i);
                if (nombreLibroEnComboBox.equals(nombreLibroSeleccionado)) {
                    view.cmbLibros.setSelectedIndex(i);
                    break;
                }
            }

            String nombreUsuarioSeleccionado = view.tablaEventos.getValueAt(row, 2).toString();

            // Buscar el libro correspondiente en el JComboBox de libros
            for (int i = 0; i < view.cmbUsuarios.getItemCount(); i++) {
                String nombreUsuarioEnComboBox = (String) view.cmbLibros.getItemAt(i);
                if (nombreUsuarioEnComboBox.equals(nombreUsuarioSeleccionado)) {
                    view.cmbLibros.setSelectedIndex(i);
                    break;
                }
            }

            LocalDate fechaPrestamo = LocalDate.parse(view.tablaEventos.getValueAt(row, 3).toString());
            LocalDate fechaDevolucion = LocalDate.parse(view.tablaEventos.getValueAt(row, 4).toString());
            view.jDatePrestamo.setDate(java.sql.Date.valueOf(fechaPrestamo));
            view.jDateDevolucion.setDate(java.sql.Date.valueOf(fechaDevolucion));
        }
        else {
            JOptionPane.showMessageDialog(view, "selecione una fila.");
        }
    }

   // covertir fechas
    private LocalDate convertToLocalDate(java.util.Date dateToConvert) {
        return dateToConvert.toInstant().atZone(java.time.ZoneId.systemDefault()).toLocalDate();
    }
    

    private boolean camposPrestamoVacios() {
        return view.txtId.getText().isEmpty()
                || view.jDatePrestamo.getDate() == null
                || view.jDateDevolucion.getDate() == null;
    }

    private void cargarLibros() {
        try {
            List<Libro> libros = libroDao.leerLibros();
            view.cmbLibros.removeAllItems();
            for (Libro libro : libros) {
                view.cmbLibros.addItem(libro); 
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(view, "Error al cargar libros: " + e.getMessage());
        }
    }
 

    private void cargarUsuarios() {
       try {
           List<Usuarios> usuario = usuarioDao.leerUsuario();
           view.cmbUsuarios.removeAllItems();
           for (Usuarios user : usuario){
               view.cmbUsuarios.addItem(user);
           }
       } catch (Exception e) {
           JOptionPane.showMessageDialog(view, "Error al cargar los >Usuarios: " + e.getMessage());
       }
    }
}