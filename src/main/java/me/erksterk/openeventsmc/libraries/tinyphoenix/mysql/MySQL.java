package me.erksterk.openeventsmc.libraries.tinyphoenix.mysql;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import java.sql.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

public class MySQL extends Thread{
    //TODO: cleanup
    String ip;
    String port;
    String user;
    String password;
    String database;
    String connectionName;
    String url;
    public Connection connection;
    private ThreadPoolExecutor execute = (ThreadPoolExecutor) Executors.newFixedThreadPool(1);

    private ThreadPoolExecutor queue = (ThreadPoolExecutor) Executors.newFixedThreadPool(1);

    public MySQL(String ip, String port, String user, String password, String database,String connectionName){

        this.ip=ip;
        this.port=port;
        this.user=user;
        this.password=password;
        this.database=database;
        this.connectionName=connectionName;
        final String url = "jdbc:mysql://" +ip+ ":" + port+ "/" +database+"?autoReconnect=true&amp;autoReconnectForPools=true&amp;interactiveClient=true&amp;characterEncoding=UTF-8";
        this.url=url;
        try { //We use a try catch to avoid errors, hopefully we don't get any.
            Class.forName("com.mysql.jdbc.Driver"); //this accesses Driver in jdbc.
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            System.err.println("jdbc driver unavailable!");
            return;
        }
        try {
            this.connection = DriverManager.getConnection(url,user,password);
        } catch (SQLException e) {

        }
    }
    //This is the simplest for of query in the API, it does NOT recieve any information BUT it is quicker and more stable. so use this for SETTING information!
    public void executeQuery(String query){
        execute.execute(new ThreadUpdateQuery(connection,query));

    }
    //This executeQuery requires a refrence so we can identify which query is which. this should be an UNIQUE id for that specific query in this MYSQL instance.
    //It also returns information from the execution so this is not suitable for setting information.

    //This makes a table in the sql database all tables contains a row ID which is auto incremented and set as the primary key!
    public void makeTable(String tablename, String engine){
        String sql = "CREATE TABLE IF NOT EXISTS " + "`"+database+"`"+".`"+tablename+"` (\n" +
                "    `ID` INT NOT NULL AUTO_INCREMENT,\n" +
                "    PRIMARY KEY (ID)\n"+
                ") ENGINE="+engine+";";
        //Executes the query generated above
        executeQuery(sql);
    }


