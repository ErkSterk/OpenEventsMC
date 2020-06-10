package me.erksterk.openeventsmc.libraries.tinyphoenix.mysql;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

public class Connections {
    private static HashMap<String,MySQL> connections = new HashMap<>();
    //creates a new connection, if a plugin allready opened the conenction it will use the same one to save resources
    public static boolean establishNewConnection(String connectionName,String ip, String port, String database, String user, String password) throws SQLException {
        if(connections.containsKey(connectionName)){
            Bukkit.getConsoleSender().sendMessage(connectionName+" Already Exists, If the connection is dead it will be replaced by the new attempt!");
            if(!connections.get(connectionName).connection.isClosed()){
                Bukkit.getConsoleSender().sendMessage("The Connection was alive assuming the new connection should not have occurred, aborting!");
                return true;
            }
        }

        try{
            MySQL connection = new MySQL(ip,port,user,password,database,connectionName);
            if(!connection.connection.isClosed()){
                connections.put(connectionName,connection);
                connection.start();
                return true;
            }else{
                Bukkit.getConsoleSender().sendMessage(ChatColor.RED+"The connection for database:"+connectionName+" Failed!\n"+
                        ChatColor.RED+"A likely cause for this is either: wrong login credentials or the server is down!\n"+
                        ChatColor.RED+"This is not a PhoenixLib Error!");
                return false;
            }
        }catch(NullPointerException ex){
            Bukkit.getConsoleSender().sendMessage(ChatColor.RED+"The connection for database:"+connectionName+" Failed!\n"+
                    ChatColor.RED+"A likely cause for this is either: wrong login credentials or the server is down!\n"+
                    ChatColor.RED+"This is not a PhoenixLib Error!");
            return false;
        }

    }
    //gets a connection from name
    public static MySQL getConnection(String connectionName){
        if(connections.containsKey(connectionName)){
            return connections.get(connectionName);

        }else{
            return null;
        }
    }
    //gets the name of all open connections
    public static List<String> getAllConnectionNames(){
        Set<String> set = connections.keySet();
        List<String> list = new ArrayList<>(set);
        return list;
    }
    //kills a connection
    public static boolean killConnection(String connectionName) {
        MySQL con = getConnection(connectionName);
        if(con==null){
            return false;
        }else{
            connections.remove(connectionName);
            try {
                con.interrupt();
                con.connection.close();
            } catch (SQLException e) {}
            return true;
        }
    }
}
