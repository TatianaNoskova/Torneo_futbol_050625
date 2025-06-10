package torneo_futbol.model;

import java.awt.Image;
import java.io.*;
import java.sql.*;
import java.time.*;
import java.util.*;
import java.time.format.DateTimeParseException;


import javax.swing.*;

import torneo_futbol.dao.ClubDAO;
import torneo_futbol.dao.EquipoDAO;
import torneo_futbol.dao.EstadioDAO;
import torneo_futbol.db.Conexion;
import torneo_futbol.ui.PanelAdminClub;


public class AdminClub extends Administrador {

	private Club club;

	public AdminClub(String nombre, String apellido, String email, String password) {
		super(nombre, apellido, email, password,  "Administrador del Club");
	}
	
	public String getEmail() {
	    return this.email; 
	}
	
	 public Club getClub() {
	      return this.club;
	 }

	public void setClub(Club club) {
		this.club = club;
	}
	
	@Override
	  public void mostrarMenu() {
	    boolean salir = false;

	    while (!salir) {
	      String[] opciones = {
	    	  "Administrar Club",
	          "Registrar club",
	          "Registrar estadio",
	          "Registrar equipo",
	          "Registrar disciplina y instalacion deportiva",
	          "Registrar director técnico",
	          "Registrar correos de socios",
	          "Gestionar disciplinas y actividades",
	          "Reservar/administrar instalaciones",
	          "Vender entradas",
	          "Administrar beneficios y premios",
	          "Salir"
	      };

	      String seleccion = (String) JOptionPane.showInputDialog(
	          null,
	          "Menú Administrador de Club",
	          "Opciones",
	          JOptionPane.QUESTION_MESSAGE,
	          null,
	          opciones,
	          opciones[0]);

	      if (seleccion == null || seleccion.equals("Salir")) {
	        salir = true;
	      } else {
	        procesarOpcion(seleccion);
	      }
	    }
	  }

	  private void procesarOpcion(String seleccion) {
	    switch (seleccion) {
	      case "Administrar Club" -> mostrarPanelAdminClub();
	      case "Registrar club" -> registrarClub();
	      case "Registrar estadio" -> registrarEstadio();
	      case "Registrar equipo" -> registrarEquipo();
	      case "Registrar disciplina y instalacion deportiva" -> registrarDisciplinaYInstalacion();
	      case "Registrar director técnico" -> registrarDirectorTecnico();
	      case "Registrar correos de socios" -> registrarSocioPorEmail();
	      case "Vender entradas" -> venderEntradas();

	      // Otros opciones van a ser agregadas despues

	      default -> JOptionPane.showMessageDialog(null,
	          "Has seleccionado: " + seleccion + "\n(Función aún no implementada)");
	    }
	  }	
	  
	  private void mostrarPanelAdminClub() {
		    if (this.club == null) {
		        JOptionPane.showMessageDialog(null, "Primero debes registrar un club.");
		        return;
		    }

		    JFrame frame = new JFrame("Panel de Administración del Club");
		    frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		    frame.setContentPane(new PanelAdminClub(null, club));
		    frame.pack();
		    frame.setLocationRelativeTo(null);
		    frame.setVisible(true);
		}
	  
	  public void cargarClub() throws IOException {
		  int id = ClubDAO.obtenerIdClubPorAdmin(this.getEmail());


		    if (id != -1) {
		    	try (Connection conn = Conexion.getInstance().getConnection()) {
		    		ClubDAO clubDAO = new ClubDAO();
		    		this.club = clubDAO.obtenerClubPorId(conn, id);

		    	    
		    	} catch (SQLException e) {
		    	    e.printStackTrace();
		    	    
		    	}
	 
		    } else {
		        JOptionPane.showMessageDialog(null, "No se encontró el club asociado al administrador.");
		    }
		}
	  
