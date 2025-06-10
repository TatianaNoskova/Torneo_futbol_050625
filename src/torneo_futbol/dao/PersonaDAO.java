package torneo_futbol.dao;

import java.sql.*;
import java.util.*;

import torneo_futbol.model.AdminClub;
import torneo_futbol.model.DirectorTecnico;

public class PersonaDAO {
	
	public List<DirectorTecnico> obtenerTecnicosPorClub(Connection conn, int idClub) throws SQLException {
	    List<DirectorTecnico> tecnicos = new ArrayList<>();
	    String sql = "SELECT id_persona, nombre, apellido, email FROM persona WHERE rol = 'DT' AND id_club = ?";
	    
	    try (PreparedStatement stmt = conn.prepareStatement(sql)) {
	        stmt.setInt(1, idClub);
	        try (ResultSet rs = stmt.executeQuery()) {
	            while (rs.next()) {
	                int id = rs.getInt("id_persona");
	                String nombre = rs.getString("nombre");
	                String apellido = rs.getString("apellido");
	                String email = rs.getString("email");

	                DirectorTecnico dt = new DirectorTecnico(nombre, apellido, email, null);
	                dt.setIdDT(id); // если есть такое поле
	                tecnicos.add(dt);
	            }
	        }
	    }
	    return tecnicos;
	}
	
	public int obtenerIdPersonaPorEmail(Connection conn, String email) throws SQLException {
        String sql = "SELECT id_persona FROM persona WHERE email = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, email);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("id_persona");
                } else {
                    throw new SQLException("No se encontró el usuario con email: " + email);
                }
            }
        }
    }
	
	

		public AdminClub obtenerAdminPorClubId(Connection conn, int idClub) throws SQLException {
		    String sql = """
		        SELECT p.nombre, p.apellido, p.email, p.password
		        FROM persona p
		        JOIN club c ON c.id_admin = p.id_persona
		        WHERE c.id_club = ? AND p.rol = 'Admin Club'
		    """;

		    try (PreparedStatement stmt = conn.prepareStatement(sql)) {
		        stmt.setInt(1, idClub);
		        try (ResultSet rs = stmt.executeQuery()) {
		            if (rs.next()) {
		                String nombre = rs.getString("nombre");
		                String apellido = rs.getString("apellido");
		                String email = rs.getString("email");
		                String password = rs.getString("password"); // Добавлено
		                return new AdminClub(nombre, apellido, email, password); // Исправлено
		            }
		        }
		    }

		    return null; // если не найден
		}

	
}



