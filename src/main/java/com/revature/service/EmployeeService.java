package com.revature.service;

import java.io.IOException;
import java.sql.SQLException;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;

import com.revature.model.Employee;
import com.revature.repository.EmployeeRepository;

//here we convert the JSON object into an employee object
//then we send the converted information to the repository level for further processing
public class EmployeeService {

    private HttpExchange exchange;

    public EmployeeService(HttpExchange exchange) {
        this.exchange = exchange;
    }

    public void register(String jsonObj) {

        // we pass the exchange down to the repository level
        EmployeeRepository repo = new EmployeeRepository(exchange);
        // print to console for testing
        System.out.println("Exchange passed down to repository level...");

        // first we convert the jsonObj into an Employee object
        try {
            ObjectMapper mapper = new ObjectMapper();
            Employee e = mapper.readValue(jsonObj, Employee.class);
            // print to console for testing
            System.out.println("Creating Employee object: " + e.toString() + "\n\nand sending to repository...");

            // then we send the new object to the repository level
            // there is where it's filtered and registered if it doesn't already exist

            repo.register(e);

        } catch (JsonParseException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        } catch (JsonMappingException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        } catch (IOException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        } catch (SQLException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }

    }

    public void login(String jsonObj) {

    }

}
