package com.revature;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

import javax.xml.ws.spi.http.HttpExchange;

import com.revature.controllers.Controller;
import com.revature.model.Employee;
import com.revature.repository.EmployeeRepository;
import com.revature.utils.ConnectionUtil;
import com.sun.net.httpserver.HttpServer;

public final class App {
    private App() {
    }

        public static void main(String[] args) throws IOException {
        // System.out.println("Hello World!");

        // Employee emp = new Employee();
        // ObjectMapper mapper = new ObjectMapper();
        // try {
        //     System.out.println(mapper.writeValueAsString(emp));
        // } catch (IOException e) {
        //     // TODO Auto-generated catch block
        //     e.printStackTrace();
        // }

        // File f = new File("test.txt");
        // if (f.exists()){System.out.println("File already exists");}

        // else{

        // try {
        //     f.createNewFile();
        //     f.exists();
        // } catch (IOException e) {
        //     // TODO Auto-generated catch block
        //     e.printStackTrace();
        // }

        // try {
        //     FileWriter fw = new FileWriter(f);
        //     fw.write("Writing to the second file");
        //     fw.close();
        // } catch (IOException e) {
        //     // TODO Auto-generated catch block
        //     e.printStackTrace();
        // }

        int port1 = 8000;
        HttpServer server = HttpServer.create(new InetSocketAddress(port1), 0);
        server.setExecutor(null);

        server.createContext("/testUrl", new Controller());

        server.start(); //starts backend    
        System.out.println("Local server running on port: " + port1);
        
        }

    }

