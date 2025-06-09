package torneo_futbol.ui;

import java.awt.*;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.sql.*;
import java.util.function.Function;
import java.util.ArrayList;
import java.util.List;

import javax.swing.*;

import torneo_futbol.dao.ClubDAO;
import torneo_futbol.dao.DisciplinaDAO;
import torneo_futbol.dao.EquipoDAO;
import torneo_futbol.dao.EstadioDAO;
import torneo_futbol.dao.PersonaDAO;
import torneo_futbol.db.Conexion;
import torneo_futbol.model.AdminClub;
import torneo_futbol.model.Club;
import torneo_futbol.model.DirectorTecnico;
import torneo_futbol.model.Disciplina;
import torneo_futbol.model.Equipo;
import torneo_futbol.model.Estadio;
import torneo_futbol.model.Persona;


public class PanelAdminClub extends JPanel {
	
	private AdminClub admin;  // объект администратора
    private Club club;

    // Обновленный конструктор
    public PanelAdminClub(AdminClub admin, Club club) {
        this.admin = admin;  // передаем объект admin
        this.club = club;
        inicializar();
    }
    
    

    
    
    private void inicializar() {
        setLayout(new BorderLayout(20, 0));
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Izquierda — contenido principal (info +  secciones )
        JPanel panelIzquierdo = new JPanel();
        panelIzquierdo.setLayout(new BoxLayout(panelIzquierdo, BoxLayout.Y_AXIS));

        JLabel titulo = new JLabel("Panel de Administración del Club");
        titulo.setFont(new Font("Arial", Font.BOLD, 20));
        titulo.setAlignmentX(Component.CENTER_ALIGNMENT);
        panelIzquierdo.add(titulo);

        panelIzquierdo.add(Box.createVerticalStrut(20));

        panelIzquierdo.add(seccionInfoClub());
        panelIzquierdo.add(Box.createVerticalStrut(10));

        panelIzquierdo.add(seccionCRUD( "Equipos", club.getEquipos(), "equipo", (Equipo e) -> e.getNombre()));
        panelIzquierdo.add(Box.createVerticalStrut(10));

        panelIzquierdo.add(seccionCRUD("Estadios", club.getEstadios(), "estadio", (Estadio e) -> e.getNombre()));
        panelIzquierdo.add(Box.createVerticalStrut(10));

        panelIzquierdo.add(seccionCRUD("Disciplinas", club.getDisciplinas(), "disciplina", (Disciplina e) -> e.getNombreDisciplina()));

        add(panelIzquierdo, BorderLayout.CENTER);

        // Правая часть — эмблема клуба
        add(seccionEscudo(), BorderLayout.EAST);
    }

