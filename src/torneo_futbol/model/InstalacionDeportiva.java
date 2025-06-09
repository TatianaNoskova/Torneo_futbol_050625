package torneo_futbol.model;

public class InstalacionDeportiva {
	
	 private String nombreInstalacion; 
	    private Disciplina disciplina;   
	    private boolean estaReservada;   
	    private String fechaReserva;    

	    
	    public InstalacionDeportiva(String nombreInstalacion, Disciplina disciplina) {
	        this.nombreInstalacion = nombreInstalacion;
	        this.disciplina = disciplina;
	        this.estaReservada = false;   
	        this.fechaReserva = null;     
	    }

	    
	    public String getNombreInstalacion() {
	        return nombreInstalacion;
	    }

	    public void setNombreInstalacion(String nombreInstalacion) {
	        this.nombreInstalacion = nombreInstalacion;
	    }

	    public Disciplina getDisciplina() {
	        return disciplina;
	    }

	    public void setDisciplina(Disciplina disciplina) {
	        this.disciplina = disciplina;
	    }

	    public boolean isEstaReservada() {
	        return estaReservada;
	    }

	    public void setEstaReservada(boolean estaReservada) {
	        this.estaReservada = estaReservada;
	    }

	    public String getFechaReserva() {
	        return fechaReserva;
	    }

	    public void setFechaReserva(String fechaReserva) {
	        this.fechaReserva = fechaReserva;
	    }

}



