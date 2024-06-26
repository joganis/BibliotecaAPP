package com.dao;

import config.ConexionBD;
import model.Categoria;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import model.LibrosCategoria;

public class LibrosCategoriaDAO {

    private Connection conexion;
    private ConexionBD conexionBD;

    public LibrosCategoriaDAO() {
        conexionBD = new ConexionBD();
        conexion = conexionBD.getConexion();
    }

    public void agregarCategoriaALibro(LibrosCategoria libroCategoria) {
        String sql = "INSERT INTO libros_categorias (libro_id, categoria_id) VALUES (?, ?)";
        try (PreparedStatement statement = conexion.prepareStatement(sql)) {
            statement.setInt(1, libroCategoria.getIdLibro());
            statement.setInt(2, libroCategoria.getIdCategoria());
            statement.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Error al agregar la categoría al libro: " + e.getMessage());
        }
    }

    public List<Categoria> obtenerCategoriasDeLibro(int libroId) {
        List<Categoria> categorias = new ArrayList<>();
        String sql = "SELECT c.id, c.nombre "
                + "FROM categorias c "
                + "JOIN libros_categorias lc ON c.id = lc.categoria_id "
                + "WHERE lc.libro_id = ?";
        try (PreparedStatement statement = conexion.prepareStatement(sql)) {
            statement.setInt(1, libroId);
            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    int id = resultSet.getInt("id");
                    String nombre = resultSet.getString("nombre");
                    Categoria categoria = new Categoria(nombre, id);
                    categorias.add(categoria);
                }
            }
        } catch (SQLException e) {
            System.out.println("Error al obtener las categorías del libro: " + e.getMessage());
        }
        return categorias;
    }

    public void eliminarCategoriasDeLibro(int libroId) {
        String sql = "DELETE FROM libros_categorias WHERE libro_id = ?";
        try (PreparedStatement statement = conexion.prepareStatement(sql)) {
            statement.setInt(1, libroId);
            statement.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Error al eliminar las categorías del libro: " + e.getMessage());
        }
    }

    public void cerrarConexion() {
        conexionBD.closeConexion(conexion);
    }
}
