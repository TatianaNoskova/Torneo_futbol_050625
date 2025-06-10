package torneo_futbol.dao;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import torneo_futbol.db.Conexion;
import torneo_futbol.model.Torneo;

public class TorneoDAO {
    public List<Torneo> obtenerTodos(Connection conn) throws SQLException {
        List<Torneo> torneos = new ArrayList<>();
        
        String sql = "SELECT idTorneo, nombreTorneo, anoTorneo, estado FROM torneos";
        
        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                
                Torneo torneo = new Torneo(
                    rs.getInt("idTorneo"),
                    rs.getString("nombreTorneo"),
                    rs.getString("anoTorneo")
                );
                torneo.setEstado(rs.getString("estado"));
                torneos.add(torneo);
            }
        }
        return torneos;
    }

    public boolean insertar(Connection conn, Torneo torneo) throws SQLException {
        String sql = "INSERT INTO torneos (nombreTorneo, anoTorneo, estado) VALUES (?, ?, ?)";
        
        try (PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setString(1, torneo.getNombreTorneo());
            pstmt.setString(2, torneo.getAnoTorneo());
            pstmt.setString(3, torneo.getEstado());
            int affectedRows = pstmt.executeUpdate();
            
            try (ResultSet rs = pstmt.getGeneratedKeys()) {
                if (rs.next()) {
                    torneo.setIdTorneo(rs.getInt(1));
                }
            }
            return affectedRows > 0;
        }
    }
       
}
