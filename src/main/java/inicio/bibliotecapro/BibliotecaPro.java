package inicio.bibliotecapro;

import com.dao.CategoriaDAO;
import com.dao.LibroDAO;
import com.dao.LibrosCategoriaDAO;
import com.dao.PrestamoDAO;
import com.dao.UsuarioDAO;
import controller.LibroController;                                      
import controller.PrestamoController;
import controller.UsuarioController;
import java.awt.Window;
import views.UsuarioView;

import javax.swing.*;
import views.LibroView;
import views.PrestamoView;

public class BibliotecaPro {

    public static void main(String[] args) {
       
           
                PrestamoView view = new PrestamoView();
                PrestamoDAO dao = new PrestamoDAO();
                LibroDAO librosDao = new LibroDAO();
                UsuarioDAO usuarioDao = new UsuarioDAO();
                PrestamoController prestamo = new PrestamoController(view, dao, librosDao, usuarioDao);

                
                
                JFrame frame = new JFrame("Biblioteca");
                frame.getContentPane().add(view);
                frame.pack();
                frame.setVisible(true);
                
                frame.setLocationRelativeTo(null);
             
            
       
    }
}
