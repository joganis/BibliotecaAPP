package com.dao;

import config.ConexionBD;
import model.Categoria;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import model.Libro;

public class CategoriaDAO {

    private Connection conexion;
    private ConexionBD conexionBD;

    public CategoriaDAO() {
        conexionBD = new ConexionBD();
        conexion = conexionBD.getConexion();
    }

    public void crearCategoria(Categoria categoria) {
        String sql = "INSERT INTO categorias (nombre) VALUES (?)";
        try (PreparedStatement statement = conexion.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS)) {
            statement.setString(1, categoria.getNombre());
            
            statement.executeUpdate();

            try (ResultSet generatedKeys = statement.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    categoria.setId(generatedKeys.getInt(1));
                } else {
                    throw new SQLException("No se pudo obtener el ID del libro insertado.");
                }
            }
        } catch (SQLException e) {
            System.out.println("Error al guardar los datos: " + e.getMessage());
        }
    }

    public List<Categoria> leerCategoria() {
        List<Categoria> categoriass = new ArrayList<>();
        String sql = "SELECT * FROM categorias";
        try (Statement statement = conexion.createStatement(); ResultSet resultSet = statement.executeQuery(sql)) {
            while (resultSet.next()) {
                int id = resultSet.getInt("id");
                String titulo = resultSet.getString("nombre");
               
                Categoria categoria = new Categoria(titulo, id);
                categoriass.add(categoria);
            }
        } catch (SQLException e) {
            System.out.println("Error al consultar los datos: " + e.getMessage());
        }
        return categoriass;
    }

    public void actualizarCategoria(Categoria categoria) {
        String sql = "UPDATE categorias SET nombre = ? WHERE id = ?";
        try (PreparedStatement statement = conexion.prepareStatement(sql)) {
            statement.setString(1, categoria.getNombre());
            statement.setInt(2, categoria.getId());
            statement.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Error al actualizar los datos: " + e.getMessage());
        }
    }

    public void eliminarCategoria(int id) {
        String sql = "DELETE FROM categorias WHERE id = ?";
        try (PreparedStatement statement = conexion.prepareStatement(sql)) {
            statement.setInt(1, id);
            statement.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Error al eliminar los datos: " + e.getMessage());
        }
    }

    public boolean existeCategoria(int id) {
        String sql = "SELECT COUNT(*) AS count FROM categorias WHERE id = ?";
        try (PreparedStatement statement = conexion.prepareStatement(sql)) {
            statement.setInt(1, id);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    int count = resultSet.getInt("count");
                    return count > 0;
                }
            }
        } catch (SQLException e) {
            System.out.println("Error al verificar la existencia del libro: " + e.getMessage());
        }
        return false;
    }

    public void cerrarConexion() {
        conexionBD.closeConexion(conexion);
    }
}
