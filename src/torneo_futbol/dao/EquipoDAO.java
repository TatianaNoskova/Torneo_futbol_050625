package torneo_futbol.dao;

import java.io.*;
import java.sql.*;
import java.util.*;

import javax.swing.*;

import torneo_futbol.db.Conexion;
import torneo_futbol.model.Club;
import torneo_futbol.model.Equipo;
import torneo_futbol.model.Estadio;

public class EquipoDAO {
	
	public List<Equipo> obtenerEquiposPorClub(Connection conn, int idClub) throws SQLException, IOException {
		
		List<Equipo> equipos = new ArrayList<>();

	    Club club = new Club(idClub, null, null);
	    
	    club.setId(idClub);

	    String sql = "SELECT id_equipo, nombre, categoria, colores, escudo FROM equipo WHERE id_club = ?";
	    try (PreparedStatement stmt = conn.prepareStatement(sql)) {
	        stmt.setInt(1, idClub);
	        try (ResultSet rs = stmt.executeQuery()) {
	            while (rs.next()) {
	                int idEquipo = rs.getInt("id_equipo");
	                String nombre = rs.getString("nombre");
	                String categoria = rs.getString("categoria");
	                String colores = rs.getString("colores");
	                byte[] escudoBytes = rs.getBytes("escudo");

	                String rutaEscudo = null;
	                if (escudoBytes != null && escudoBytes.length > 0) {
	                    File dir = new File("tempEscudos");
	                    if (!dir.exists()) dir.mkdirs();

	                    File archivoEscudo = new File(dir, "escudo_" + idEquipo + ".png");
	                    try (FileOutputStream fos = new FileOutputStream(archivoEscudo)) {
	                        fos.write(escudoBytes);
	                        rutaEscudo = archivoEscudo.getAbsolutePath();
	                    }
	                }

	                Equipo equipo = new Equipo(idEquipo, nombre, categoria, colores, rutaEscudo, null, null);
	                equipo.setClub(club);  // <-- вот это ключевое
	                equipos.add(equipo);
	            }
	        }
	    }

	    return equipos;
	}

	
	public List<Equipo> obtenerEquiposDelClub(int idClub) {
	    List<Equipo> equipos = new ArrayList<>();
	    
	    try (Connection conn = Conexion.getInstance().getConnection()) {
	        
	        String sql = "SELECT id_equipo, nombre FROM equipo WHERE id_club = ?";
	        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
	            stmt.setInt(1, idClub); 
	            
	            try (ResultSet rs = stmt.executeQuery()) {
	                while (rs.next()) {
	                    
	                    int idEquipo = rs.getInt("id_equipo");
	                    String nombreEquipo = rs.getString("nombre");
	                    
	                    Equipo equipo = new Equipo(idEquipo, nombreEquipo, null, null, null, null, null);
	                    equipos.add(equipo);  
	                }
	            }
	        }
	    } catch (SQLException e) {
	        e.printStackTrace();
	        JOptionPane.showMessageDialog(null, "Error al conectar con la base de datos: " + e.getMessage());
	    }
	    
	    return equipos;
	}
	
	public static Equipo obtenerEquipoPorId(Connection conn, int idEquipo) throws SQLException {
        String query = "SELECT id_equipo, nombre, categoria FROM equipo WHERE id_equipo = ?";
        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, idEquipo);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return new Equipo(rs.getInt("id_equipo"), rs.getString("nombre"), rs.getString("categoria"), null, null, null, null);
                }
            }
        }
        return null; // <- если не найден
    }
	
	public List<String> obtenerCategorias(Connection conn) throws SQLException {
        List<String> categorias = new ArrayList<>();
        String sql = "SELECT DISTINCT categoria FROM equipo";  // Получаем уникальные категории
        try (PreparedStatement stmt = conn.prepareStatement(sql); ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                categorias.add(rs.getString("categoria"));
            }
        }
        return categorias;
    }
	
	public void actualizarEquipo(Connection conn, Equipo equipo, byte[] nuevoEscudo, int idEstadioLocal) throws SQLException {
	    String sql;
	    PreparedStatement stmt;

	    if (nuevoEscudo != null) {
	        sql = "UPDATE equipo SET nombre = ?, categoria = ?, colores = ?, escudo = ? WHERE id_equipo = ?";
	        stmt = conn.prepareStatement(sql);
	        stmt.setString(1, equipo.getNombre());
	        stmt.setString(2, equipo.getCategoria());
	        stmt.setString(3, equipo.getColores());
	        stmt.setBytes(4, nuevoEscudo);  // только если эмблема передана
	        stmt.setInt(5, equipo.getIdEquipo());
	    } else {
	        sql = "UPDATE equipo SET nombre = ?, categoria = ?, colores = ? WHERE id_equipo = ?";
	        stmt = conn.prepareStatement(sql);
	        stmt.setString(1, equipo.getNombre());
	        stmt.setString(2, equipo.getCategoria());
	        stmt.setString(3, equipo.getColores());
	        stmt.setInt(4, equipo.getIdEquipo());
	    }

	    stmt.executeUpdate();
	    stmt.close();
	}

	
	public ImageIcon obtenerEscudo(Connection conn, int idEquipo) {
        String sql = "SELECT escudo FROM equipo WHERE id_equipo = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, idEquipo);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    byte[] imgBytes = rs.getBytes("escudo");
                    if (imgBytes != null && imgBytes.length > 0) {
                        System.out.println("Escudo encontrado, tamaño: " + imgBytes.length);
                        return new ImageIcon(imgBytes); // Convertir BLOB в ImageIcon
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null; // Если эмблема не найдена
    }
	
	public void eliminarEquipoPorId(Connection conn, int idEquipo) throws SQLException {
        String sql = "DELETE FROM equipo WHERE id_equipo = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, idEquipo);
            stmt.executeUpdate();
        }
    }


}
