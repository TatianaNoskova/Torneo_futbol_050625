package torneo_futbol.db;

import java.sql.*;

public class Conexion {
	
	private static Conexion instance;
    private Connection connection;

    private final String URL = "jdbc:mysql://localhost:3306/torneo_futbol?useSSL=false&serverTimezone=UTC";
    private final String USER = "root";
    private final String PASSWORD = "";

    private Conexion() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            this.connection = DriverManager.getConnection(URL, USER, PASSWORD);
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
            System.out.println("No se conectó");
        }
    }

    public Connection getConnection() {
        return connection;
    }

    public static Conexion getInstance() {
        if (instance == null) {
            instance = new Conexion();
        } else {
            try {
                if (instance.getConnection().isClosed()) {
                    instance = new Conexion();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return instance;
    }

}
