package com.revature.utils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConnectionUtil {

    // we want only one connection to the database the entire time
    private static Connection con;

    public ConnectionUtil() {
        con = null;
    }

    // method will give us a connection to db
    // OR it will give the existing connection
    public static Connection getConnection() {

        // determine if we already have a connection
        // if so give the current connection
        try {
            if (con != null && !con.isClosed()) {
                return con;
            }
        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        String url, user, pass;

        // not smart, it will be exposed once we push to remote repo
        // url = "localhost";
        // user = "johnSmith52";
        // pass = "123qwe";

        url = System.getenv("url");
        user = System.getenv("user");
        pass = System.getenv("pass");

        // System.out.println(url);
        // System.out.println(user);
        // System.out.println(pass);

        try {
            con = DriverManager.getConnection(url, user, pass);
            // we print the status of the connection
            System.out.println("--CONNECTION TO DB ESTABLISHED SUCCESSFULLY");
        } catch (SQLException e) {
            // TODO Auto-generated catch block
            System.out.println("--CONNECTION TO DB UNSUCCESSFUL");
            System.out.println("--YOU PROBABLY ENTERED THE WRONG PW OR URL--");
            e.printStackTrace();
        }

        return con;

    }
}
