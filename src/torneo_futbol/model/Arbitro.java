package torneo_futbol.model;

import java.sql.*;
import javax.swing.*;

public class Arbitro extends Usuario {
	
	protected int idArbitro;

	public Arbitro (String nombre, String apellido, String email, String password) {
        super(nombre, apellido, email, password, "Árbitro" );
        
        this.email = email;
       
    }
    
    public Arbitro(int idArbitro, String nombre, String apellido) {
        super(nombre, apellido, null, null, "Árbitro");
        this.idArbitro = idArbitro;  
    }
    
    public int getidArbitro() {
    	return idArbitro;
    }


	public String getEmail() {
		return email;
	}
	
	
	public int getIdArbitro() {
		return idArbitro;
	}
		
	public void setIdArbitro(int idArbitro) {
		this.idArbitro = idArbitro;
	}
	
	@Override
    public void mostrarMenu() {
		boolean salir = false;
		
		while (!salir) {
			String[] opciones = {
                "Ver partidos asignados",
                "Ver horario y lugar de los partidos",
                "Salir"
			};
			
			String seleccion = (String) JOptionPane.showInputDialog(
                    null,
                    "Bienvenido " + nombre + " " + apellido + "\n\nSelecciona una opción:",
                    "Menú Árbitro",
                    JOptionPane.PLAIN_MESSAGE,
                    null,
                    opciones,
                    opciones[0]
			);
                    		
                            
            if (seleccion == null || seleccion.equals("Salir")) {
                salir = true;
            } else {
                procesarOpcion(seleccion);
            }
        }
    }
	
	 private void procesarOpcion(String seleccion) {
	        switch (seleccion) {
	            case "Ver partidos asignados" -> mostrarSubmenuPartidosAsignados();
	            case "Ver horario y lugar de los partidos" -> mostrarSubmenuHorarioLugar();
	            
	            default -> JOptionPane.showMessageDialog(null, "Opción no valida.");
	        }
	    }


		
	 private void mostrarSubmenuPartidosAsignados() {
		 String seleccion = "Ver partidos asignados";
		    JOptionPane.showMessageDialog(
		        null,
		        "Has seleccionado: " + seleccion + "\n(Función aún no implementada)"
		    );
		}
	 
	 private void mostrarSubmenuHorarioLugar() {
		 String seleccion = "Ver horario y lugar de los partidos";
		    JOptionPane.showMessageDialog(
		        null,
		        "Has seleccionado: " + seleccion + "\n(Función aún no implementada)"
		    );
		}
	 
	 
	 public static Arbitro obtenerArbitroPorId(Connection conn, int idArbitro) throws SQLException {
		    String query = "SELECT id_persona, nombre, apellido FROM persona WHERE id_persona = ? AND rol = 'Arbitro'";
		    try (PreparedStatement stmt = conn.prepareStatement(query)) {
		        stmt.setInt(1, idArbitro);
		        try (ResultSet rs = stmt.executeQuery()) {
		            if (rs.next()) {
		                return new Arbitro(
		                    rs.getInt("id_persona"),
		                    rs.getString("nombre"),
		                    rs.getString("apellido")
		                );
		            } else {
		                throw new SQLException("No se encontró árbitro con ID: " + idArbitro);
		            }
		        }
		    }
		}


	        
	 @Override
	 public String toString() {
	 return nombre + " " + apellido;
	 }           
        
    
}
	
	


