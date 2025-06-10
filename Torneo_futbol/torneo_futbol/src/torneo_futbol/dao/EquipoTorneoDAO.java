package torneo_futbol.dao;

import java.sql.*;
import torneo_futbol.db.Conexion;

public class EquipoTorneoDAO {
    public boolean asignarEquipo(Connection conn, int idTorneo, int idEquipo, int posicion) throws SQLException {
        String sql = "INSERT INTO equipo_torneo (id_torneo, id_equipo, categoria, fecha_registro) VALUES (?, ?, ?, ?)";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, idTorneo);
            pstmt.setInt(2, idEquipo);
            pstmt.setString(3, "Juvenil"); 
            pstmt.setDate(4, new Date(System.currentTimeMillis()));
            return pstmt.executeUpdate() > 0;
        }
    }
}