    private JPanel seccionInfoClub() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel label = new JLabel("Club: " + club.getNombre());
        label.setFont(new Font("Tahoma", Font.BOLD, 14));
        panel.add(label);
        panel.add(new JLabel("Dirección: " + club.getDireccion()));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));

        return panel;
    }

    private <T> JPanel seccionCRUD(String titulo, List<T> items, String tipo, Function<T, String> getNombre) {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.setBorder(BorderFactory.createTitledBorder(titulo));

        for (T item : items) {
            JPanel fila = new JPanel(new FlowLayout(FlowLayout.LEFT));
            fila.add(new JLabel(getNombre.apply(item)));

            JButton editar = new JButton("Editar");
            editar.addActionListener(e -> editarElemento(tipo, item));
            fila.add(editar);

            JButton eliminar = new JButton("Eliminar");
            eliminar.addActionListener(e -> eliminarElemento(tipo, item));
            fila.add(eliminar);

            panel.add(fila);
        }

        JButton agregar = new JButton("+ Agregar " + tipo);
        agregar.setAlignmentX(Component.LEFT_ALIGNMENT);
        agregar.addActionListener(e -> agregarElemento(tipo));
        
        
        panel.add(agregar);

        return panel;
    }

    private <T> void editarElemento(String tipo, T item) {
        if (item instanceof Equipo) {
            Equipo equipo = (Equipo) item;

            JPanel panel = new JPanel();
            panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

            JTextField nombreField = new JTextField(equipo.getNombre());
            JComboBox<String> categoriaCombo = new JComboBox<>();
            JTextField coloresField = new JTextField(equipo.getColores());

            // 1. categorias
            try (Connection conn = Conexion.getInstance().getConnection()) {
                EquipoDAO equipoDAO = new EquipoDAO();
                List<String> categorias = equipoDAO.obtenerCategorias(conn);
                for (String categoria : categorias) {
                    categoriaCombo.addItem(categoria);
                }
                categoriaCombo.setSelectedItem(equipo.getCategoria());
            } catch (SQLException e) {
                e.printStackTrace();
            }

            // 2. Estadios
            JComboBox<Estadio> estadioCombo = new JComboBox<>();
            try (Connection conn = Conexion.getInstance().getConnection()) {
                EstadioDAO estadioDAO = new EstadioDAO();
                Club club = equipo.getClub();
                if (club != null) {
                    List<Estadio> estadios = estadioDAO.obtenerEstadiosPorClub(conn, club.getId());
                    for (Estadio est : estadios) {
                        estadioCombo.addItem(est);
                    }
                    if (equipo.getEstadio() != null) {
                        estadioCombo.setSelectedItem(equipo.getEstadio());
                    }
                } else {
                    System.err.println("Club es null, no se pueden cargar estadios.");
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }

            // 3. DT
            JComboBox<Persona> tecnicoCombo = new JComboBox<>();
            try (Connection conn = Conexion.getInstance().getConnection()) {
                PersonaDAO personaDAO = new PersonaDAO();

                // Получаем id клуба через команду
                Club club = equipo.getClub();
                if (club != null) {
                    List<DirectorTecnico> tecnicosDelClub = personaDAO.obtenerTecnicosPorClub(conn, club.getId());

                    for (DirectorTecnico dt : tecnicosDelClub) {
                        tecnicoCombo.addItem(dt);
                    }

                    // Устанавливаем текущего DT, если есть
                    if (equipo.getDirectorTecnico() != null) {
                        tecnicoCombo.setSelectedItem(equipo.getDirectorTecnico());
                    }
                } else {
                    System.err.println("Equipo no tiene club asignado.");
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }


            // 4. components
            panel.add(new JLabel("Nombre del equipo:"));
            panel.add(nombreField);
            panel.add(new JLabel("Categoría del equipo:"));
            panel.add(categoriaCombo);
            panel.add(new JLabel("Colores del equipo:"));
            panel.add(coloresField);
            panel.add(new JLabel("Estadio local:"));
            panel.add(estadioCombo);
            panel.add(new JLabel("Técnico:"));
            panel.add(tecnicoCombo);

            int option = JOptionPane.showConfirmDialog(this, panel, "Editar Equipo", JOptionPane.OK_CANCEL_OPTION);

            if (option == JOptionPane.OK_OPTION) {
                equipo.setNombre(nombreField.getText());
                equipo.setCategoria((String) categoriaCombo.getSelectedItem());
                equipo.setColores(coloresField.getText());

                Estadio estadioSeleccionado = (Estadio) estadioCombo.getSelectedItem();
                DirectorTecnico tecnicoSeleccionado = (DirectorTecnico) tecnicoCombo.getSelectedItem();
                int idEstadio = (estadioSeleccionado != null) ? estadioSeleccionado.getIdEstadio() : 0;

                
                byte[] nuevoEscudo = null;

                try (Connection conn = Conexion.getInstance().getConnection()) {
                    new EquipoDAO().actualizarEquipo(conn, equipo, nuevoEscudo, idEstadio);
                    JOptionPane.showMessageDialog(this, "Equipo actualizado correctamente.");
                } catch (SQLException e) {
                    e.printStackTrace();
                    JOptionPane.showMessageDialog(this, "Error al actualizar el equipo.");
                }
            }

        } else if (item instanceof Estadio) {
            Estadio estadio = (Estadio) item;

            JPanel panel = new JPanel();
            panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

            JTextField nombreField = new JTextField(estadio.getNombre());
            JTextField direccionField = new JTextField(estadio.getDireccion());
            JTextField capacidadField = new JTextField(String.valueOf(estadio.getCapacidad()));

            panel.add(new JLabel("Nombre del estadio:"));
            panel.add(nombreField);
            panel.add(new JLabel("Dirección del estadio:"));
            panel.add(direccionField);
            panel.add(new JLabel("Capacidad del estadio:"));
            panel.add(capacidadField);

            int option = JOptionPane.showConfirmDialog(this, panel, "Editar Estadio", JOptionPane.OK_CANCEL_OPTION);

            if (option == JOptionPane.OK_OPTION) {
                estadio.setNombre(nombreField.getText());
                estadio.setDireccion(direccionField.getText());
                try {
                    estadio.setCapacidad(Integer.parseInt(capacidadField.getText()));
                } catch (NumberFormatException e) {
                    JOptionPane.showMessageDialog(this, "Capacidad debe ser un número válido.");
                    return;
                }

                try (Connection conn = Conexion.getInstance().getConnection()) {
                    new EstadioDAO().actualizarEstadio(conn, estadio);
                    JOptionPane.showMessageDialog(this, "Estadio actualizado correctamente.");
                } catch (SQLException e) {
                    e.printStackTrace();
                    JOptionPane.showMessageDialog(this, "Error al actualizar el estadio.");
                }
            }
        } else if (item instanceof Disciplina) {
            Disciplina disciplina = (Disciplina) item;

            // Создаем панель для редактирования дисциплины
            JPanel panel = new JPanel();
            panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

            // Поле для редактирования названия дисциплины
            JTextField nombreField = new JTextField(disciplina.getNombreDisciplina());

            // Добавляем компоненты в панель
            panel.add(new JLabel("Nombre de la disciplina:"));
            panel.add(nombreField);

            // Показываем диалоговое окно, где пользователь может изменить название дисциплины
            int option = JOptionPane.showConfirmDialog(this, panel, "Editar Disciplina", JOptionPane.OK_CANCEL_OPTION);

            // Если пользователь нажал OK
            if (option == JOptionPane.OK_OPTION) {
                // Обновляем название дисциплины
                disciplina.setNombreDisciplina(nombreField.getText());

                try (Connection conn = Conexion.getInstance().getConnection()) {
                    // Теперь вызываем метод, передавая id и новое название дисциплины
                    new DisciplinaDAO().actualizarDisciplina(conn, disciplina.getIdDisciplina(), disciplina.getNombreDisciplina());
                    JOptionPane.showMessageDialog(this, "Disciplina actualizada correctamente.");
                } catch (SQLException e) {
                    e.printStackTrace();
                    JOptionPane.showMessageDialog(this, "Error al actualizar la disciplina.");
                }
            }
        }
    }




    private <T> void eliminarElemento(String tipo, T item) {
        int respuesta = JOptionPane.showConfirmDialog(
            this,
            "¿Eliminar " + tipo + "?\n" + item.toString(),
            "Confirmar",
            JOptionPane.YES_NO_OPTION
        );

        if (respuesta == JOptionPane.YES_OPTION) {
            try (Connection conn = Conexion.getInstance().getConnection()) {
                switch (tipo.toLowerCase()) {
                    case "equipo" -> {
                        if (item instanceof Equipo equipo) {
                            new EquipoDAO().eliminarEquipoPorId(conn, equipo.getIdEquipo());
                            JOptionPane.showMessageDialog(this, "Equipo eliminado correctamente.");
                            
                        }
                    }
                    case "estadio" -> {
                        if (item instanceof Estadio estadio) {
                            new EstadioDAO().eliminarEstadioPorId(conn, estadio.getIdEstadio());
                            JOptionPane.showMessageDialog(this, "Estadio eliminado correctamente.");
                            
                        }
                    }
                    
                    case "disciplina" -> {
                        if (item instanceof Disciplina disciplina) {
                            new DisciplinaDAO().eliminarDisciplina(conn, disciplina.getIdDisciplina());
                            JOptionPane.showMessageDialog(this, "Disciplina eliminado correctamente.");
                    // Aquí se puede usar el mismo método para otros tipos
                        }
                    }
                            
                            default -> {
                        JOptionPane.showMessageDialog(this, "Tipo no soportado: " + tipo);
                    }
                }
            } catch (SQLException e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(this, "Error al eliminar " + tipo + ": " + e.getMessage());
            }
        }
    }


    private void agregarElemento(String tipo) {
        if (tipo.equalsIgnoreCase("equipo")) {
            
            String nombreEquipo = JOptionPane.showInputDialog(this, "Ingrese el nombre del equipo:");
            if (nombreEquipo == null || nombreEquipo.trim().isEmpty()) {
                JOptionPane.showMessageDialog(this, "El nombre del equipo es obligatorio.");
                return;
            }

            String categoria = JOptionPane.showInputDialog(this, "Ingrese la categoría del equipo (ej: Primera, Juvenil, etc):");
            if (categoria == null || categoria.trim().isEmpty()) {
                JOptionPane.showMessageDialog(this, "La categoría del equipo es obligatoria.");
                return;
            }

            String colores = JOptionPane.showInputDialog(this, "Ingrese los colores del equipo:");
            if (colores == null || colores.trim().isEmpty()) {
                JOptionPane.showMessageDialog(this, "Los colores del equipo son obligatorios.");
                return;
            }

            String nombreEscudo = JOptionPane.showInputDialog("Ingrese el nombre del archivo del escudo del equipo (ej: escudo1.png):");
    	    String rutaEscudo = "./torneo_futbol/escudos/" + nombreEscudo;  
    	    System.out.println("Ruta del escudo: " + rutaEscudo); 

    	    File archivoEscudo = new File(rutaEscudo);
    	    if (!archivoEscudo.exists()) {
    	        JOptionPane.showMessageDialog(null, "El archivo del escudo no existe en la ruta especificada.");
    	        return;
    	    }
    	    
    	    System.out.println("Tamaño del archivo del escudo: " + archivoEscudo.length() + " bytes");

    	    byte[] escudoBytes = new byte[(int) archivoEscudo.length()];
    	    try (FileInputStream fis = new FileInputStream(archivoEscudo)) {
    	        fis.read(escudoBytes);
    	    } catch (IOException e) {
    	        e.printStackTrace();
    	        JOptionPane.showMessageDialog(null, "Error al leer el archivo del escudo.");
    	        return;
    	    }

    	    if (escudoBytes == null || escudoBytes.length == 0) {
    	        JOptionPane.showMessageDialog(null, "El archivo del escudo está vacío o no se pudo leer correctamente.");
    	        return;
    	    }
            // Получаем клуб администратора
            ClubDAO clubDAO = new ClubDAO();
            int idClub = clubDAO.obtenerIdClubPorAdmin(admin.getEmail());  // Используем admin.getEmail()

            if (idClub == -1) {
                JOptionPane.showMessageDialog(this, "Primero debe registrar un club.");
                return;
            }

            EstadioDAO estadioDAO = new EstadioDAO();
            List<Estadio> estadios = estadioDAO.obtenerEstadiosPorClub(null, idClub);

            if (estadios.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Primero debe registrar al menos un estadio.");
                return;
            }

            String[] nombresEstadios = new String[estadios.size()];
            for (int i = 0; i < estadios.size(); i++) {
                nombresEstadios[i] = estadios.get(i).getNombre();
            }

            String seleccion = (String) JOptionPane.showInputDialog(
                this,
                "Seleccione el estadio local:",
                "Estadio",
                JOptionPane.QUESTION_MESSAGE,
                null,
                nombresEstadios,
                nombresEstadios[0]
            );

            if (seleccion == null) {
                JOptionPane.showMessageDialog(this, "No se seleccionó ningún estadio.");
                return;
            }

            Estadio estadioSeleccionado = null;
            for (Estadio estadio : estadios) {
                if (estadio.getNombre().equals(seleccion)) {
                    estadioSeleccionado = estadio;
                    break;
                }
            }

            // Вставка данных в базу данных
            try (Connection conn = Conexion.getInstance().getConnection()) {
                String sql = "INSERT INTO equipo (nombre, categoria, colores, escudo, id_club) VALUES (?, ?, ?, ?, ?)";
                try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                    stmt.setString(1, nombreEquipo);
                    stmt.setString(2, categoria);
                    stmt.setString(3, colores);
                    stmt.setBytes(4, escudoBytes);
                    stmt.setInt(5, idClub);

                    stmt.executeUpdate();

                    // Показать данные и изображение
                    ImageIcon escudoIcon = new ImageIcon(escudoBytes);
                    Image escudoImage = escudoIcon.getImage();
                    if (escudoImage == null) {
                        throw new Exception("Не удалось загрузить изображение из байтов.");
                    }
                    Image escudoEscalado = escudoImage.getScaledInstance(150, 150, Image.SCALE_SMOOTH);
                    ImageIcon escudoEscaladoIcon = new ImageIcon(escudoEscalado);

                    JOptionPane.showMessageDialog(
                        this,
                        "Equipo registrado exitosamente:\n" +
                        "Nombre: " + nombreEquipo + "\n" +
                        "Categoría: " + categoria + "\n" +
                        "Colores: " + colores + "\n" +
                        "Estadio Local: " + estadioSeleccionado.getNombre(),
                        "Escudo del equipo",
                        JOptionPane.INFORMATION_MESSAGE,
                        escudoEscaladoIcon
                    );
                }
            } catch (SQLException e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(this, "Error al registrar el equipo.");
            } catch (Exception e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(this, "Error al cargar o procesar la imagen del escudo.");
            }
        } else if (tipo.equalsIgnoreCase("estadio")) {
        	
        	ClubDAO clubDAO = new ClubDAO();
            String emailAdmin = admin.getEmail();  

            int idClub = clubDAO.obtenerIdClubPorAdmin(emailAdmin);  
        	
        	String nombre = JOptionPane.showInputDialog("Ingrese el nombre del estadio:");
            String direccion = JOptionPane.showInputDialog("Ingrese la dirección del estadio:");
            String capacidadStr = JOptionPane.showInputDialog("Ingrese la capacidad del estadio:");

            if (nombre == null || direccion == null || capacidadStr == null ||
                nombre.isBlank() || direccion.isBlank() || capacidadStr.isBlank()) {
                JOptionPane.showMessageDialog(null, "Todos los campos son obligatorios.");
                return;
            }

            int capacidad = 0;
            try {
                capacidad = Integer.parseInt(capacidadStr);
                if (capacidad <= 0) {
                    JOptionPane.showMessageDialog(null, "La capacidad debe ser mayor que cero.");
                    return;
                }
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(null, "La capacidad debe ser un número válido.");
                return;
            }

            // Вставка данных в базу данных
            try (Connection conn = Conexion.getInstance().getConnection()) {
                String sql = "INSERT INTO estadio (nombre, direccion, capacidad, id_club) VALUES (?, ?, ?, ?)";
                try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                    stmt.setString(1, nombre);
                    stmt.setString(2, direccion);
                    stmt.setInt(3, capacidad);
                    stmt.setInt(4, idClub);

                    stmt.executeUpdate();
                    JOptionPane.showMessageDialog(null, "Estadio registrado exitosamente.");
                }
            } catch (SQLException e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(null, "Error al registrar el estadio.");
            	}  
            
           } else if (tipo.equalsIgnoreCase("disciplina")) {
                String nombreDisciplina = JOptionPane.showInputDialog(this, "Ingrese el nombre de la disciplina:");
                
                if (nombreDisciplina == null || nombreDisciplina.trim().isEmpty()) {
                    JOptionPane.showMessageDialog(this, "El nombre de la disciplina es obligatorio.");
                    return;
                }

                
                ClubDAO clubDAO = new ClubDAO();
                int idClub = clubDAO.obtenerIdClubPorAdmin(admin.getEmail());

                
             

                    
                    try (Connection conn = Conexion.getInstance().getConnection()) {
                        String sql = "INSERT INTO disciplina (nombre_disciplina, id_club) VALUES (?, ?)";
                        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                            stmt.setString(1, nombreDisciplina);
                            stmt.setInt(2, idClub);
                            stmt.executeUpdate();

                            JOptionPane.showMessageDialog(this, "Disciplina registrada exitosamente.");
                        }
                    } catch (SQLException e) {
                        e.printStackTrace();
                        JOptionPane.showMessageDialog(this, "Error al registrar la disciplina.");
                    }
            
           }
        
    }
        

    private JPanel seccionEscudo() {
        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout());
        panel.setPreferredSize(new Dimension(200, 250));

        JLabel label = new JLabel("Escudo del Club", SwingConstants.CENTER);
        label.setFont(new Font("Arial", Font.BOLD, 14));
        panel.add(label, BorderLayout.NORTH);

        if (!club.getEquipos().isEmpty()) {
            Equipo equipo = club.getEquipos().get(0);  
            try (Connection conn = Conexion.getInstance().getConnection()) {
                EquipoDAO equipoDAO = new EquipoDAO();
                ImageIcon escudoIcon = equipoDAO.obtenerEscudo(conn, equipo.getIdEquipo());  

                if (escudoIcon != null) {
                    
                    Image imagen = escudoIcon.getImage().getScaledInstance(150, 150, Image.SCALE_SMOOTH);
                    JLabel labelEscudo = new JLabel(new ImageIcon(imagen));
                    labelEscudo.setHorizontalAlignment(SwingConstants.CENTER);
                    panel.add(labelEscudo, BorderLayout.CENTER);
                } else {
                    JLabel sinEscudo = new JLabel("Sin escudo", SwingConstants.CENTER);
                    panel.add(sinEscudo, BorderLayout.CENTER);
                }
            } catch (SQLException e) {
                e.printStackTrace();
                JLabel errorLabel = new JLabel("Error al cargar el escudo", SwingConstants.CENTER);
                panel.add(errorLabel, BorderLayout.CENTER);
            }
        } else {
            JLabel sinEquipos = new JLabel("No hay equipos", SwingConstants.CENTER);
            panel.add(sinEquipos, BorderLayout.CENTER);
        }

        return panel;
    }
    
    
  
}
