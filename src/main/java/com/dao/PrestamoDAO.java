package com.dao;

import config.ConexionBD;
import model.Categoria;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import model.Libro;
import model.Prestamo;

public class PrestamoDAO {

    private Connection conexion;
    private ConexionBD conexionBD;

    public PrestamoDAO() {
        conexionBD = new ConexionBD();
        conexion = conexionBD.getConexion();
    }

    public void crearPrestamo(Prestamo prestamo) {
        String sql = "INSERT INTO prestamos (libro_id, usuario_id, fecha_prestamo, fecha_devolucion) VALUES (?, ?, ?, ?)";
        try (PreparedStatement statement = conexion.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS)) {
            statement.setInt(1, prestamo.getLibroId());
            statement.setInt(2, prestamo.getUsuarioId());
            statement.setDate(3, Date.valueOf(prestamo.getFechaPrestamo()));
            if (prestamo.getFechaDevolucion() != null) {
                statement.setDate(4, Date.valueOf(prestamo.getFechaDevolucion()));
            } else {
                statement.setNull(4, java.sql.Types.DATE);
            }
            statement.executeUpdate();

            try (ResultSet generatedKeys = statement.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    prestamo.setId(generatedKeys.getInt(1));
                } else {
                    throw new SQLException("No se pudo obtener el ID del préstamo insertado.");
                }
            }
        } catch (SQLException e) {
            System.out.println("Error al guardar el préstamo: " + e.getMessage());
        }
    }

    public List<Prestamo> leerPrestamos() {
        List<Prestamo> prestamos = new ArrayList<>();
        String sql = "SELECT * FROM prestamos";
        try (Statement statement = conexion.createStatement(); ResultSet resultSet = statement.executeQuery(sql)) {
            while (resultSet.next()) {
                int id = resultSet.getInt("id");
                int libroId = resultSet.getInt("libro_id");
                int usuarioId = resultSet.getInt("usuario_id");
                LocalDate fechaPrestamo = resultSet.getDate("fecha_prestamo").toLocalDate();
                LocalDate fechaDevolucion = resultSet.getDate("fecha_devolucion") != null ? resultSet.getDate("fecha_devolucion").toLocalDate() : null;
                Prestamo prestamo = new Prestamo(id, libroId, usuarioId, fechaPrestamo, fechaDevolucion);
                prestamos.add(prestamo);
            }
        } catch (SQLException e) {
            System.out.println("Error al consultar los préstamos: " + e.getMessage());
        }
        return prestamos;
    }

    public void eliminarPrestamo(int idPrestamo) {
        String sql = "DELETE FROM prestamos WHERE id = ?";
        try (PreparedStatement statement = conexion.prepareStatement(sql)) {
            statement.setInt(1, idPrestamo);
            statement.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Error al eliminar el préstamo: " + e.getMessage());
        }
    }

    public void actualizarPrestamo(Prestamo prestamo) {
        String sql = "UPDATE prestamos SET libro_id = ?, usuario_id = ?, fecha_prestamo = ?, fecha_devolucion = ? WHERE id = ?";
        try (PreparedStatement statement = conexion.prepareStatement(sql)) {
            statement.setInt(1, prestamo.getLibroId());
            statement.setInt(2, prestamo.getUsuarioId());
            statement.setDate(3, Date.valueOf(prestamo.getFechaPrestamo()));
            if (prestamo.getFechaDevolucion() != null) {
                statement.setDate(4, Date.valueOf(prestamo.getFechaDevolucion()));
            } else {
                statement.setNull(4, java.sql.Types.DATE);
            }
            statement.setInt(5, prestamo.getId());
            statement.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Error al actualizar el préstamo: " + e.getMessage());
        }
    }

    public void cerrarConexion() {
        conexionBD.closeConexion(conexion);
    }
}
