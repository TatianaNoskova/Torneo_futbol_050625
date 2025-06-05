package torneo_futbol.model;

import java.util.*;

public class SistemaTorneos {
	
	 private List<Torneo> torneos;

	    public SistemaTorneos() {
	        torneos = new ArrayList<>();
	    }

	    public void agregarTorneo(Torneo torneo) {
	        torneos.add(torneo);
	    }

	    public List<Torneo> obtenerTorneos() {
	    	return torneos; 
	    	
	    }

	    public void mostrarTorneos() {
	        if (torneos.isEmpty()) {
	            System.out.println("No hay torneos registrados.");
	        } else {
	            for (Torneo torneo : torneos) {
	                System.out.println("Torneo: " + torneo.getNombreTorneo() + " (" + torneo.getAnoTorneo() + ")");
	                System.out.println("Equipos participantes:");
	                for (Equipo equipo : torneo.getEquiposParticipantes()) {
	                    System.out.println(" - " + equipo.getNombre());
	                }
	            }
	        }
	    }


}
