package torneo_futbol.model;

import java.util.*;

public class Torneo {
	
	protected int idTorneo;
	private String nombreTorneo;
	private String anoTorneo;
	private List<Equipo> equiposParticipantes = new ArrayList<>();
	private List<String> partidosSorteados; 
	private List<Partido> partidos;  
	
	public Torneo(int idTorneo, String nombreTorneo, String anoTorneo) {
		this.idTorneo = idTorneo;
		this.nombreTorneo = nombreTorneo;
		this.anoTorneo = anoTorneo;
		this.partidos = new ArrayList<>();
	}
	
	public int getIdTorneo() {
		return idTorneo;
	}
	
	
	
	public void setIdTorneo(int idTorneo) {
		this.idTorneo = idTorneo;
	}

	public String getNombreTorneo() {
		return nombreTorneo;
	}
	
	public String getAnoTorneo() {
		return anoTorneo;
	}
	
	public void agregarEquipoParticipante(Equipo equipo) {
	    equiposParticipantes.add(equipo);
	}

	public List<Equipo> getEquiposParticipantes() {
	    return equiposParticipantes;
	}
	
	public List<String> obtenerPartidosSorteados() {
        return partidosSorteados;
    }

    public void setPartidosSorteados(List<String> partidos) {
        partidosSorteados = partidos;
    }
    
    public void agregarPartido(Partido partido) {
        partidos.add(partido);
    }

    public Set<String> getCategoriasDePartidos() {
        Set<String> categorias = new HashSet<>();
        for (Partido partido : partidos) {
            if (partido.getEquipo1() != null) {
                categorias.add(partido.getEquipo1().getCategoria());
            }
            if (partido.getEquipo2() != null) {
                categorias.add(partido.getEquipo2().getCategoria());
            }
        }
        return categorias;
    }
    

    public List<Partido> getPartidosPorCategoria(String categoria) {
        List<Partido> partidosPorCategoria = new ArrayList<>();
        for (Partido partido : partidos) {
            
            if (partido.getEquipo1().getCategoria().equalsIgnoreCase(categoria) ||
                partido.getEquipo2().getCategoria().equalsIgnoreCase(categoria)) {
                partidosPorCategoria.add(partido);
            }
        }
        return partidosPorCategoria;
    }

}
