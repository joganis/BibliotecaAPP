package com.dao;

import config.ConexionBD;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import model.Usuarios;


public class UsuarioDAO {

    private Connection conexion;
    private ConexionBD conexionBD;

    public UsuarioDAO() {
        conexionBD = new ConexionBD();
        conexion = conexionBD.getConexion();
    }

    public void crearUsuario(Usuarios usuario) {
        String sql = "INSERT INTO usuarios ( nombre, email, telefono) VALUES (?,?,?)";
        try (PreparedStatement statement = conexion.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS)) {
            statement.setString(1, usuario.getNombre());
            statement.setString(2, usuario.getEmail());
            statement.setString(3, usuario.getTelefono());
            
            statement.executeUpdate();

        } catch (SQLException e) {
            System.out.println("Error al guardar los datos: " + e.getMessage());
        }
    }

    public List<Usuarios> leerUsuario() {
        List<Usuarios> usuario = new ArrayList<>();
        String sql = "SELECT * FROM usuarios";
        try (Statement statement = conexion.createStatement(); ResultSet resultSet = statement.executeQuery(sql)) {
            while (resultSet.next()) {
                int id = resultSet.getInt("id");
                String nombre = resultSet.getString("nombre");
                String email = resultSet.getString("email");
                String telefono = resultSet.getString("telefono");
               
                Usuarios usuario1 = new Usuarios(id, nombre, email,telefono);
               usuario.add(usuario1);
            }
        } catch (SQLException e) {
            System.out.println("Error al consultar los datos: " + e.getMessage());
        }
        return usuario;
    }

    public void actualizarUsuario(Usuarios usuario) {
        String sql = "UPDATE usuarios SET nombre = ?, email = ?, telefono = ? WHERE id = ?";
        try (PreparedStatement statement = conexion.prepareStatement(sql)) {
            statement.setString(1, usuario.getNombre());
            statement.setString(2, usuario.getEmail());
            statement.setString(3, usuario.getTelefono());
            statement.setInt(4, usuario.getId());
            statement.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Error al actualizar los datos: " + e.getMessage());
        }
    }

    public void eliminarUsuario(int id) {
        String sql = "DELETE FROM usuarios WHERE id = ?";
        try (PreparedStatement statement = conexion.prepareStatement(sql)) {
            statement.setInt(1, id);
            statement.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Error al eliminar los datos: " + e.getMessage());
        }
    }

    public boolean existeCategoria(int id) {
        String sql = "SELECT COUNT(*) AS count FROM Usuario WHERE id = ?";
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
