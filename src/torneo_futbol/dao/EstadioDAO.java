package torneo_futbol.dao;

import java.sql.*;
import java.util.*;

import torneo_futbol.db.Conexion;
import torneo_futbol.model.Estadio;

public class EstadioDAO {
	
	public List<Estadio> obtenerEstadiosPorClub(Connection conn, int idClub) {
	    List<Estadio> estadios = new ArrayList<>();
	    String sql = "SELECT id_estadio, nombre, direccion, capacidad FROM estadio WHERE id_club = ?";

	    try (PreparedStatement stmt = conn.prepareStatement(sql)) {
	        stmt.setInt(1, idClub);
	        try (ResultSet rs = stmt.executeQuery()) {
	            while (rs.next()) {
	                int idEstadio = rs.getInt("id_estadio");
	                String nombre = rs.getString("nombre");
	                String direccion = rs.getString("direccion");
	                int capacidad = rs.getInt("capacidad");

	                Estadio estadio = new Estadio(idEstadio, nombre, direccion, capacidad);
	                estadios.add(estadio);
	            }
	        }
	    } catch (SQLException e) {
	        e.printStackTrace();
	    }

	    return estadios;
	}
	

	    public Estadio obtenerEstadioPorEquipo(Connection conn, int idEquipo) throws SQLException {
	        String sql = "SELECT e.id_estadio, e.nombre, e.direccion, e.capacidad " +
	                     "FROM estadio e JOIN club c ON e.id_club = c.id_club " +
	                     "JOIN equipo eq ON eq.id_club = c.id_club " +
	                     "WHERE eq.id_equipo = ? LIMIT 1";

	        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
	            stmt.setInt(1, idEquipo);
	            try (ResultSet rs = stmt.executeQuery()) {
	                if (rs.next()) {
	                    return new Estadio(
	                        rs.getInt("id_estadio"),
	                        rs.getString("nombre"),
	                        rs.getString("direccion"),
	                        rs.getInt("capacidad")
	                    );
	                }
	            }
	        }
	        return null;
	    }
	    
	    public void actualizarEstadio(Connection conn, Estadio estadio) throws SQLException {
	        String query = "UPDATE estadio SET nombre = ?, direccion = ?, capacidad = ? WHERE id_estadio = ?";
	        try (PreparedStatement stmt = conn.prepareStatement(query)) {
	            stmt.setString(1, estadio.getNombre());
	            stmt.setString(2, estadio.getDireccion());
	            stmt.setInt(3, estadio.getCapacidad());
	            stmt.setInt(4, estadio.getIdEstadio());
	            stmt.executeUpdate();
	        }
	    }

	    public void eliminarEstadioPorId(Connection conn, int idEquipo) throws SQLException {
	        String sql = "DELETE FROM estadio WHERE id_estadio = ?";
	        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
	            stmt.setInt(1, idEquipo);
	            stmt.executeUpdate();
	        }
	    }


}
