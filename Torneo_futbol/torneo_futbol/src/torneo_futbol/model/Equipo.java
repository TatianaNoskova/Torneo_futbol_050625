package torneo_futbol.model;

import java.io.File;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.swing.*;

import torneo_futbol.db.Conexion;



public class Equipo {
	
	protected int idEquipo;
    private String nombre;
    private String categoria;
    private String colores;
    private String rutaEscudo; 
    private Estadio estadioLocal;
    
    private DirectorTecnico directorTecnico;
    
    private Club club;
    
    
    
    public Equipo(int idEquipo, String nombre, String categoria, String colores, String rutaEscudo,
			Estadio estadioLocal, DirectorTecnico directorTecnico) {
		super();
		this.idEquipo = idEquipo;
		this.nombre = nombre;
		this.categoria = categoria;
		this.colores = colores;
		this.rutaEscudo = rutaEscudo;
		this.estadioLocal = estadioLocal;
		this.directorTecnico = directorTecnico;
	}
    
    public int getIdEquipo() {
    return idEquipo;
    }
    
    

    public void setIdEquipo(int idEquipo) {
		this.idEquipo = idEquipo;
	}

	public String getNombre() {
        return nombre;
    }

    public String getCategoria() {
        return categoria;
    }

    public String getColores() {
        return colores;
    }

    public ImageIcon getEscudo() {
        if (rutaEscudo != null && !rutaEscudo.isBlank()) {
            File archivo = new File(rutaEscudo);
            if (archivo.exists()) {
                return new ImageIcon(rutaEscudo);
            }
        }
        return null;
    }


    public Estadio getEstadioLocal() {
        return estadioLocal;
    }
    
    public DirectorTecnico getDirectorTecnico() {
        return directorTecnico;
    }

    public void setDirectorTecnico(DirectorTecnico directorTecnico) {
        this.directorTecnico = directorTecnico;
    }
    
    
  
    public void setNombre(String nombre) {
		this.nombre = nombre;
	}

	public void setCategoria(String categoria) {
		this.categoria = categoria;
	}

	public void setColores(String colores) {
		this.colores = colores;
	}

	public void setRutaEscudo(String rutaEscudo) {
		this.rutaEscudo = rutaEscudo;
	}

	public void setEstadioLocal(Estadio estadioLocal) {
		this.estadioLocal = estadioLocal;
	}
	
	public Estadio getEstadio() {
	    Estadio estadio = null;
	    String sql = "SELECT e.id_estadio, e.nombre, e.direccion, e.capacidad FROM estadio e "
	               + "WHERE e.id_club = ? LIMIT 1";  

	    try (Connection conn = Conexion.getInstance().getConnection();
	         PreparedStatement stmt = conn.prepareStatement(sql)) {
	        
	    	stmt.setInt(1, this.club.getId());

	        
	        try (ResultSet rs = stmt.executeQuery()) {
	            if (rs.next()) {
	                int idEstadio = rs.getInt("id_estadio");
	                String nombre = rs.getString("nombre");
	                String direccion = rs.getString("direccion");
	                int capacidad = rs.getInt("capacidad");
	                
	                estadio = new Estadio(idEstadio, nombre, direccion, capacidad);  
	            }
	        }
	    } catch (SQLException e) {
	        e.printStackTrace();
	    }

	    return estadio;
	}
	
	
	public Club getClub() {
		return club;
	}


	public void setClub(Club club) {
		this.club = club;
	}


	@Override
    public String toString() {
        return "Equipo: " + nombre +
               "\nCategoría: " + categoria +
               "\nColores: " + colores +
               "\nEscudo: " + rutaEscudo + 
               "\nEstadio Local: " + (estadioLocal != null ? estadioLocal.getNombre() : "No asignado");  
    }

}


