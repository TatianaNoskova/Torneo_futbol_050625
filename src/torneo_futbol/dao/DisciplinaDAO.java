package torneo_futbol.dao;

import java.sql.*;
import java.util.*;

import javax.swing.*;

import torneo_futbol.db.Conexion;
import torneo_futbol.model.*;

public class DisciplinaDAO {
	
	public List<Disciplina> obtenerDisciplinasPorClub(Connection conn, int idClub) throws SQLException {
	    List<Disciplina> disciplinas = new ArrayList<>();
	    String sql = "SELECT DISTINCT d.id_disciplina, d.nombre_disciplina " +
	                 "FROM disciplina d " +
	                 "JOIN instalacion i ON i.id_disciplina = d.id_disciplina " +
	                 "WHERE i.id_club = ? " + 
	                 "UNION " + 
	                 "SELECT d.id_disciplina, d.nombre_disciplina " +
	                 "FROM disciplina d " +
	                 "WHERE d.id_club = ?"; // Здесь нужно добавить условие, что дисциплина привязана к клубу напрямую

	    try (PreparedStatement stmt = conn.prepareStatement(sql)) {
	        stmt.setInt(1, idClub); // Для первого SELECT (по установке)
	        stmt.setInt(2, idClub); // Для второго SELECT (по клубу)

	        try (ResultSet rs = stmt.executeQuery()) {
	            while (rs.next()) {
	                Disciplina disciplina = new Disciplina(
	                    rs.getInt("id_disciplina"),
	                    rs.getString("nombre_disciplina")
	                );
	                disciplinas.add(disciplina);
	            }
	        }
	    }
	    return disciplinas;
	}




    // Получить дисциплину по id
	public Disciplina obtenerDisciplinaPorId(Connection conn, int idDisciplina) throws SQLException {
	    Disciplina disciplina = null;
	    String sql = "SELECT id_disciplina, nombre_disciplina FROM disciplina WHERE id_disciplina = ?";

	    try (PreparedStatement stmt = conn.prepareStatement(sql)) {
	        stmt.setInt(1, idDisciplina);
	        try (ResultSet rs = stmt.executeQuery()) {
	            if (rs.next()) {
	                disciplina = new Disciplina(
	                    rs.getInt("id_disciplina"),
	                    rs.getString("nombre_disciplina")
	                );
	            }
	        }
	    }
	    return disciplina;
	}
	
	/* private void editarDisciplina(Disciplina disciplina) {
	    try (Connection conn = Conexion.getInstance().getConnection()) {
	        // 1. Загружаем информацию о площадке, связанной с дисциплиной
	        String sql = "SELECT * FROM instalacion WHERE id_diciplina = ? AND id_club = ?";
	        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
	            stmt.setInt(1, disciplina.getId()); // ID дисциплины
	            stmt.setInt(2, disciplina.getClubId()); // ID клуба

	            try (ResultSet rs = stmt.executeQuery()) {
	                if (rs.next()) {
	                    // 2. Создаем панель для редактирования адреса и расписания
	                    String direccion = rs.getString("direccion");
	                    String horario = rs.getString("horario");

	                    JPanel panel = new JPanel();
	                    panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

	                    JTextField direccionField = new JTextField(direccion);
	                    JTextField horarioField = new JTextField(horario);

	                    panel.add(new JLabel("Dirección de la instalación:"));
	                    panel.add(direccionField);
	                    panel.add(new JLabel("Horario de la instalación:"));
	                    panel.add(horarioField);

	                    // 3. Показываем диалоговое окно для редактирования
	                    int option = JOptionPane.showConfirmDialog(this, panel, "Editar Instalación de Disciplina", JOptionPane.OK_CANCEL_OPTION);

	                    if (option == JOptionPane.OK_OPTION) {
	                        // 4. Обновляем данные
	                        String newDireccion = direccionField.getText();
	                        String newHorario = horarioField.getText();

	                        // 5. Обновляем данные в базе данных
	                        String updateSql = "UPDATE instalacion SET direccion = ?, horario = ? WHERE id_instalacion = ?";
	                        try (PreparedStatement updateStmt = conn.prepareStatement(updateSql)) {
	                            updateStmt.setString(1, newDireccion);
	                            updateStmt.setString(2, newHorario);
	                            updateStmt.setInt(3, rs.getInt("id_instalacion"));
	                            updateStmt.executeUpdate();

	                            JOptionPane.showMessageDialog(this, "Instalación actualizada correctamente.");
	                        }
	                    }
	                } else {
	                    JOptionPane.showMessageDialog(this, "No se encontró la instalación asociada a esta disciplina.");
	                }
	            }
	        }
	    } catch (SQLException e) {
	        e.printStackTrace();
	        JOptionPane.showMessageDialog(this, "Error al editar la disciplina.");
	    }
	} */



    // Добавить новую дисциплину
	public void agregarDisciplina(Connection conn, Disciplina disciplina, int idClub) throws SQLException {
	    String sql = "INSERT INTO disciplina (nombre_disciplina, id_club) VALUES (?, ?)";
	    try (PreparedStatement stmt = conn.prepareStatement(sql)) {
	        stmt.setString(1, disciplina.getNombreDisciplina());
	        stmt.setInt(2, idClub);
	        stmt.executeUpdate();
	    }
	}


    // Обновить дисциплину
    public void actualizarDisciplina(Connection conn, int idDisciplina, String nuevoNombre) throws SQLException {
        String sql = "UPDATE disciplina SET nombre_disciplina = ? WHERE id_disciplina = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, nuevoNombre);
            stmt.setInt(2, idDisciplina);
            stmt.executeUpdate();
        }
    }

    // Удалить дисциплину
    public void eliminarDisciplina(Connection conn, int idDisciplina) throws SQLException {
        String sql = "DELETE FROM disciplina WHERE id_disciplina = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, idDisciplina);
            stmt.executeUpdate();
        }
    }

}
