package controller;

import com.dao.LibroDAO;
import com.dao.CategoriaDAO;
import com.dao.LibrosCategoriaDAO;
import com.dao.PrestamoDAO;
import com.dao.UsuarioDAO;
import model.Libro;
import model.Categoria;
import model.LibrosCategoria;
import views.LibroView;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import views.PrestamoView;

public class LibroController {

    private LibroView view;
    private LibroDAO libroDao;
    private CategoriaDAO categoriaDao;
    private LibrosCategoriaDAO libroCategoriaDao;

    public LibroController(LibroView view, LibroDAO libroDao, CategoriaDAO categoriaDao, LibrosCategoriaDAO libroCategoriaDao) {
        this.view = view;
        this.libroDao = libroDao;
        this.categoriaDao = categoriaDao;
        this.libroCategoriaDao = libroCategoriaDao;
        
        this.view.btnVover.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                //
                // Cerrar la vista actual
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

        // Acción para Crear Libro
        this.view.btnCrearLibro.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                crearLibro();
            }
        });

        // Acción para Mostrar Libros
        this.view.btnMostrarLibro.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                mostrarLibros();
            }
        });

        // Acción para Actualizar Libro
        this.view.btnActualizarLibro.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (camposLibroVacios()) {
                    JOptionPane.showMessageDialog(view, "Por favor, seleccione un libro de la tabla para actualizar.");
                } else {
                    actualizarLibro();
                }
            }
        });

        // Acción para Eliminar Libro
        this.view.btnEliminarLibro.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (camposLibroVacios()) {
                    JOptionPane.showMessageDialog(view, "Por favor, seleccione un libro de la tabla para eliminar.");
                } else {
                    int response = JOptionPane.showConfirmDialog(
                            view,
                            "¿Está seguro de que desea eliminar este libro?",
                            "Confirmar eliminación",
                            JOptionPane.YES_NO_OPTION,
                            JOptionPane.QUESTION_MESSAGE
                    );
                    if (response == JOptionPane.YES_OPTION) {
                        eliminarLibro();
                    }
                }
            }
        });

        // Listener para selección de libro en la tabla
        this.view.tablaLibros.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                if (!e.getValueIsAdjusting() && view.tablaLibros.getSelectedRow() != -1) {
                    mostrarDatosSeleccionados();
                    cargarCategorias();
                    mostrarCategoriasLibro();
                }
            }
        });

        
        
        // Acciones para Asignar Categorías
        this.view.btnMostrarLIbroCatgoria.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
             
            }
        });

        this.view.btnAñadirLibrocategoria.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                añadirCategoria();
            }
        });

        this.view.btnEliminarLibroCategoria.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                eliminarCategoria();
            }
        });

        mostrarLibros();
        cargarCategorias();
    }

    private void crearLibro() {
        String titulo = view.txtTituloLibro.getText();
        String autor = view.txtAutorLibro.getText();
        String editorial = view.txtEditorialLibro.getText();
        String anioPublicacion = view.txtAnioPublicacionLibro.getText();
        String isbn = view.txtIsbnLibro.getText();

        Libro libro = new Libro(titulo, autor, editorial, anioPublicacion, isbn);
        libroDao.crearLibro(libro);

        JOptionPane.showMessageDialog(view, "Libro creado exitosamente.");
        limpiarCamposLibro();
        mostrarLibros();
    }

    private void mostrarLibros() {
        List<Libro> listaLibros = libroDao.leerLibros();
        DefaultTableModel model = (DefaultTableModel) view.tablaLibros.getModel();
        model.setRowCount(0);

        for (Libro libro : listaLibros) {
            model.addRow(new Object[]{libro.getId(), libro.getTitulo(), libro.getAutor(), libro.getEditorial(), libro.getAnioPublicacion(), libro.getIsbn()});
        }
    }

    private void actualizarLibro() {
        int id = Integer.parseInt(view.txtIdLibro.getText());
        String titulo = view.txtTituloLibro.getText();
        String autor = view.txtAutorLibro.getText();
        String editorial = view.txtEditorialLibro.getText();
        String anioPublicacion = view.txtAnioPublicacionLibro.getText();
        String isbn = view.txtIsbnLibro.getText();

        Libro libro = new Libro(id, titulo, autor, editorial, anioPublicacion, isbn);
        libroDao.actualizarLibro(libro);

        JOptionPane.showMessageDialog(view, "Libro actualizado exitosamente.");
        limpiarCamposLibro();
        mostrarLibros();
    }

    private void eliminarLibro() {
        int id = Integer.parseInt(view.txtIdLibro.getText());
        libroDao.eliminarLibro(id);

        JOptionPane.showMessageDialog(view, "Libro eliminado exitosamente.");
        limpiarCamposLibro();
        mostrarLibros();
    }

    private void limpiarCamposLibro() {
        view.txtIdLibro.setText("");
        view.txtTituloLibro.setText("");
        view.txtAutorLibro.setText("");
        view.txtEditorialLibro.setText("");
        view.txtAnioPublicacionLibro.setText("");
        view.txtIsbnLibro.setText("");
    }

    private void mostrarDatosSeleccionados() {
        int row = view.tablaLibros.getSelectedRow();
        view.txtIdLibro.setText(view.tablaLibros.getValueAt(row, 0).toString());
        view.txtTituloLibro.setText(view.tablaLibros.getValueAt(row, 1).toString());
        view.txtAutorLibro.setText(view.tablaLibros.getValueAt(row, 2).toString());
        view.txtEditorialLibro.setText(view.tablaLibros.getValueAt(row, 3).toString());
        view.txtAnioPublicacionLibro.setText(view.tablaLibros.getValueAt(row, 4).toString());
        view.txtIsbnLibro.setText(view.tablaLibros.getValueAt(row, 5).toString());

        // Actualizar el campo de nombre de película
        view.txtNombrePelicula.setText(view.txtTituloLibro.getText());
    }

    private boolean camposLibroVacios() {
        return view.txtIdLibro.getText().isEmpty()
                || view.txtTituloLibro.getText().isEmpty()
                || view.txtAutorLibro.getText().isEmpty()
                || view.txtEditorialLibro.getText().isEmpty()
                || view.txtAnioPublicacionLibro.getText().isEmpty()
                || view.txtIsbnLibro.getText().isEmpty();
    }

    
    private void cargarCategorias() {
        List<Categoria> categorias = categoriaDao.leerCategoria();
        view.bxMostrarCategorias.removeAllItems();
        for (Categoria categoria : categorias) {
            view.bxMostrarCategorias.addItem(categoria);
        }
    }

    private void añadirCategoria() {
        int idLibro = Integer.parseInt(view.txtIdLibro.getText());
        Categoria categoria = (Categoria) view.bxMostrarCategorias.getSelectedItem();
        if (categoria != null) {
            LibrosCategoria libroCategoria = new LibrosCategoria(idLibro, categoria.getId());
            libroCategoriaDao.agregarCategoriaALibro(libroCategoria);

            JOptionPane.showMessageDialog(view, "Categoría añadida al libro exitosamente.");
            mostrarCategoriasLibro();
        } else {
            JOptionPane.showMessageDialog(view, "Por favor, seleccione una categoría.");
        }
    }

    private void eliminarCategoria() {
        int selectedRow = view.tablaMostarcategoriasPeliculas.getSelectedRow();
        if (selectedRow != -1) {
            int idLibro = Integer.parseInt(view.txtIdLibro.getText());
            int idCategoria = Integer.parseInt(view.tablaMostarcategoriasPeliculas.getValueAt(selectedRow, 0).toString());
            libroCategoriaDao.eliminarCategoriasDeLibro(idLibro);

            JOptionPane.showMessageDialog(view, "Categoría eliminada del libro exitosamente.");
            mostrarCategoriasLibro();
        } else {
            JOptionPane.showMessageDialog(view, "Por favor, seleccione una categoría de la tabla para eliminar.");
        }
    }

  

    private void mostrarCategoriasLibro() {
        int idLibro = Integer.parseInt(view.txtIdLibro.getText());
        List<Categoria> categorias = libroCategoriaDao.obtenerCategoriasDeLibro(idLibro);
        DefaultTableModel model = (DefaultTableModel) view.tablaMostarcategoriasPeliculas.getModel();
        model.setRowCount(0);

        for (Categoria categoria : categorias) {
            model.addRow(new Object[]{categoria.getId(), categoria.getNombre()});
        }
    }
}
