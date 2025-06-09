package torneo_futbol.model;

import java.util.*;

public class Club {

    private int id;
    private String nombre;
    private String direccion;
    
    private List<String> sociosEmailList = new ArrayList<>();
    private List<Equipo> equipos = new ArrayList<>();
    private List<Estadio> estadios = new ArrayList<>();
    private List<Disciplina> disciplinas = new ArrayList<>();
    

    public Club(String nombre, String direccion) {
        this.nombre = nombre;
        this.direccion = direccion;
    }

    public Club(int id, String nombre, String direccion) {
        this.id = id;
        this.nombre = nombre;
        this.direccion = direccion;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public String getDireccion() {
        return direccion;
    }
    
    public List<String> getSociosEmailList() {
        return sociosEmailList;
    }
    
    public List<Equipo> getEquipos() {
        return equipos;
    }

    public void setEquipo(List<Equipo> equipos) {
        this.equipos = equipos;
    }
    
    public List<Estadio> getEstadios() {
        return estadios;
    }

    public void setEstadios(List<Estadio> estadios) {
        this.estadios = estadios;
    }

    
    public List<Disciplina> getDisciplinas() {
        return disciplinas;
    }

    public void setDisciplinas(List<Disciplina> disciplinas) {
        this.disciplinas = disciplinas;
    }

    
    


    @Override
    public String toString() {
        return "Nombre del club: " + nombre + "\nDirección: " + direccion;
    }
}

