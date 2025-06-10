package torneo_futbol.ui;

import java.awt.*;
import java.sql.*;
import java.util.List;
import java.util.ArrayList;
import java.util.Collections;
import java.util.*;
import javax.swing.*;
import torneo_futbol.db.Conexion;
import torneo_futbol.dao.*;
import torneo_futbol.dao.TorneoDAO;
import torneo_futbol.dao.EquipoTorneoDAO; 
import torneo_futbol.dao.EquipoDAO;
import torneo_futbol.model.AdminAFA;
import torneo_futbol.model.Torneo;
import torneo_futbol.model.Equipo;
import torneo_futbol.model.*;

public class PanelAdminAfa extends JPanel {
    private AdminAFA admin;  

    public PanelAdminAfa(AdminAFA admin) {  
        this.admin = admin;  
        inicializar();
    }

    private void inicializar() {
        setLayout(new BorderLayout(20, 0));
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Panel izquierdo - Menú de opciones
        JPanel panelIzquierdo = new JPanel();
        panelIzquierdo.setLayout(new BoxLayout(panelIzquierdo, BoxLayout.Y_AXIS));

        JLabel titulo = new JLabel("Panel de Administración AFA");
        titulo.setFont(new Font("Arial", Font.BOLD, 20));
        titulo.setAlignmentX(Component.CENTER_ALIGNMENT);
        panelIzquierdo.add(titulo);
        panelIzquierdo.add(Box.createVerticalStrut(20));

        // Botones para cada funcionalidad
        panelIzquierdo.add(crearBoton("Gestionar Torneos", e -> gestionarTorneos()));
        panelIzquierdo.add(Box.createVerticalStrut(10));
        panelIzquierdo.add(crearBoton("Asignar Estadios/Árbitros", e -> asignarRecursos()));
        panelIzquierdo.add(Box.createVerticalStrut(10));
        panelIzquierdo.add(crearBoton("Generar Partidos", e -> generarPartidos()));
        panelIzquierdo.add(Box.createVerticalStrut(10));
        panelIzquierdo.add(crearBoton("Registrar Eventos", e -> registrarEventos()));

        add(panelIzquierdo, BorderLayout.CENTER);
    }

    private JButton crearBoton(String texto, java.awt.event.ActionListener listener) {
        JButton boton = new JButton(texto);
        boton.setAlignmentX(Component.LEFT_ALIGNMENT);
        boton.addActionListener(listener);
        return boton;
    }

    // --- Funcionalidades del AdminAfa ---
    private void gestionarTorneos() {
        // 1. Obtener torneos existentes desde la BD
        List<Torneo> torneos = new ArrayList<>();  
        try (Connection conn = Conexion.getInstance().getConnection()) {
            torneos = new TorneoDAO().obtenerTodos(conn);  
        } catch (SQLException e) {
            e.printStackTrace();
        }

        // 2. Mostrar diálogo de gestión (crear/editar/eliminar)
        JPanel panel = new JPanel(new GridLayout(0, 1));
        JComboBox<Torneo> combo = new JComboBox<>(torneos.toArray(new Torneo[0]));
        panel.add(new JLabel("Seleccione un torneo:"));
        panel.add(combo);

        String[] opciones = {"Crear Nuevo", "Editar", "Eliminar", "Asignar Equipos"};
        int opcion = JOptionPane.showOptionDialog(
            this, panel, "Gestión de Torneos", 
            JOptionPane.DEFAULT_OPTION, JOptionPane.PLAIN_MESSAGE, 
            null, opciones, opciones[0]
        );

        if (opcion == 0) {
            crearTorneo();
        } else if (opcion == 1 || opcion == 2 || opcion == 3) {
            Torneo seleccionado = (Torneo) combo.getSelectedItem();
            if (seleccionado != null) {
                if (opcion == 1) editarTorneo(seleccionado);  
                else if (opcion == 2) eliminarTorneo(seleccionado);  
                else if (opcion == 3) asignarEquiposTorneo(seleccionado);
            }
        }
    }

