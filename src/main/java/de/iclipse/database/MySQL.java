package de.iclipse.database;

import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.sql.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public class MySQL {

    private JavaPlugin plugin;
    private Logger logger;

    private String HOST;
    private String DATABASE;
    private String USER;
    private String PASSWORD;
    private String prefix;

    private Connection conn;

    public MySQL(JavaPlugin plugin) {
        this.plugin = plugin;
        this.logger = plugin.getLogger();
        connect();
    }

    public File getMySQLFile() {
        return new File("plugins/" + plugin.getDescription().getName(), "mysql.yml");
    }

    public FileConfiguration getMySQLFileConfiguration() {
        return YamlConfiguration.loadConfiguration(getMySQLFile());
    }

    public void setStandardMySQL() {
        FileConfiguration cfg = getMySQLFileConfiguration();
        cfg.options().copyDefaults(true);
        cfg.addDefault("host", "localhost");
        cfg.addDefault("database", "BansystemLegend");
        cfg.addDefault("user", "mysql");
        cfg.addDefault("password", "dshchangE762");

        cfg.addDefault("prefix", "&5" + plugin.getDescription().getName() + " &3MySQL &8&7");
        try {
            cfg.save(getMySQLFile());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void readMySQL() {
        FileConfiguration cfg = getMySQLFileConfiguration();
        HOST = cfg.getString("host");
        DATABASE = cfg.getString("database");
        USER = cfg.getString("user");
        PASSWORD = cfg.getString("password");
        prefix = ChatColor.translateAlternateColorCodes('&', cfg.getString("prefix")) + " ";
    }


    public void connect() {
        setStandardMySQL();
        readMySQL();
        try {
            conn = DriverManager.getConnection("jdbc:mysql://" + HOST + ":3306/" + DATABASE + "?autoReconnect=false", USER, PASSWORD);
            logger.log(Level.INFO, prefix + "Connected with database");
        } catch (SQLException e) {
            logger.log(Level.INFO, prefix + "No connection! Error: " + e.getMessage());
            plugin.getServer().shutdown();
        }
    }

    public void close() {
        try {
            if (!conn.isClosed()) {
                if (conn != null) {
                    conn.close();
                    logger.log(Level.INFO, prefix + "disconnected successfully!");
                }
            }

        } catch (SQLException e) {
            logger.log(Level.INFO, prefix + "No connection! Error: " + e.getMessage());
        }
    }

    public void update(String querry) {
        Statement st;
        try {
            checkConnection();
            st = conn.createStatement();
            st.executeUpdate(querry);
            st.close();
        } catch (SQLException e) {
            connect();
            logger.log(Level.WARNING, e.getMessage());
        }
    }

    public ResultSet querry(String querry) {
        ResultSet rs = null;

        Statement st;
        try {
            checkConnection();
            st = conn.createStatement();
            rs = st.executeQuery(querry);
        } catch (SQLException e) {
            connect();
            System.err.println(e);
        }
        return rs;
    }

    public void checkConnection() throws SQLException {
        if (conn == null || conn.isClosed()) {
            connect();
        }
    }
}
