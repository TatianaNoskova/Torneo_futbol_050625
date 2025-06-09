package torneo_futbol.model;

public class Entrada {
	
	private Partido partido;
	private String categoria; 
	private double precio; 
	private int id;
	private boolean vendida;
	private Club clubVendedor;

	public Entrada(Partido partido, String categoria, double precio, int id, boolean vendida, Club clubVendedor) {
	this.partido = partido;
	this.categoria = categoria;
	this.precio = precio;
	this.id = id;
	this.vendida = vendida;
	this.clubVendedor = clubVendedor;
	}

	public Partido getPartido() {
	return partido;
	}

	public void setPartido(Partido partido) {
	this.partido = partido;
	}

	public String getCategoria() {
	return categoria;
	}

	public void setCategoria(String categoria) {
	this.categoria = categoria;
	}

	public double getPrecio() {
	return precio;
	}

	public void setPrecio(double precio) {
	this.precio = precio;
	}

	public int getId() {
	return id;
	}

	public void setId(int id) {
	this.id = id;
	}

	public boolean isVendida() {
	return vendida;
	}

	public void setVendida(boolean vendida) {
	this.vendida = vendida;
	}

	public Club getClubVendedor() {
	return clubVendedor;
	}

	public void setClubVendedor(Club clubVendedor) {
	this.clubVendedor = clubVendedor;
	}

	@Override
	public String toString() {
	return "Entrada [id=" + id + ", partido=" + partido + ", categoria=" + categoria + ", precio=" + precio + ", vendida=" + vendida + "]";
	}
	

}


