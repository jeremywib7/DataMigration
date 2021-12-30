package sample.controllers;

import java.sql.Connection;
import java.sql.DriverManager;

public class DatabaseConnection {
    Connection con = null;
    public static String serverName = "localhost";
    public static String databaseName = "TraddingFoxPro";


    public static Connection getSQLConnection() {
        try {
            Class.forName("net.sourceforge.jtds.jdbc.Driver");
            Connection con = DriverManager.getConnection("jdbc:jtds:sqlserver://"+serverName+";databaseName="+databaseName+";integratedSecurity=true");
            return con;
        } catch (Exception e) {
            e.printStackTrace();
            e.getCause();
        }
        return null;
    }

}
//   Class.forName("com.mysql.cj.jdbc.Driver");
//           con = DriverManager.getConnection("jdbc:mysql://localhost/tradding?useTimezone=true&serverTimezone=UTC", "root" ,"1234");
//           System.out.println("Connected");


//        String databaseUser = "sa";
//        String databasePassword = "1234";

//        Server Tradding Auth
//        String url = "jdbc:sqlserver://192.168.0.1;databaseName=Trading;integratedSecurity=false;";
//        Local
