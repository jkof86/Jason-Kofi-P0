package com.revature.repository;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;

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
    public void log(String s) throws IOException{

        //the true parameter allows us to append to the end of the log file
        //we use BufferedWriter and PrintWriter to write to an existing file
        try ( FileWriter fw = new FileWriter("./log.txt", true);
        BufferedWriter bw = new BufferedWriter(fw);
        PrintWriter pw = new PrintWriter(bw); ) 
        
        { 
            //we add a line break between entries
            pw.println();
            pw.println(s.toString()); 
        }

        catch (IOException e) {
            e.printStackTrace();
        }
    
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
    }

    public void register(Employee e){
        //first we save the

    }
    
}
