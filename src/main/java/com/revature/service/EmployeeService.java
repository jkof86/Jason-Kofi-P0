package com.revature.service;

import java.io.IOException;
import java.io.OutputStream;
import java.sql.SQLException;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import org.codehaus.jackson.JsonGenerationException;
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

    private EmployeeRepository repo = new EmployeeRepository(exchange);
    private ObjectMapper mapper = new ObjectMapper();
    private Employee emp = new Employee();

    public void register(String jsonObj) throws JsonGenerationException, JsonMappingException, IOException {

        // we pass the exchange down to the repository level

        // print to console for testing
        System.out.println("Exchange passed down to repository level...");

        // first we convert the jsonObj into an Employee object

        try {
            emp = mapper.readValue(jsonObj, Employee.class);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        // print to console for testing
        System.out.println("Creating Employee object: \n\n" + emp.toString() + "\n\nand sending to repository...");

        // then we send the new object to the repository level

        try {
            // if the registration was successful we send a response
            if (repo.register(emp)) {
                mapper = new ObjectMapper();
                String response = "The DB was updated successfully!\n" + mapper.writeValueAsString(emp);
                System.out.println(response);

                exchange.sendResponseHeaders(200, response.getBytes().length);
                OutputStream os = exchange.getResponseBody();
                os.write(response.getBytes());
                os.close();
            }
            // if the statement fails we send a 400 response and a message
            else {

                String response = "There was an issue updating the DB...";
                try {
                    exchange.sendResponseHeaders(400, response.getBytes().length);
                } catch (IOException e1) {
                    // TODO Auto-generated catch block
                    e1.printStackTrace();
                }
                OutputStream os = exchange.getResponseBody();
                try {
                    os.write(response.getBytes());
                    os.close();
                } catch (IOException e1) {
                    // TODO Auto-generated catch block
                    e1.printStackTrace();
                }
            }

        } catch (SQLException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }

    }

}
