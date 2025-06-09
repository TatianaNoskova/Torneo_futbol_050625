package torneo_futbol.model;

public class Persona {
	
	protected String nombre;
	protected String apellido;
	protected String email;
	private String password;
	
	protected int idPersona;

	public Persona(String nombre, String apellido, String email, String password) {
		this.nombre = nombre;
		this.apellido = apellido;
		this.email = email;
		this.password = password;
	}

	public String getNombre() {
		return nombre;
	}

	public String getApellido() {
		return apellido;
	}
	
	public String getEmail() {
		return email;
	}
		
	public void setPassword(String password) {
		this.password = password;
	}

	public String getPassword() {
		return password;
	}
	
	public int getIdPersona() {
	    return idPersona;
	}

	public void setIdPersona(int idPersona) {
	    this.idPersona = idPersona;
	}

	public void mostrarMenu() {
			
			
	}

}
