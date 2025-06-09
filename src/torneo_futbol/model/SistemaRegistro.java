package torneo_futbol.model;

import javax.swing.*;

import torneo_futbol.dao.ClubDAO;
import torneo_futbol.dao.PersonaDAO;
import torneo_futbol.db.Conexion;
import torneo_futbol.ui.PanelAdminClub;

import java.io.*;
import java.sql.*;


public class SistemaRegistro {
	
	private Persona usuarioActual;
	
	
	
public void iniciarRegistro() {
	try {
		String[] opciones = {"Registrarse", "Iniciar sesión"};
		String seleccion = (String) JOptionPane.showInputDialog(null,
				"¿Qué deseas hacer?",
				"Bienvenido",
				JOptionPane.QUESTION_MESSAGE,
				null,
				opciones,
				opciones[0]
				);

				if (seleccion == null) {
					JOptionPane.showMessageDialog(null, "Operación cancelada.");
					return;
				}

				if (seleccion.equals("Registrarse")) {
					registrarNuevoUsuario();
					} else {
						iniciarSesion();
					
					}
	} catch (Exception e) {
        e.printStackTrace();
        JOptionPane.showMessageDialog(null, "Error inesperado: " + e.getMessage());
    }
	}

public void registrarNuevoUsuario() {
    String email = JOptionPane.showInputDialog("Ingrese su correo electrónico:");
    if (email == null || email.isBlank() || !email.contains("@")) {
        JOptionPane.showMessageDialog(null, "Correo inválido.");
        return;
    }

    if (emailExiste(email)) {
        JOptionPane.showMessageDialog(null, "Este correo ya está registrado.");
        return;
    }

    String password = JOptionPane.showInputDialog("Cree una contraseña:");
    if (password == null || password.isBlank()) {
        JOptionPane.showMessageDialog(null, "Debe ingresar una contraseña.");
        return;
    }

    String nombre = JOptionPane.showInputDialog("Ingrese su nombre:");
    String apellido = JOptionPane.showInputDialog("Ingrese su apellido:");

    String[] roles = {"Administrador AFA", "Administrador de club", "Socio del club", "Público general"};
    String rol = (String) JOptionPane.showInputDialog(
            null,
            "Seleccione su rol:",
            "Registro",
            JOptionPane.QUESTION_MESSAGE,
            null,
            roles,
            roles[0]
    );

    if (rol == null) {
        JOptionPane.showMessageDialog(null, "No se seleccionó ningún rol.");
        return;
    }

    if (!rol.equals("Público general") && !emailAutorizadoParaRol(email, rol)) {
        JOptionPane.showMessageDialog(null, "Este correo no está autorizado para ese rol.");
        return;
    }

    Persona persona;
    switch (rol) {
        case "Administrador AFA" -> persona = new AdminAFA(nombre, apellido, email, password);
        case "Administrador de club" -> persona = new AdminClub(nombre, apellido, email, password);
        case "Socio del club" -> persona = new Socio(nombre, apellido, email, password);
        default -> persona = new Publico(nombre, apellido, email, password);
    }

    String rolBD = switch (rol) {
        case "Administrador AFA" -> "Admin Afa";
        case "Administrador de club" -> "Admin Club";
        case "Socio del club" -> "Socio";
        default -> "Publico";
    };

    try (Connection conn = Conexion.getInstance().getConnection()) {
        String sql = "SELECT COUNT(*) FROM persona WHERE email = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, email);
            ResultSet rs = stmt.executeQuery();

            if (rs.next() && rs.getInt(1) > 0) {
                // Usuario ya existe → actualizar
                String updateSql = "UPDATE persona SET nombre = ?, apellido = ?, password = ?, rol = ? WHERE email = ?";
                try (PreparedStatement updateStmt = conn.prepareStatement(updateSql)) {
                    updateStmt.setString(1, nombre);
                    updateStmt.setString(2, apellido);
                    updateStmt.setString(3, password);
                    updateStmt.setString(4, rolBD);
                    updateStmt.setString(5, email);
                    updateStmt.executeUpdate();

                    // Получаем id через DAO
                    PersonaDAO personaDAO = new PersonaDAO();
                    int idPersona = personaDAO.obtenerIdPersonaPorEmail(conn, email);
                    persona.setIdPersona(idPersona);
                    this.usuarioActual = persona;

                    JOptionPane.showMessageDialog(null, "¡Datos actualizados para el email: " + email + "!");
                    usuarioActual.mostrarMenu();
                }

            } else {
                // Новый пользователь → вставка
                String insertSql = "INSERT INTO persona (nombre, apellido, email, password, rol) VALUES (?, ?, ?, ?, ?)";
                try (PreparedStatement insertStmt = conn.prepareStatement(insertSql, Statement.RETURN_GENERATED_KEYS)) {
                    insertStmt.setString(1, nombre);
                    insertStmt.setString(2, apellido);
                    insertStmt.setString(3, email);
                    insertStmt.setString(4, password);
                    insertStmt.setString(5, rolBD);
                    insertStmt.executeUpdate();

                    ResultSet generatedKeys = insertStmt.getGeneratedKeys();
                    if (generatedKeys.next()) {
                        int idPersona = generatedKeys.getInt(1);
                        persona.setIdPersona(idPersona);
                        this.usuarioActual = persona;

                        JOptionPane.showMessageDialog(null, "¡Registro exitoso como " + rol + "!");
                        usuarioActual.mostrarMenu();
                    } else {
                        JOptionPane.showMessageDialog(null, "No se pudo obtener el ID del nuevo usuario.");
                    }
                }
            }
        }
    } catch (SQLException e) {
        e.printStackTrace();
        JOptionPane.showMessageDialog(null, "Error al registrar el usuario.");
    }
}

	private boolean emailAutorizadoParaRol(String email, String rolInterfaz) {
	    String rolBD = switch (rolInterfaz) {
	        case "Administrador AFA" -> "Admin Afa";
	        case "Administrador de club" -> "Admin Club";
	        case "Socio del club" -> "Socio";
	        case "Público general" -> "Publico";
	        default -> null;
	    };

	    if (rolBD == null) {
	        System.out.println("Rol desconocido: " + rolInterfaz);
	        return false;
	    }
	    
	    if (rolBD.equals("Publico")) {
	        return true;
	    }

	    String sql = "SELECT COUNT(*) FROM persona WHERE LOWER(email) = LOWER(?) AND rol = ?";
	    try (Connection conn = Conexion.getInstance().getConnection();
	         PreparedStatement stmt = conn.prepareStatement(sql)) {
	        
	        stmt.setString(1, email);
	        stmt.setString(2, rolBD);
	        
	        try (ResultSet rs = stmt.executeQuery()) {
	            if (rs.next()) {
	                int count = rs.getInt(1);
	                System.out.println("Coincidencias para " + email + " con rol " + rolBD + ": " + count);
	                return count > 0;
	            }
	        }
	    } catch (SQLException e) {
	        e.printStackTrace();
	    }

	    return false;
	}


	public void iniciarSesion() throws IOException {
	    String email = JOptionPane.showInputDialog("Ingrese su correo electrónico:");
	    if (email == null || email.isBlank()) {
	        JOptionPane.showMessageDialog(null, "Debe ingresar un correo.");
	        return;
	    }

	    String password = JOptionPane.showInputDialog("Ingrese su contraseña:");
	    if (password == null || password.isBlank()) {
	        JOptionPane.showMessageDialog(null, "Debe ingresar una contraseña.");
	        return;
	    }

	    try (Connection conn = Conexion.getInstance().getConnection()) {
	        String sql = "SELECT * FROM persona WHERE email = ? AND password = ?";
	        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
	            stmt.setString(1, email);
	            stmt.setString(2, password);

	            try (ResultSet rs = stmt.executeQuery()) {
	                if (rs.next()) {
	                    String nombre = rs.getString("nombre");
	                    String apellido = rs.getString("apellido");
	                    String rol = rs.getString("rol").toLowerCase();

	                    Persona persona = null;

	                    switch (rol) {
	                        case "admin afa":
	                            JOptionPane.showMessageDialog(null, "¡Inicio de sesión exitoso como " + rol + "!");
	                            persona = new AdminAFA(nombre, apellido, email, password);
	                            break;

	                        case "admin club":
	                            AdminClub adminClub = new AdminClub(nombre, apellido, email, password);
	                            
	                            // 🔧 УСТАНАВЛИВАЕМ id_persona!
	                            int idPersona = rs.getInt("id_persona");
	                            adminClub.setIdPersona(idPersona);  // ← ЭТО ОБЯЗАТЕЛЬНО
	                            
	                            adminClub.cargarClub();

	                            if (adminClub.getClub() != null) {
	                                try (Connection connClub = Conexion.getInstance().getConnection()) {
	                                    ClubDAO clubDAO = new ClubDAO();
	                                    Club clubCompleto = clubDAO.obtenerClubPorId(connClub, adminClub.getClub().getId());

	                                    SwingUtilities.invokeLater(() -> {
	                                        JFrame frame = new JFrame("Panel del Administrador del Club");
	                                        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
	                                        frame.getContentPane().add(new PanelAdminClub(adminClub, clubCompleto));
	                                        frame.pack();
	                                        frame.setLocationRelativeTo(null);
	                                        frame.setVisible(true);
	                                    });
	                                } catch (SQLException e) {
	                                    e.printStackTrace();
	                                    JOptionPane.showMessageDialog(null, "Error al cargar datos del club desde la base.");
	                                }
	                            } else {
	                                adminClub.mostrarMenu();
	                            }
	                            return;
	                        
	                        case "socio":
	                            JOptionPane.showMessageDialog(null, "¡Inicio de sesión exitoso como " + rol + "!");
	                            persona = new Socio(nombre, apellido, email, password);
	                            break;

	                        case "publico":
	                            JOptionPane.showMessageDialog(null, "¡Inicio de sesión exitoso como " + rol + "!");
	                            persona = new Publico(nombre, apellido, email, password);
	                            break;

	                        case "director tecnico":
	                            JOptionPane.showMessageDialog(null, "¡Inicio de sesión exitoso como " + rol + "!");
	                            persona = new DirectorTecnico(nombre, apellido, email, password);
	                            break;

	                        case "arbitro":
	                            JOptionPane.showMessageDialog(null, "¡Inicio de sesión exitoso como " + rol + "!");
	                            persona = new Arbitro(nombre, apellido, email, password);
	                            break;

	                        default:
	                            JOptionPane.showMessageDialog(null, "Rol desconocido.");
	                            return;
	                    }

	                    if (persona != null) {
	                        persona.mostrarMenu(); // для всех, кроме admin club
	                    }

	                } else {
	                    JOptionPane.showMessageDialog(null, "Correo o contraseña incorrectos.");
	                }
	            }
	        }
	    } catch (SQLException e) {
	        JOptionPane.showMessageDialog(null, "Error de conexión a la base de datos: " + e.getMessage());
	    }
	}





	private boolean emailExiste(String email) {
	    boolean exists = false;
	    try (Connection connection = Conexion.getInstance().getConnection()) {
	        String query = "SELECT rol FROM persona WHERE LOWER(email) = LOWER(?)";
	        try (PreparedStatement stmt = connection.prepareStatement(query)) {
	            stmt.setString(1, email.toLowerCase());
	            var resultSet = stmt.executeQuery();
	            if (resultSet.next()) {
	                String rolEnBaseDeDatos = resultSet.getString("rol");
	                
	                return !rolEnBaseDeDatos.equals("Admin Club"); 
	            }
	        }
	    } catch (SQLException e) {
	        JOptionPane.showMessageDialog(null, "Error al verificar el correo en la base de datos: " + e.getMessage());
	    }
	    return exists;
	}

}
