package com.revature.service;

import java.io.IOException;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;

import com.revature.model.Employee;
import com.revature.repository.EmployeeRepository;

//here we convert the JSON object into an employee object
//then we send the converted information to the repository level for further processing
public class EmployeeService{

    private HttpExchange exchange;

    public EmployeeService(HttpExchange exchange){
        this.exchange = exchange;
    }

    public void registerJson(String jsonObj) throws JsonMappingException, IOException{
        ObjectMapper mapper = new ObjectMapper();
        EmployeeRepository repo = new EmployeeRepository(exchange);
        
        
        //then we send the new object to the repository level
        //there is where it's filtered and added if it doesn't already exist
        repo.log(jsonObj);
       
    }

    public void login(String jsonObj){

    }



    
}
