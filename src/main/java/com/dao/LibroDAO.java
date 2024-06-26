package com.dao;

import config.ConexionBD;
import model.Categoria;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import model.Libro;

public class LibroDAO {

    private Connection conexion;
    private ConexionBD conexionBD;

    public LibroDAO() {
        conexionBD = new ConexionBD();
        conexion = conexionBD.getConexion();
    }

    public void crearLibro(Libro libro) {
        String sql = "INSERT INTO libros (titulo, autor, editorial, anio_publicacion, isbn) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement statement = conexion.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS)) {
            statement.setString(1, libro.getTitulo());
            statement.setString(2, libro.getAutor());
            statement.setString(3, libro.getEditorial());
            statement.setString(4, libro.getAnioPublicacion());
            statement.setString(5, libro.getIsbn());
            statement.executeUpdate();

            try (ResultSet generatedKeys = statement.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    libro.setId(generatedKeys.getInt(1));
                } else {
                    throw new SQLException("No se pudo obtener el ID del libro insertado.");
                }
            }
        } catch (SQLException e) {
            System.out.println("Error al guardar los datos: " + e.getMessage());
        }
    }

    public List<Libro> leerLibros() {
        List<Libro> libros = new ArrayList<>();
        String sql = "SELECT * FROM libros";
        try (Statement statement = conexion.createStatement(); ResultSet resultSet = statement.executeQuery(sql)) {
            while (resultSet.next()) {
                int id = resultSet.getInt("id");
                String titulo = resultSet.getString("titulo");
                String autor = resultSet.getString("autor");
                String editorial = resultSet.getString("editorial");
                String anioPublicacion = resultSet.getString("anio_publicacion");
                String isbn = resultSet.getString("isbn");
                Libro libro = new Libro(id, titulo, autor, editorial, anioPublicacion, isbn);
                libros.add(libro);
            }
        } catch (SQLException e) {
            System.out.println("Error al consultar los datos: " + e.getMessage());
        }
        return libros;
    }

    public void actualizarLibro(Libro libro) {
        String sql = "UPDATE libros SET titulo = ?, autor = ?, editorial = ?, anio_publicacion = ?, isbn = ? WHERE id = ?";
        try (PreparedStatement statement = conexion.prepareStatement(sql)) {
            statement.setString(1, libro.getTitulo());
            statement.setString(2, libro.getAutor());
            statement.setString(3, libro.getEditorial());
            statement.setString(4, libro.getAnioPublicacion());
            statement.setString(5, libro.getIsbn());
            statement.setInt(6, libro.getId());
            statement.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Error al actualizar los datos: " + e.getMessage());
        }
    }

    public void eliminarLibro(int id) {
        String sql = "DELETE FROM libros WHERE id = ?";
        try (PreparedStatement statement = conexion.prepareStatement(sql)) {
            statement.setInt(1, id);
            statement.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Error al eliminar los datos: " + e.getMessage());
        }
    }

    public boolean existeLibro(int id) {
        String sql = "SELECT COUNT(*) AS count FROM libros WHERE id = ?";
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
