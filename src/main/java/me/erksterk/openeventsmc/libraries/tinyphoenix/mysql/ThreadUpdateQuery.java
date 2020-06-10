package me.erksterk.openeventsmc.libraries.tinyphoenix.mysql;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class ThreadUpdateQuery implements Runnable{
    private Connection connection;
    private String query;
    private String conName;
    private String refrence;
    private ResultSet results;
    public ThreadUpdateQuery(Connection connection, String query){
        this.connection=connection;
        this.query=query;
    }

    public void run(){
        try {
            PreparedStatement stmt = connection.prepareStatement(query);
                stmt.executeUpdate();

        } catch (SQLException e) {
            Bukkit.getConsoleSender().sendMessage(ChatColor.RED+"Phoenix had issues with: \n"+ChatColor.AQUA+query);
        }
    }
}
