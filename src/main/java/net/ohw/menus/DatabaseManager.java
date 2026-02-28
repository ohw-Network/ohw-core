package net.ohw.menus;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseManager {
    private Connection connection;
    private final String host, database, username, password;
    private final int port;

    public DatabaseManager(String host, int port, String database, String username, String password) {
        this.host = host;
        this.port = port;
        this.database = database;
        this.username = username;
        this.password = password;
    }

    public void connect() throws SQLException {
        if (connection != null && !connection.isClosed()) return;
        
        // 1.8.8 建議手動加載驅動
        try { Class.forName("com.mysql.jdbc.Driver"); } catch (Exception e) {}

        connection = DriverManager.getConnection(
            "jdbc:mysql://" + host + ":" + port + "/" + database + "?useSSL=false&characterEncoding=utf8", 
            username, password);
    }

    public Connection getConnection() { return connection; }
}