    private void crearTorneo() {
        String nombre = JOptionPane.showInputDialog("Nombre del torneo:");
        String ano = JOptionPane.showInputDialog("Año del torneo:");
        
        if (nombre == null || nombre.trim().isEmpty()) return;

        try (Connection conn = Conexion.getInstance().getConnection()) {
            // Obtener próximo ID 
            int nuevoId = obtenerProximoIdTorneo(conn);
            
            Torneo torneo = new Torneo(nuevoId, nombre, ano); 
            torneo.setEstado("CREADO");
            
            if (new TorneoDAO().insertar(conn, torneo)) {  // AQUI HAY UN ERROR - Type mismatch: cannot convert from void to boolean
                JOptionPane.showMessageDialog(this, "Torneo creado. ID: " + torneo.getIdTorneo());
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error al crear torneo.");
        }
    }
    
    
    

    private int obtenerProximoIdTorneo(Connection conn) throws SQLException {
        String sql = "SELECT MAX(idTorneo) FROM torneos";
        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            return rs.next() ? rs.getInt(1) + 1 : 1;
        }
    }
    
    
    private void editarTorneo(Torneo torneo) {
        String nuevoNombre = JOptionPane.showInputDialog(this, "Nuevo nombre:", torneo.getNombreTorneo());
        if (nuevoNombre != null && !nuevoNombre.trim().isEmpty()) {
            try (Connection conn = Conexion.getInstance().getConnection()) {
                String sql = "UPDATE torneos SET nombre = ? WHERE id = ?";
                try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                    pstmt.setString(1, nuevoNombre);
                    pstmt.setInt(2, torneo.getIdTorneo());
                    pstmt.executeUpdate();
                    torneo.setNombreTorneo(nuevoNombre);
                    JOptionPane.showMessageDialog(this, "Torneo actualizado.");
                }
            } catch (SQLException e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(this, "Error al actualizar torneo.");
            }
        }
    }

    private void eliminarTorneo(Torneo torneo) {
        int confirm = JOptionPane.showConfirmDialog(this, 
            "¿Eliminar el torneo " + torneo.getNombreTorneo() + "?", 
            "Confirmar", JOptionPane.YES_NO_OPTION);
        
        if (confirm == JOptionPane.YES_OPTION) {
            try (Connection conn = Conexion.getInstance().getConnection()) {
                String sql = "DELETE FROM torneos WHERE id = ?";
                try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                    pstmt.setInt(1, torneo.getIdTorneo());
                    pstmt.executeUpdate();
                    JOptionPane.showMessageDialog(this, "Torneo eliminado.");
                }
            } catch (SQLException e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(this, "Error al eliminar torneo.");
            }
        }
    }
    
    
    

    private void asignarEquiposTorneo(Torneo torneo) {
        // 1. Obtener equipos disponibles por categoría
        List<Equipo> equipos = new ArrayList<>();  
        try (Connection conn = Conexion.getInstance().getConnection()) {
            equipos = new EquipoDAO().obtenerTodos(conn);  
        } catch (SQLException e) {
            e.printStackTrace();
        }

        // 2. seleccionar 8 equipos 
        JList<Equipo> listaEquipos = new JList<>(equipos.toArray(new Equipo[0]));
        listaEquipos.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        JScrollPane scrollPane = new JScrollPane(listaEquipos);

        int confirm = JOptionPane.showConfirmDialog(
            this, scrollPane, "Seleccione 8 equipos", 
            JOptionPane.OK_CANCEL_OPTION
        );

        if (confirm == JOptionPane.OK_OPTION && listaEquipos.getSelectedValuesList().size() == 8) {
            // 3. Guardar equipos en el torneo 
            Collections.shuffle(listaEquipos.getSelectedValuesList()); // Sorteo aleatorio
            try (Connection conn = Conexion.getInstance().getConnection()) {
            	EquipoTorneoDAO dao = new EquipoTorneoDAO(); 
                for (int i = 0; i < 8; i++) {
                    dao.asignarEquipo(conn, torneo.getIdTorneo(), listaEquipos.getSelectedValuesList().get(i).getIdEquipo(), i + 1); 
                }
                JOptionPane.showMessageDialog(this, "8 equipos asignados al torneo.");
            } catch (SQLException e) {
                e.printStackTrace();
            }
        } else {
            JOptionPane.showMessageDialog(this, "Debe seleccionar exactamente 8 equipos.");
        }
    }

    private void generarPartidos() {
        // Lógica para generar partidos (cuartos, semifinales, final)
        // Similar a TorneoCRUD.java pero integrado aquí
    }

    private void asignarRecursos() {
        // Lógica para asignar estadios/árbitros a partidos
    }

    private void registrarEventos() {
        // Lógica para registrar eventos en partidos
    }
}