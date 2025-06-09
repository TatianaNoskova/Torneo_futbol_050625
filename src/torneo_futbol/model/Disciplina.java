package torneo_futbol.model;

public class Disciplina {
	
	int idDisciplina;
	String nombreDisciplina;
	
	
	
	public Disciplina(int idDisciplina, String nombreDisciplina) {
		
		this.idDisciplina = idDisciplina;
		this.nombreDisciplina = nombreDisciplina;
	}

	public int getIdDisciplina() {
		return idDisciplina;
	}

	public void setIdDisciplina(int idDisciplina) {
		this.idDisciplina = idDisciplina;
	}

	
	public String getNombreDisciplina() {
		return nombreDisciplina;
	}

	public void setNombreDisciplina(String nombreDisciplina) {
		this.nombreDisciplina = nombreDisciplina;
	}

	@Override
	public String toString() {
		return nombreDisciplina;
	}

	

	
	

}
