package torneo_futbol.model;

import java.util.*;

public class Torneo {
	
	protected int idTorneo;
	private String nombreTorneo;
	private String anoTorneo;
	private List<Equipo> equiposParticipantes = new ArrayList<>();
	private List<String> partidosSorteados; 
	private List<Partido> partidos;
	private String estado;  // "CREADO", "EN_PROGRESO", "FINALIZADO"
	
	public Torneo(int idTorneo, String nombreTorneo, String anoTorneo) {
		this.idTorneo = idTorneo;
		this.nombreTorneo = nombreTorneo;
		this.anoTorneo = anoTorneo;
		this.partidos = new ArrayList<>();
		this.estado = "CREADO";  // Valor por defecto
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
	
	
	
	public void setNombreTorneo(String nombreTorneo) {
		this.nombreTorneo = nombreTorneo;
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
    
    
    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    
    public List<Partido> getPartidos() {
	return partidos;
	}

	public void setPartidos(List<Partido> partidos) {
		this.partidos = partidos;
	}

	public List<String> getPartidosSorteados() {
		return partidosSorteados;
	}

	public void setAnoTorneo(String anoTorneo) {
		this.anoTorneo = anoTorneo;
	}

	public void setEquiposParticipantes(List<Equipo> equiposParticipantes) {
		this.equiposParticipantes = equiposParticipantes;
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
    
 // verifica si el torneo está completo (8 equipos)
    public boolean estaCompleto() {
        return equiposParticipantes.size() == 8;
    }

    // sortea equipos (devuelve lista ordenada aleatoriamente)
    public List<Equipo> sortearEquipos() {
        Collections.shuffle(equiposParticipantes);
        return equiposParticipantes;
    }

}
