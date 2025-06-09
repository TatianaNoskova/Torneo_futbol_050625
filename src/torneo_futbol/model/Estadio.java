package torneo_futbol.model;

public class Estadio {
	
	protected int idEstadio;
	private String nombre;
    private String direccion;
    private int capacidad;
    
	public Estadio(int idEstadio, String nombre, String direccion, int capacidad) {
		
		this.idEstadio = idEstadio;
		this.nombre = nombre;
		this.direccion = direccion;
		this.capacidad = capacidad;
	}

	public int getIdEstadio() {
		return idEstadio;
	}

	public void setIdEstadio(int idEstadio) {
		this.idEstadio = idEstadio;
	}

	public String getNombre() {
		return nombre;
	}

	public void setNombre(String nombre) {
		this.nombre = nombre;
	}

	public String getDireccion() {
		return direccion;
	}

	public void setDireccion(String direccion) {
		this.direccion = direccion;
	}

	public int getCapacidad() {
		return capacidad;
	}

	public void setCapacidad(int capacidad) {
		this.capacidad = capacidad;
	}
	
	@Override
    public String toString() {
        return nombre + " (Dirección: " + direccion + ", Capacidad: " + capacidad + ")";
    }
    
    

}
