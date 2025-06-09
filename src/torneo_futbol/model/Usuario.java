package torneo_futbol.model;

import java.util.*;

import javax.swing.JOptionPane;

public class Usuario extends Persona {
	
	protected String rol;

	public Usuario(String nombre, String apellido, String email, String password, String rol) {
		super(nombre, apellido, email, password);
		this.rol = rol;
	}
	
	public String getRol() {
		return rol;
	}
	
	@Override
	public void mostrarMenu() {
		boolean salir = false;

		while (!salir) {
			String[] opciones = {
					"Seleccionar disciplina deportiva y gestionar el club",
					"Comprar entradas para partidos",
					
					"Salir"
			};

			String seleccion = (String) JOptionPane.showInputDialog(
					null,
					"Menú Usuario:\n" +
							"Seleccione una opción:",
					"Menú Usuario",
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
			case "Seleccionar disciplina deportiva y gestionar el club" -> mostrarSubmenuClub();
			case "Comprar entradas para partidos" -> mostrarSubmenuCompraEntradas();
			
			default -> JOptionPane.showMessageDialog(null, "Opción no válida.");
		}
	}

	private void mostrarSubmenuClub() {
		String[] opciones = {
				"Búsqueda por nombre de Club",
				"Búsqueda por nombre de Disciplina",
				"Consultar fechas y horarios disponibles",
				"Reservar / modificar / cancelar reserva",
				"Volver"
		};

		boolean volver = false;

		while (!volver) {
			String seleccion = (String) JOptionPane.showInputDialog(
					null,
					"Submenú - Gestión de los Clubes y Diciplinas",
					"Opciones",
					JOptionPane.QUESTION_MESSAGE,
					null,
					opciones,
					opciones[0]);

			if (seleccion == null || seleccion.equals("Volver")) {
				volver = true;
			} else {
				switch (seleccion) {
					case "Búsqueda por nombre de Club" -> {
						// realizarBusqueda("Club");
					}
					case "Búsqueda por nombre de Disciplina" -> {
						// realizarBusqueda("Disciplina");
					}
					case "Consultar fechas y horarios disponibles",
							"Reservar / modificar / cancelar reserva" ->
						JOptionPane.showMessageDialog(null,
								"Has seleccionado: " + seleccion + "\n(Función aún no implementada)");
					default -> JOptionPane.showMessageDialog(null, "Opción no válida.");
				}
			}
		}
	}

	private void mostrarSubmenuCompraEntradas() {
		String[] opciones = {
				"Ver próximos partidos",
				"Elegir categoría de entrada",
				"Comprar entrada",
				"Solicitar reembolso",
				"Volver"
		};

		String seleccion = (String) JOptionPane.showInputDialog(
				null,
				"Submenú - Compra de Entradas",
				"Opciones",
				JOptionPane.QUESTION_MESSAGE,
				null,
				opciones,
				opciones[0]);

		if (seleccion != null && !seleccion.equals("Volver")) {
			JOptionPane.showMessageDialog(null,
					"Has seleccionado: " + seleccion + "\n(Función aún no implementada)");
		}
	}
	/* 
	public List<Disciplina> buscarDisciplinasPorNombreClub(String nombreClub) {
		for (Club club : SistemaRegistro.clubesRegistrados) {
			if (club.getNombre().equalsIgnoreCase(nombreClub)) {
				return club.getDisciplinas();
			}
		}
		return new ArrayList<>();
	}

	public List<Club> buscarClubesPorNombreDisciplina(String nombreDisciplina) {
		List<Club> clubesConDisciplina = new ArrayList<>();
		for (Club club : SistemaRegistro_old.clubesRegistrados) {
			for (Disciplina disciplina : club.getDisciplinas()) {
				if (disciplina.getNombreDisciplina().equalsIgnoreCase(nombreDisciplina)) {
					clubesConDisciplina.add(club);
					break;
				}
			}
		}
		return clubesConDisciplina;
	} 

	public void realizarBusqueda(String tipoBusqueda) {
		String input = JOptionPane.showInputDialog(
				"Ingrese el nombre del " + (tipoBusqueda.equals("Club") ? "Club" : "Disciplina") + ":");

		if (input != null && !input.isEmpty()) {
			if (tipoBusqueda.equals("Club")) {
				// Busqueda por nombre de Club
				List<Disciplina> disciplinas = buscarDisciplinasPorNombreClub(input);
				if (disciplinas.isEmpty()) {
					JOptionPane.showMessageDialog(null, "No se encontraron disciplinas para el club: " + input);
				} else {
					StringBuilder disciplinasInfo = new StringBuilder("Disciplinas del club " + input + ":\n");
					for (Disciplina disciplina : disciplinas) {
						disciplinasInfo.append(disciplina.getNombreDisciplina()).append("\n");
					}
					JOptionPane.showMessageDialog(null, disciplinasInfo.toString());
				}
			} else if (tipoBusqueda.equals("Disciplina")) {
				// Busqueda por nombre de Diciplina
				List<Club> clubes = buscarClubesPorNombreDisciplina(input);
				if (clubes.isEmpty()) {
					JOptionPane.showMessageDialog(null, "No se encontraron clubes con la disciplina: " + input);
				} else {
					StringBuilder clubesInfo = new StringBuilder("Clubes con la disciplina " + input + ":\n");
					for (Club club : clubes) {
						clubesInfo.append(club.getNombre()).append("\n");
					}
					JOptionPane.showMessageDialog(null, clubesInfo.toString());
				}
			}
		} else {
			JOptionPane.showMessageDialog(null, "Por favor, ingrese un nombre válido.");
		}
	} */
	

}
