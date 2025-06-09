package torneo_futbol.ui;

import java.awt.*;
import java.io.*;
import java.net.URL;

import javax.swing.*;
import javax.swing.border.*;

import torneo_futbol.model.SistemaRegistro;

public class VentanaPrincipal extends JFrame {

    private static final long serialVersionUID = 1L;
    private JPanel contentPane;
    private SistemaRegistro sistema;

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }

        EventQueue.invokeLater(() -> {
            try {
                VentanaPrincipal frame = new VentanaPrincipal();
                frame.setVisible(true);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    public VentanaPrincipal() {
        sistema = new SistemaRegistro();

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setTitle("Bienvenido al Sistema de Gestion del Torneo del Futbol");

        setBounds(100, 100, 800, 500);

        contentPane = new JPanel(new FlowLayout(FlowLayout.CENTER, 50, 30));
        contentPane.setBorder(new EmptyBorder(20, 20, 20, 20));
        setContentPane(contentPane);

        // Левая панель (текст + кнопки)
        JPanel leftPanel = new JPanel();
        leftPanel.setLayout(new BoxLayout(leftPanel, BoxLayout.Y_AXIS));
        leftPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 20));

        JLabel bienvenida = new JLabel("<html><h1>Bienvenido</h1><p>Seleccione una opción para comenzar:</p></html>");
        bienvenida.setAlignmentX(Component.LEFT_ALIGNMENT);
        leftPanel.add(bienvenida);
        leftPanel.add(Box.createRigidArea(new Dimension(0, 20)));

     // Кнопка Registrarse с иконкой
        JButton btnRegistrarse = new JButton("Registrarse", new ImageIcon(getClass().getResource("/icons/register.png")));
        btnRegistrarse.setAlignmentX(Component.LEFT_ALIGNMENT);
        btnRegistrarse.addActionListener(e -> sistema.registrarNuevoUsuario());
        leftPanel.add(btnRegistrarse);
        leftPanel.add(Box.createRigidArea(new Dimension(0, 10)));

        // Кнопка Iniciar sesión с иконкой
        JButton btnIniciarSesion = new JButton("Iniciar sesión", new ImageIcon(getClass().getResource("/icons/login.png")));
        btnIniciarSesion.setAlignmentX(Component.LEFT_ALIGNMENT);
        btnIniciarSesion.addActionListener(e -> {
			try {
				sistema.iniciarSesion();
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		});
        leftPanel.add(btnIniciarSesion);
        leftPanel.add(Box.createRigidArea(new Dimension(0, 10)));

        // Кнопка Salir с иконкой
        JButton btnSalir = new JButton("Salir", new ImageIcon(getClass().getResource("/icons/exit.png")));
        btnSalir.setAlignmentX(Component.LEFT_ALIGNMENT);
        btnSalir.addActionListener(e -> System.exit(0));
        leftPanel.add(btnSalir);


        contentPane.add(leftPanel, BorderLayout.CENTER);

        
        JLabel imageLabel = new JLabel();
        URL imageUrl = getClass().getResource("/icons/welcome.png");
        if (imageUrl != null) {
            imageLabel.setIcon(new ImageIcon(imageUrl));
        } else {
            System.err.println("No pude encontrar el imagen: /icons/welcome.png");
            imageLabel.setText("El imagen no fue encotrado");
        }
        contentPane.add(imageLabel, BorderLayout.EAST);
    }
}
