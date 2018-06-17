package me.braggs.BraggBot;


import me.braggs.BraggBot.Configuration.ConfigData;
import me.braggs.BraggBot.Configuration.ConfigManager;

import java.sql.*;

public class DatabaseManager {
    private Connection connection;

    public enum Singleton {
        INSTANCE;

        DatabaseManager value;

        Singleton(){
            value = new DatabaseManager();
        }

        public DatabaseManager getInstance(){
            return value;
        }
    }

    private DatabaseManager(){
        setupConnection();
    }

    private void setupConnection(){
        ConfigData config = ConfigManager.Singleton.INSTANCE.getInstance().getConfig();
        DiscordLogger.logMessage("(re)connecting to database");
        String jdbcUri = "jdbc:mysql://localhost:3306/" + config.databaseName;
        Connection con;

        System.out.println(jdbcUri + " " + config.databaseUser + " " + config.databasePassword);

        try{
            con = DriverManager.getConnection(jdbcUri, config.databaseUser, config.databasePassword);
        }
        catch (SQLException e){
            System.out.println("CRITICAL ERROR, DATABASE COULD NOT BE CONNECTED");
            e.printStackTrace();
            System.exit(0);
            con = null;
        }
        connection = con;
    }

    public Connection getConnection() {
        try{
            if(!connection.isValid(0)){
                setupConnection();
            }
        }
        catch (SQLException e){
            e.printStackTrace();
        }
        return connection;
    }
}
