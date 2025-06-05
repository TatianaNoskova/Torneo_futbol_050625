package torneo_futbol.dao;

import java.sql.*;
import java.util.*;

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



}