	  private void registrarClub() {
		    if (tieneClubRegistrado()) {
		        JOptionPane.showMessageDialog(null, "Ya tienes un club registrado en el sistema.");
		        return;
		    }

		    String nombre = JOptionPane.showInputDialog("Ingrese el nombre del club:");
		    if (nombre == null) {
		        JOptionPane.showMessageDialog(null, "Operación cancelada.");
		        return;
		    }

		    String direccion = JOptionPane.showInputDialog("Ingrese la dirección del club:");
		    if (direccion == null) {
		        JOptionPane.showMessageDialog(null, "Operación cancelada.");
		        return;
		    }

		    if (nombre.isBlank() || direccion.isBlank()) {
		        JOptionPane.showMessageDialog(null, "Datos inválidos. Por favor, intenta nuevamente.");
		        return;
		    }

		    try (Connection conn = Conexion.getInstance().getConnection()) {
		        String sql = "INSERT INTO club (nombre, direccion, id_admin) VALUES (?, ?, ?)";
		        try (PreparedStatement stmt = conn.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS)) {
		            stmt.setString(1, nombre);
		            stmt.setString(2, direccion);
		            stmt.setInt(3, this.getIdPersona());  

		            int rowsInserted = stmt.executeUpdate();
		            if (rowsInserted > 0) {
		                try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
		                    if (generatedKeys.next()) {
		                        int idClub = generatedKeys.getInt(1);
		                        club = new Club(nombre, direccion);
		                        club.setId(idClub);

		                        // Обновим поле id_club у пользователя (если нужно)
		                        try (PreparedStatement updateStmt = conn.prepareStatement(
		                                "UPDATE persona SET id_club = ? WHERE id_persona = ?")) {
		                            updateStmt.setInt(1, idClub);
		                            updateStmt.setInt(2, this.getIdPersona());
		                            int rowsUpdated = updateStmt.executeUpdate();
		                            if (rowsUpdated == 0) {
		                                JOptionPane.showMessageDialog(null, "No se pudo asociar el club al administrador.");
		                            }
		                        }

		                        JOptionPane.showMessageDialog(null, "Club registrado exitosamente:\n" + club);
		                    } else {
		                        JOptionPane.showMessageDialog(null, "No se pudo obtener el ID del nuevo club.");
		                    }
		                }
		            } else {
		                JOptionPane.showMessageDialog(null, "No se pudo registrar el club.");
		            }
		        }
		    } catch (SQLException e) {
		        JOptionPane.showMessageDialog(null, "Error al registrar el club en la base de datos:\n" + e.getMessage());
		        e.printStackTrace();
		    }
		}

	  
	  private boolean tieneClubRegistrado() {
		    try (Connection conn = Conexion.getInstance().getConnection()) {
		        String sql = "SELECT id_club FROM persona WHERE email = ?";
		        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
		            stmt.setString(1, this.getEmail().toLowerCase());
		            try (ResultSet rs = stmt.executeQuery()) {
		                if (rs.next()) {
		                    return rs.getObject("id_club") != null;
		                }
		            }
		        }
		    } catch (SQLException e) {
		        e.printStackTrace();
		    }
		    return false;
	  }
	  
	  private void registrarEstadio() {
		    
		  	ClubDAO clubDAO = new ClubDAO();
		    int idClub = clubDAO.obtenerIdClubPorAdmin(email);
		    
		    if (idClub == -1) {
		    	JOptionPane.showMessageDialog(null, "Primero debe registrar un club.");
		        return;
		    }

		    String nombre = JOptionPane.showInputDialog("Ingrese el nombre del estadio:");
		    String direccion = JOptionPane.showInputDialog("Ingrese la dirección del estadio:");
		    String capacidadStr = JOptionPane.showInputDialog("Ingrese la capacidad del estadio:");

		    if (nombre == null || direccion == null || capacidadStr == null ||
		        nombre.isBlank() || direccion.isBlank() || capacidadStr.isBlank()) {
		        JOptionPane.showMessageDialog(null, "Todos los campos son obligatorios.");
		        return;
		    }

		    int capacidad = 0;
		    try {
		        capacidad = Integer.parseInt(capacidadStr); 
		        if (capacidad <= 0) {
		            JOptionPane.showMessageDialog(null, "La capacidad debe ser mayor que cero.");
		            return;
		        }
		    } catch (NumberFormatException e) {
		        JOptionPane.showMessageDialog(null, "La capacidad debe ser un número válido.");
		        return;
		    }

		    try (Connection conn = Conexion.getInstance().getConnection()) {
		        String sql = "INSERT INTO estadio (nombre, direccion, capacidad, id_club) VALUES (?, ?, ?, ?)";
		        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
		            stmt.setString(1, nombre);   
		            stmt.setString(2, direccion);
		            stmt.setInt(3, capacidad);
		            stmt.setInt(4, idClub);

		            stmt.executeUpdate();
		            JOptionPane.showMessageDialog(null, "Estadio registrado exitosamente.");
		        }
		    } catch (SQLException e) {
		        e.printStackTrace();
		        JOptionPane.showMessageDialog(null, "Error al registrar el estadio.");
		    }
	  }
	  
	  private void registrarEquipo() {
		    ClubDAO clubDAO = new ClubDAO();  
		    int idClub = clubDAO.obtenerIdClubPorAdmin(email);  
		    
		    if (idClub == -1) {
		        JOptionPane.showMessageDialog(null, "Primero debe registrar un club.");
		        return;
		    }

		    String nombreEquipo = JOptionPane.showInputDialog("Ingrese el nombre del equipo:");
		    String categoria = JOptionPane.showInputDialog("Ingrese la categoría del equipo (ej: Primera, Juvenil, etc):");
		    String colores = JOptionPane.showInputDialog("Ingrese los colores del equipo:");
		    
		    String nombreEscudo = JOptionPane.showInputDialog("Ingrese el nombre del archivo del escudo del equipo (ej: escudo1.png):");
		    String rutaEscudo = "./resources/escudos/" + nombreEscudo;
		    System.out.println("Ruta del escudo: " + rutaEscudo); 

		    File archivoEscudo = new File(rutaEscudo);
		    System.out.println("Ruta absoluta real: " + archivoEscudo.getAbsolutePath());
		    System.out.println("Existe archivo: " + archivoEscudo.exists());

		    if (!archivoEscudo.exists()) {
		        JOptionPane.showMessageDialog(null, "El archivo del escudo no existe en la ruta especificada.");
		        return;
		    }

		    byte[] escudoBytes = new byte[(int) archivoEscudo.length()];
		    try (FileInputStream fis = new FileInputStream(archivoEscudo)) {
		        fis.read(escudoBytes);
		    } catch (IOException e) {
		        e.printStackTrace();
		        JOptionPane.showMessageDialog(null, "Error al leer el archivo del escudo.");
		        return;
		    }

		    if (escudoBytes.length == 0) {
		        JOptionPane.showMessageDialog(null, "El archivo del escudo está vacío o no se pudo leer correctamente.");
		        return;
		    }

		    
		    try (Connection conn = Conexion.getInstance().getConnection()) {
		        if (conn == null) {
		            JOptionPane.showMessageDialog(null, "No se pudo conectar a la base de datos.");
		            return;
		        }

		        // Obtener estadios
		        EstadioDAO estadioDAO = new EstadioDAO();
		        List<Estadio> estadios = estadioDAO.obtenerEstadiosPorClub(conn, idClub);

		        if (estadios.isEmpty()) {
		            JOptionPane.showMessageDialog(null, "Primero debe registrar al menos un estadio.");
		            return;
		        }

		        String[] nombresEstadios = new String[estadios.size()];
		        for (int i = 0; i < estadios.size(); i++) {
		            nombresEstadios[i] = estadios.get(i).getNombre();
		        }

		        String seleccion = (String) JOptionPane.showInputDialog(
		            null,
		            "Seleccione el estadio local:",
		            "Estadio",
		            JOptionPane.QUESTION_MESSAGE,
		            null,
		            nombresEstadios,
		            nombresEstadios[0]
		        );

		        if (seleccion == null) {
		            JOptionPane.showMessageDialog(null, "No se seleccionó ningún estadio.");
		            return;
		        }

		        Estadio estadioSeleccionado = estadios.stream()
		            .filter(e -> e.getNombre().equals(seleccion))
		            .findFirst()
		            .orElse(null);

		        // Вставка команды
		        String sql = "INSERT INTO equipo (nombre, categoria, colores, escudo, id_club) VALUES (?, ?, ?, ?, ?)";
		        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
		            stmt.setString(1, nombreEquipo);   
		            stmt.setString(2, categoria);      
		            stmt.setString(3, colores);        
		            stmt.setBytes(4, escudoBytes); 
		            stmt.setInt(5, idClub);  

		            stmt.executeUpdate();
		        }

		        // Показать подтверждение с изображением
		        try {
		            ImageIcon escudoIcon = new ImageIcon(escudoBytes);
		            Image imagenOriginal = escudoIcon.getImage();
		            Image imagenEscalada = imagenOriginal.getScaledInstance(150, 150, Image.SCALE_SMOOTH);
		            ImageIcon escudoEscalado = new ImageIcon(imagenEscalada);

		            JOptionPane.showMessageDialog(
		                null,
		                "Equipo registrado exitosamente:\n" +
		                "Nombre: " + nombreEquipo + "\n" +
		                "Categoría: " + categoria + "\n" +
		                "Colores: " + colores + "\n" +
		                "Estadio Local: " + estadioSeleccionado.getNombre(),
		                "Escudo del equipo",
		                JOptionPane.INFORMATION_MESSAGE,
		                escudoEscalado
		            );
		        } catch (Exception ex) {
		            ex.printStackTrace();
		            JOptionPane.showMessageDialog(null, "Equipo registrado, pero no se pudo cargar el escudo.");
		        }

		    } catch (SQLException e) {
		        e.printStackTrace();
		        JOptionPane.showMessageDialog(null, "Error al registrar el equipo.");
		    }
		}

	  
	  private void registrarDisciplinaYInstalacion() {
		    
		  int idClub = ClubDAO.obtenerIdClubPorAdmin(email);
		   if (idClub == -1) {
		        JOptionPane.showMessageDialog(null, "Primero debe registrar un club.");
		        return;
		    }

		    String nombreDisciplina = JOptionPane.showInputDialog("Ingrese el nombre de la disciplina:");
		    String nombreInstalacion = JOptionPane.showInputDialog("Ingrese el nombre de la instalación deportiva:");
		    String direccionInstalacion = JOptionPane.showInputDialog("Ingrese la dirección de la instalación deportiva:");
		    String descripcion = JOptionPane.showInputDialog("Ingrese una breve descripción (capacidad etc.)");
		    
		    String aperturaStr = JOptionPane.showInputDialog("Ingrese la hora de apertura (HH:mm) para los días de semana (lunes a viernes):");
		    String cierreStr = JOptionPane.showInputDialog("Ingrese la hora de cierre (HH:mm) para los días de semana (lunes a viernes):");

		    String aperturaFinStr = JOptionPane.showInputDialog("Ingrese la hora de apertura (HH:mm) para el fin de semana (sábado y domingo):");
		    String cierreFinStr = JOptionPane.showInputDialog("Ingrese la hora de cierre (HH:mm) para el fin de semana (sábado y domingo):");

		    LocalTime horaApertura;
		    LocalTime horaCierre;
		    LocalTime horaAperturaFin;
		    LocalTime horaCierreFin;

		    try {
		        horaApertura = LocalTime.parse(aperturaStr);
		        horaCierre = LocalTime.parse(cierreStr);
		        horaAperturaFin = LocalTime.parse(aperturaFinStr);
		        horaCierreFin = LocalTime.parse(cierreFinStr);
		    } catch (DateTimeParseException e) {
		        JOptionPane.showMessageDialog(null, "Formato de hora inválido.");
		        return;
		    }

		    if (nombreDisciplina == null || nombreInstalacion == null ||
		        nombreDisciplina.isBlank() || nombreInstalacion.isBlank()) {
		        JOptionPane.showMessageDialog(null, "Todos los campos son obligatorios.");
		        return;
		    }

		    int idDisciplina = -1;
		    int idInstalacion = -1;

		    try (Connection conn = Conexion.getInstance().getConnection()) {
		        
		        String sqlDisciplina = "INSERT INTO disciplina (nombre_disciplina) VALUES (?)";
		        try (PreparedStatement stmtDisciplina = conn.prepareStatement(sqlDisciplina, Statement.RETURN_GENERATED_KEYS)) {
		            stmtDisciplina.setString(1, nombreDisciplina);  
		            stmtDisciplina.executeUpdate();
		            try (ResultSet rs = stmtDisciplina.getGeneratedKeys()) {
		                if (rs.next()) {
		                    idDisciplina = rs.getInt(1); 
		                }
		            }
		        }

		        String sqlInstalacion = "INSERT INTO instalacion (nombre, direccion, descripcion, id_disciplina, id_club) VALUES (?, ?, ?, ?, ?)";
		        try (PreparedStatement stmtInstalacion = conn.prepareStatement(sqlInstalacion, Statement.RETURN_GENERATED_KEYS)) {
		            stmtInstalacion.setString(1, nombreInstalacion);
		            stmtInstalacion.setString(2, direccionInstalacion);
		            stmtInstalacion.setString(3, descripcion);
		            stmtInstalacion.setInt(4, idDisciplina);  
		            stmtInstalacion.setInt(5, idClub);
		            stmtInstalacion.executeUpdate();
		            try (ResultSet rs = stmtInstalacion.getGeneratedKeys()) {
		                if (rs.next()) {
		                    idInstalacion = rs.getInt(1);  
		                }
		            }
		        } catch (SQLException e) {
		            e.printStackTrace();
		            JOptionPane.showMessageDialog(null, "Error al registrar la instalación.");
		            return;  
		        }

		        
		        if (idInstalacion == -1) {
		            JOptionPane.showMessageDialog(null, "Error al registrar la instalación. El id_instalacion no fue generado.");
		            return; 
		        }

		        String sqlHorarioSemana = "INSERT INTO horario_instalacion (horario_semana_apertura, horario_semana_cierre, horario_fin_semana_apertura, horario_fin_semana_cierre, id_instalacion) VALUES (?, ?, ?, ?, ?)";
		        try (PreparedStatement stmtHorarioSemana = conn.prepareStatement(sqlHorarioSemana)) {
		            stmtHorarioSemana.setTime(1, Time.valueOf(horaApertura));  
		            stmtHorarioSemana.setTime(2, Time.valueOf(horaCierre));    
		            stmtHorarioSemana.setTime(3, Time.valueOf(horaAperturaFin));  
		            stmtHorarioSemana.setTime(4, Time.valueOf(horaCierreFin));   
		            stmtHorarioSemana.setInt(5, idInstalacion);  
		            stmtHorarioSemana.executeUpdate();
		        }

		        JOptionPane.showMessageDialog(
		            null,
		            "Disciplina e instalación registradas correctamente:\n" +
		                "Disciplina: " + nombreDisciplina + "\n" +
		                "Instalación: " + nombreInstalacion);

		    } catch (SQLException e) {
		        e.printStackTrace();
		        JOptionPane.showMessageDialog(null, "Error al registrar la disciplina, instalación o horarios.");
		    }
	  }
	  
	  private void registrarDirectorTecnico() {
		  
		  ClubDAO clubDAO = new ClubDAO();
		  int idClub = clubDAO.obtenerIdClubPorAdmin(email);

		    if (idClub == -1) {
		        JOptionPane.showMessageDialog(null, "Primero debe registrar un club.");
		        return;
		    }
		    
		    EquipoDAO equipoDAO = new EquipoDAO();
		    List<Equipo> equipos = equipoDAO.obtenerEquiposDelClub(idClub);

		    if (equipos.isEmpty()) {
		        JOptionPane.showMessageDialog(null, "Primero debe registrar al menos un equipo.");
		        return;
		    }

		    String nombre = JOptionPane.showInputDialog("Ingrese el nombre del Director Técnico:");
		    String apellido = JOptionPane.showInputDialog("Ingrese el apellido del Director Técnico:");
		    
		    String emailDT = null;
		    boolean emailValido = false;

		    while (!emailValido) {
		        emailDT = JOptionPane.showInputDialog("Ingrese el email del Director Técnico:");
		        if (emailDT == null || emailDT.isBlank()) {
		            JOptionPane.showMessageDialog(null, "El email no puede estar vacío.");
		            continue;
		        }
		        if (!emailDT.contains("@")) {
		            JOptionPane.showMessageDialog(null, "El email ingresado no es válido. Debe contener '@'.");
		            continue;
		        }
		        emailValido = true;
		    }

		    String password = JOptionPane.showInputDialog("Ingrese la contraseña del Director Técnico:");
		    if (password == null || password.isBlank()) {
		        JOptionPane.showMessageDialog(null, "La contraseña no puede estar vacía.");
		        return;
		    }

		    if (nombre == null || apellido == null || nombre.isBlank() || apellido.isBlank()) {
		        JOptionPane.showMessageDialog(null, "Todos los campos son obligatorios.");
		        return;
		    }

		    String[] nombresEquipos = new String[equipos.size()];
		    for (int i = 0; i < equipos.size(); i++) {
		        nombresEquipos[i] = equipos.get(i).getNombre();
		    }

		    String seleccion = (String) JOptionPane.showInputDialog(
		        null,
		        "Seleccione el equipo al que desea asignar el Director Técnico:",
		        "Equipo",
		        JOptionPane.QUESTION_MESSAGE,
		        null,
		        nombresEquipos,
		        nombresEquipos[0]
		    );

		    if (seleccion == null) {
		        JOptionPane.showMessageDialog(null, "No se seleccionó ningún equipo.");
		        return;
		    }

		    Equipo equipoSeleccionado = null;
		    for (Equipo eq : equipos) {
		        if (eq.getNombre().equals(seleccion)) {
		            equipoSeleccionado = eq;
		            break;
		        }
		    }

		    try (Connection conn = Conexion.getInstance().getConnection()) {
		        String sqlCheckDT = "SELECT COUNT(*) FROM persona WHERE id_equipo = ? AND rol = 'DT'";
		        try (PreparedStatement stmtCheckDT = conn.prepareStatement(sqlCheckDT)) {
		            stmtCheckDT.setInt(1, equipoSeleccionado.getIdEquipo());
		            try (ResultSet rs = stmtCheckDT.executeQuery()) {
		                if (rs.next() && rs.getInt(1) > 0) {
		                    JOptionPane.showMessageDialog(null, "El equipo ya tiene un Director Técnico asignado.");
		                    return; 
		                }
		            }
		        }

		        String sql = "INSERT INTO persona (nombre, apellido, email, password, rol, id_club, id_equipo) VALUES (?, ?, ?, ?, ?, ?, ?)";
		        try (PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
		            stmt.setString(1, nombre);
		            stmt.setString(2, apellido);
		            stmt.setString(3, emailDT); 
		            stmt.setString(4, password);
		            stmt.setString(5, "DT"); 
		            stmt.setInt(6, idClub); // 💡 сохраняем привязку к клубу
		            stmt.setInt(7, equipoSeleccionado.getIdEquipo());


		            int rowsAffected = stmt.executeUpdate();
		            if (rowsAffected > 0) {
		                try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
		                    if (generatedKeys.next()) {
		                        int directorId = generatedKeys.getInt(1); 

		                        JOptionPane.showMessageDialog(null,
		                            "Director Técnico asignado al equipo " + equipoSeleccionado.getNombre() + ":\n" +
		                            nombre + " " + apellido + "\nEmail: " + emailDT);
		                    }
		                }
		            } else {
		                JOptionPane.showMessageDialog(null, "Error al registrar el Director Técnico.");
		            }
		        }
		    } catch (SQLException e) {
		        e.printStackTrace();
		        JOptionPane.showMessageDialog(null, "Error al conectar con la base de datos.");
		    }
		}
	  
	  
	  
	  private void venderEntradas() { // ----- ESTE METODO NO ESTA CONNECTADO A LA BD
		  if (club == null) {
			  JOptionPane.showMessageDialog(null, "Primero debe registrar un club.");
			  return;
		  }

		  List<Partido> partidos = obtenerPartidosDisponibles();
		  if (partidos.isEmpty()) {
			  JOptionPane.showMessageDialog(null, "No hay partidos disponibles.");
			  return;
		  }

		  String[] nombresPartidos = new String[partidos.size()];
		  for (int i = 0; i < partidos.size(); i++) {
			  nombresPartidos[i] = partidos.get(i).toString();
		  }

		  String partidoSeleccionado = (String) JOptionPane.showInputDialog(
				  null,
				  "Seleccione el partido:",
				  "Partido",
				  JOptionPane.QUESTION_MESSAGE,
				  null,
				  nombresPartidos,
				  nombresPartidos[0]
		  );

		  if (partidoSeleccionado == null) return;

		  Partido partido = null;
		  for (Partido p : partidos) {
			  if (p.toString().equals(partidoSeleccionado)) {
				  partido = p;
				  break;
			  }
		  }

		  if (partido == null) {
			  JOptionPane.showMessageDialog(null, "Partido no válido.");
			  return;
		  }

		  int capacidadEstadio = partido.getEstadio().getCapacidad();
		  int entradasRestantes = capacidadEstadio;

		  Set<String> categoriasProcesadas = new HashSet<>();
		  String[] categorias = {"VIP", "General", "Economía"};

		  while (categoriasProcesadas.size() < categorias.length && entradasRestantes > 0) {
			  List<String> restantes = new ArrayList<>();
			  for (String cat : categorias) {
				  if (!categoriasProcesadas.contains(cat)) {
					  restantes.add(cat);
				  }
		  }

		  String categoriaSeleccionada = (String) JOptionPane.showInputDialog(
				  null,
				  "Seleccione la categoría de la entrada (faltan " + restantes.size() + "):\n" +
						  "Entradas restantes disponibles: " + entradasRestantes,
						  "Categoría",
						  JOptionPane.QUESTION_MESSAGE,
						  null,
						  restantes.toArray(),
						  restantes.get(0)
				  );

		  if (categoriaSeleccionada == null) return;


		  String precioStr = JOptionPane.showInputDialog( "Ingrese el precio de la entrada para la categoría " + categoriaSeleccionada + ":");
		  if (precioStr == null) continue;
		  double precio;
		  try {
			  precio = Double.parseDouble(precioStr);
			  if (precio <= 0) throw new NumberFormatException();
		  	} catch (NumberFormatException e) {
		  		JOptionPane.showMessageDialog(null, "Debe ingresar un precio válido y mayor que cero.");
		  		continue;
		  	}

		  String cantidadStr = JOptionPane.showInputDialog("Ingrese la cantidad de entradas a vender para categoría " + categoriaSeleccionada + ":\n" + "Máximo disponible: " + entradasRestantes);
		  	if (cantidadStr == null) continue;

		  	int cantidad;
		  	try {
		  		cantidad = Integer.parseInt(cantidadStr);
		  		if (cantidad <= 0) throw new NumberFormatException();
		  	} catch (NumberFormatException e) {
		  		JOptionPane.showMessageDialog(null, "La cantidad debe ser un número mayor que cero.");
		  		continue;
		  	}

		  	if (cantidad > entradasRestantes) {
		  		JOptionPane.showMessageDialog(null, "No hay suficientes entradas disponibles. Puedes vender hasta " + entradasRestantes + ".");
		  		continue;
		  	}

		  for (int i = 0; i < cantidad; i++) {
			  int contador = 0;
			  Entrada entrada = new Entrada(partido, categoriaSeleccionada, precio, contador++, false, club);
		  }

		  entradasRestantes -= cantidad;
		  categoriasProcesadas.add(categoriaSeleccionada);

		  JOptionPane.showMessageDialog(null,
		  cantidad + " entradas de categoría " + categoriaSeleccionada +
		  " vendidas.\nEntradas restantes para otras categorías: " + entradasRestantes);
		  }

		  if (entradasRestantes == 0) {
			  JOptionPane.showMessageDialog(null, "¡Se ha alcanzado la capacidad máxima del estadio!");
		  	} else {
		  		JOptionPane.showMessageDialog(null, "¡Venta completada para todas las categorías!");
		  	}
		  }

		  private List<Partido> obtenerPartidosDisponibles() {
			  List<Partido> partidosDisponibles = new ArrayList<>();


			  /* for (Torneo torneo : SistemaRegistro.torneosRegistrados) {
				  for (Partido partido : torneo.getPartidosPorCategoria("Primera")) { 
					  if (partido.getEstadio() != null) {
						  for (Estadio e : club.getEstadios()) {
							  if (e.getNombre().equals(partido.getEstadio().getNombre())) {
								  partidosDisponibles.add(partido);
								  break;
							  }
						  }
					  }
				  
			  } */

		  return partidosDisponibles;
	} 
		  
	private void registrarSocioPorEmail() { // ----- ESTE METODO TAMPOCO ESTA CONNECTADO A LA BASE DE DATOS ----
		if (club == null) {
			JOptionPane.showMessageDialog(null, "Primero debe registrar un club.");
			return;
		}
		String email = null;
		boolean emailValido = false;

			while (!emailValido) {
				email = JOptionPane.showInputDialog("Ingrese el correo electrónico del socio:");

			        if (email == null || email.isBlank()) {
			            JOptionPane.showMessageDialog(null, "El correo electrónico no puede estar vacío.");
			            continue;
			        }

			        if (!email.contains("@")) {
			            JOptionPane.showMessageDialog(null, "El correo electrónico ingresado no es válido. Debe contener '@'.");
			            continue;
			        }

			        if (club.getSociosEmailList().contains(email.toLowerCase())) {
			            JOptionPane.showMessageDialog(null, "Este correo ya está registrado como socio.");
			            return;
			        }

			        emailValido = true;
			    }

			    club.getSociosEmailList().add(email.toLowerCase());

			    JOptionPane.showMessageDialog(null, "El correo electrónico fue registrado exitosamente como socio permitido.");
			}
		  


}