    //tablename and columnname are selfexplainatory, rowdata refers to the data specified for the row example: INT NOT NULL
    public void addColumn(String tablename, String columnname, String rowdata){
        String sql = "IF NOT EXISTS( SELECT NULL\n"+
        "FROM INFORMATION_SCHEMA.COLUMNS\n"+
        "WHERE table_name = '"+tablename+"'\n"+
        "AND table_schema = '"+database+"'\n"+
        "AND column_name = '"+columnname+"')  THEN\n"+
        "ALTER TABLE `"+tablename+"` ADD `"+columnname+"` "+rowdata+";\n"+
        "END IF;;";
        //Executes the query generated above
        executeQuery(sql);
    }
    public List<List<String>> getEntireResult(String tablename, String[] conditions){
        String sql = "SELECT * FROM "+tablename;
        for(int i=0;i<conditions.length;i++){
            if(i==0){
                sql=sql+" WHERE "+conditions[i];
            }
            if(conditions.length>1 && i!=0){
                sql=sql+" AND "+conditions[i];
            }
        }
        sql=sql+";";
        final List<List<String>> arr = new ArrayList<>();

        final String finalSql = sql;
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    PreparedStatement stmt = connection.prepareStatement(finalSql);
                    ResultSet result = null;
                    result = stmt.executeQuery();
                    while(result.next()){
                        List<String> rowinfo = new ArrayList<>();
                        ResultSetMetaData rsmd = result.getMetaData();
                        int x =rsmd.getColumnCount();
                        for(int i=1;i<=x;i++){
                            String column=rsmd.getColumnName(i);
                            String rs = result.getString(column);
                            rowinfo.add(column+"|"+rs);
                        }
                        arr.add(rowinfo);
                    }
                } catch (SQLException e) {
                    Bukkit.getConsoleSender().sendMessage(ChatColor.RED+"Phoenix had issues with: \n"+ChatColor.AQUA+finalSql);
                }
            }
        }).start();
        for(int i=0;i<20;i++){
            try {
                if(arr.isEmpty()){
                    sleep(10);
                }else{
                    return arr;
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        return arr;
    }
   public Object getObject(final String columnOfInterest, String tablename, String[] conditions) throws InterruptedException{
        String sql = "SELECT "+columnOfInterest+" FROM "+tablename;
        for(int i=0;i<conditions.length;i++){
            if(i==0){
                sql=sql+" WHERE "+conditions[i];
            }
            if(conditions.length>1 && i!=0){
                sql=sql+" AND "+conditions[i];
            }
        }
        sql=sql+";";
        final String finalSql = sql;
        final Object[] obj = {null};
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    PreparedStatement stmt = connection.prepareStatement(finalSql);
                    ResultSet result = null;
                    result = stmt.executeQuery();
                    if (result.next()) {
                        obj[0] = result.getObject(columnOfInterest);
                    }
                } catch (SQLException e) {
                    Bukkit.getConsoleSender().sendMessage(ChatColor.RED+"Phoenix had issues with: \n"+ChatColor.AQUA+finalSql);
                }
            }
        }).start();
       int in=0;
       while(obj[0]==null) {
           sleep(5);
           in++;
           if(in>=100){
               return obj[0];
           }
       }
        return obj[0];
    }
    public List<Object> getList(final String column, String tablename, String[] conditions) throws InterruptedException {

        String sql = "SELECT * FROM "+tablename;
        for(int i=0;i<conditions.length;i++){
            if(i==0){
                sql=sql+" WHERE "+conditions[i];
            }
            if(conditions.length>1 && i!=0){
                sql=sql+" AND "+conditions[i];
            }
            if(conditions.length==1 || conditions.length==i){
                sql=sql+";";
            }
        }
        final String finalSql = sql;
        final List<Object> list = new ArrayList<>();
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    PreparedStatement stmt = connection.prepareStatement(finalSql);
                    ResultSet result = null;
                    result = stmt.executeQuery();
                        while(result.next()){
                        list.add(result.getObject(column));
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }).start();
        int in=0;
        while(list.isEmpty()) {
            sleep(5);
            in++;
            if(in>=100){
                return list;
            }
        }

        return list;
    }



    //This will make a new row in the database
    public void insertData(String table, HashMap<String,String> data){
        String sql = "INSERT INTO "+table+" (";
        for(int i=0;i<data.keySet().size();i++){
            List<String> keyset = new ArrayList<>(data.keySet());
            String key = keyset.get(i);
            if(i==0){
                sql=sql+keyset.get(i);
            }
            if(keyset.size()>1 && i!=0){
                sql=sql+", "+keyset.get(i);
            }
        }
        sql=sql+")";


        sql=sql+" VALUES (";
        for(int i=0;i<data.keySet().size();i++){
            List<String> keyset = new ArrayList<>(data.keySet());
            String key = keyset.get(i);
            if(i==0){
                sql=sql+data.get(key);
            }
            if(keyset.size()>1 && i!=0){
                sql=sql+", "+data.get(key);
            }
        }
        sql=sql+");";
        executeQuery(sql);
    }

    //this changes information in an allready defined row, if you want to define a row please use insertData

    public void updateData(String table,String[] conditions,HashMap<String,String> data){
        String sql = "UPDATE "+table+" SET ";
        for(int i=0;i<data.keySet().size();i++){
            List<String> keyset = new ArrayList<>(data.keySet());
            String key = keyset.get(i);
            if(i==0){
                sql=sql+key+" = "+data.get(key);
            }
            if(keyset.size()>1 && i!=0){
                sql=sql+", "+key+" = "+data.get(key);
            }
        }
        for(int i=0;i<conditions.length;i++){
            if(i==0){
                sql=sql+" WHERE "+conditions[i];
            }
            if(conditions.length>1 && i!=0){
                sql=sql+" AND "+conditions[i];
            }
            if(conditions.length==1 || conditions.length==i){
                sql=sql+";";
            }
        }
        executeQuery(sql);
    }



    //this removes a row
    public void removeData(String table,String[] conditions){
        String sql = "DELETE FROM "+table;
        for(int i=0;i<conditions.length;i++){
            if(i==0){
                sql=sql+" WHERE "+conditions[i];
            }
            if(conditions.length>1 && i!=0){
                sql=sql+" AND "+conditions[i];
            }
            if(conditions.length==1 || conditions.length==i){
                sql=sql+";";
            }
        }
        executeQuery(sql);
    }



    //This updates the data if it exists or inserts if it doesnt. essentially writes or overwrites
    public void setData(final String table, final String[] conditions, final HashMap<String,String> data){
        queue.submit(
        new Runnable() {
            @Override
            public void run() {
                try {
                    Object o = getObject("ID", table, conditions);
                    if (o != null) {
                        updateData(table, conditions, data);
                    } else {
                        insertData(table, data);
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

            }
        });
    }


    //continiously runs the thread to check if the connection is still open incase of a mysql shutdown
    public void run(){
      while(!isInterrupted()){
          try {
              sleep(10000);
              if(connection.isClosed()){
                  connection=DriverManager.getConnection(url,user,password);
              }


          } catch (InterruptedException e) {
          } catch (SQLException e) {
          }
          {

          }
      }
  }
}

