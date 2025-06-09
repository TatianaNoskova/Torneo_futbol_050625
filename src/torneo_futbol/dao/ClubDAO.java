package torneo_futbol.dao;

import java.io.*;
import java.sql.*;

import torneo_futbol.db.Conexion;
import torneo_futbol.model.Club;
import torneo_futbol.dao.EstadioDAO;
import torneo_futbol.dao.DisciplinaDAO;


public class ClubDAO {
	
	public static int obtenerIdClubPorAdmin(String emailAdmin) {
		String sql = "SELECT id_club FROM persona WHERE email = ? AND rol = 'Admin Club'";
			
		try (Connection conn = Conexion.getInstance().getConnection();
				PreparedStatement stmt = conn.prepareStatement(sql)) {
	            stmt.setString(1, emailAdmin);
	            try (ResultSet rs = stmt.executeQuery()) {
	                if (rs.next()) {
	                    return rs.getInt("id_club");
	                }
	            }
	        } catch (SQLException e) {
	            e.printStackTrace();
	        }
	        return -1;
	    }
	
	public Club obtenerClubPorId(Connection conn, int id) throws SQLException, IOException {
	    Club club = null;
	    String sqlClub = "SELECT id_club, nombre, direccion FROM club WHERE id_club = ?";
	    try (PreparedStatement stmt = conn.prepareStatement(sqlClub)) {
	        stmt.setInt(1, id);
	        try (ResultSet rs = stmt.executeQuery()) {
	            if (rs.next()) {
	                club = new Club(rs.getString("nombre"), rs.getString("direccion"));
	                club.setId(rs.getInt("id_club")); 

	                
	                club.getEquipos().addAll(new EquipoDAO().obtenerEquiposPorClub(conn, id));
	                club.getEstadios().addAll(new EstadioDAO().obtenerEstadiosPorClub(conn, id));
	                club.getDisciplinas().addAll(new DisciplinaDAO().obtenerDisciplinasPorClub(conn, id));
	                // getInstalaciones().addAll(new InstalacionDeportivaDAO().obtenerInstalacionesPorClub(conn, id));
	                // club.getDirectoresTecnicos().addAll(new DirectorTecnicoDAO().obtenerDirectoresPorClub(conn, id));
	            }
	        }
	    }
	    return club;
	}
}
