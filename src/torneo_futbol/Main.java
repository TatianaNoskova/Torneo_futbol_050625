package torneo_futbol;


import javax.swing.*;

import torneo_futbol.model.SistemaRegistro;
import torneo_futbol.ui.VentanaPrincipal;

public class Main {
	
	

	public static void main(String[] args) {
	    SwingUtilities.invokeLater(() -> {
	        VentanaPrincipal ventana = new VentanaPrincipal();
	        ventana.setVisible(true);
	    });
	}


}
