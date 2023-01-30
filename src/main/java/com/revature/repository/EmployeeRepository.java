package com.revature.repository;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStream;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import org.codehaus.jackson.map.ObjectMapper;

import com.revature.model.Employee;

//here we will save the Employee object into a database
//first we'll start by saving a file passed down from a jsonObj locally

public class EmployeeRepository{

    private HttpExchange exchange;

    public EmployeeRepository(HttpExchange exchange){
        this.exchange = exchange;
    }

    //here we will save each employee object that's created, locally
    public void log(String s){

        try {
            FileWriter fw = new FileWriter("./log.txt");
            fw.append(s.toString());
            fw.close();

            //if this block of code executes, then we update the 
            //log and return an http response to the end user
            exchange.getRequestBody();
            
            //we create the response in a new variable so we can verify the correct response length 
            //in the second response header parameter
            String response1 = "The local log has been updated with the the user: " + s.toString();
            exchange.sendResponseHeaders(200, response1.getBytes().length);
            
            //we create the stream and write it to the response body
            OutputStream os = exchange.getResponseBody();
            
            //we made sure to use response1 so the .length is correct
            os.write(response1.getBytes());
            os.close();
            
        } catch (IOException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }

    }

    public void register(Employee e){
        //first we save the

    }

        
    
}
