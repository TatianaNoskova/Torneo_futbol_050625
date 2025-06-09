package torneo_futbol.model;

import javax.swing.*;

public class DirectorTecnico extends Usuario {
	
	protected int idDT;

	public DirectorTecnico(String nombre, String apellido, String email, String password) {
		super(nombre, apellido, email, password, "Director Tecnico");
	
	this.idDT = idDT;
	this.email = email;
	
	}
	
	
	
	
	public int getIdDT() {
		return idDT;
	}
	
	
	

	public void setIdDT(int idDT) {
		this.idDT = idDT;
	}




	public String getEmail() {
		return email;
	}

	
	@Override
		public void mostrarMenu() {
		boolean salir = false;
	
		while (!salir) {
			String[] opciones = {
					"Registrar jugadores",
					"Hacer alineación",
					"Consultar estadísticas",
					"Salir"
			};
    
    
			String seleccion = (String) JOptionPane.showInputDialog(
					null,
					"Bienvenido " + nombre + " " + apellido + "\n\nSelecciona una opción:",
					"Menú Director Técnico",
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
        case "Registrar jugadores" -> mostrarSubmenuRegistrarJugadores();
        case "Hacer alineación"-> mostrarSubmenuHacerAlineacion();
        case "Consultar estadísticas" -> mostrarSubmenuConsultarEstadisticas();
        default -> JOptionPane.showMessageDialog(null, "Opción no válida.");
		}
	}
    
	private void mostrarSubmenuRegistrarJugadores() {
		String seleccion = "Registrar Jugadores";
		JOptionPane.showMessageDialog(
		null,
		"Has seleccionado: " + seleccion + "\n(Función aún no implementada)"
		 );
	}

	private void mostrarSubmenuHacerAlineacion() {
		String seleccion = "Hacer alineacion";
		JOptionPane.showMessageDialog(
		null,
		"Has seleccionado: " + seleccion + "\n(Función aún no implementada)"
		 );
	}

	private void mostrarSubmenuConsultarEstadisticas() {
		String seleccion = "Consultar estadisticas";
		JOptionPane.showMessageDialog(
		null,
		"Has seleccionado: " + seleccion + "\n(Función aún no implementada)"
		);
}
	
	@Override
	public String toString() {
		 return nombre + " " + apellido;
		 }   

}
