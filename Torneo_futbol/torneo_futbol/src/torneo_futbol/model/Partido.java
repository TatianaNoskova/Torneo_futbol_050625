package torneo_futbol.model;

import java.sql.*;
import java.time.*;
import java.time.format.DateTimeFormatter;

public class Partido {
	
protected int idPartido;
	
	private Estadio estadio;
	
	private Equipo equipo1;  
    private Equipo equipo2; 
    
    private LocalDate fecha;
    private LocalTime hora; 
    
    private Arbitro arbitro;
    private int golesEquipo1;
    private int golesEquipo2;
    private boolean resultadoCapturado = false;
    
    public Partido() {
        // vacio - para subir los datos de la BD
    }

    
    public Partido (Equipo equipo1, Equipo equipo2) {
    	
        this.equipo1 = equipo1;
        this.equipo2 = equipo2;
    }
    
    public int getIdPartido() {
        return idPartido;
    }
    
    
    public void setIdPartido(int idPartido) {
		this.idPartido = idPartido;
	}


	public Estadio getEstadio() {
        return estadio;
    }

    public void setEstadio(Estadio estadio) {
        this.estadio = estadio;
    }
    

    public Equipo getEquipo1() {
        return equipo1;
    }

    
    public Equipo getEquipo2() {
        return equipo2;
    }
    
    
    public void setEquipo1(Equipo equipo1) {
		this.equipo1 = equipo1;
	}


	public void setEquipo2(Equipo equipo2) {
		this.equipo2 = equipo2;
	}


	public LocalDate getFecha() {
		return fecha;
	}

	public LocalTime getHora() {
		return hora;
	}

	public void setFecha(LocalDate fecha) {
        this.fecha = fecha;
    }

    public void setHora(LocalTime hora) {
        this.hora = hora;
    }
    
    public void setArbitro(Arbitro arbitro) {
    	this.arbitro = arbitro;
    	}
    
    public LocalDateTime getFechaHora() {
        if (fecha != null && hora != null) {
            return LocalDateTime.of(fecha, hora);
        }
        return null;
    }


    public Arbitro getArbitro() {
    	return this.arbitro;
    	}
    public int getGolesEquipo1() { return golesEquipo1; }
    public int getGolesEquipo2() { return golesEquipo2; }

    public void setGolesEquipo1(int goles) { this.golesEquipo1 = goles; }
    public void setGolesEquipo2(int goles) { this.golesEquipo2 = goles; }

    public boolean resultadoCapturado() { return resultadoCapturado; }
    public void setResultadoCapturado(boolean capturado) { this.resultadoCapturado = capturado; }
    
 
    public void registrar(Connection conn, String categoria, int idTorneo) throws SQLException {
        String sql = "INSERT INTO partido (id_equipo1, id_equipo2, id_estadio, fecha_hora, id_torneo) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, equipo1.getIdEquipo());
            ps.setInt(2, equipo2.getIdEquipo());
            ps.setInt(3, 1); // estadio temporal
            ps.setTimestamp(4, null); 
            ps.setInt(5, idTorneo);

            ps.executeUpdate();

            
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    this.idPartido = rs.getInt(1); // 
                }
            }
        }
    }
    
    


    
    @Override
    public String toString() {
        String nombre1 = equipo1 != null ? equipo1.getNombre() : "???";
        String nombre2 = equipo2 != null ? equipo2.getNombre() : "???";

        LocalDateTime fechaHora = getFechaHora();
        String fechaHoraStr = (fechaHora != null)
                ? fechaHora.format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"))
                : "Fecha/hora no asignada";

        return nombre1 + " vs " + nombre2 + " (" + fechaHoraStr + ")";
    }




    

}


