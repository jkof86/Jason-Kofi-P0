package com.revature.service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.Reader;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;

import com.revature.model.Employee;
import com.revature.model.Ticket;
import com.revature.repository.EmployeeRepository;
import com.revature.repository.TicketRepository;

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



    public static String convertRequest(HttpExchange exchange) throws IOException{

        InputStream is = exchange.getRequestBody();

            // then we convert the request to an object and pass it into the repository
            try (Reader reader = new BufferedReader(
                    new InputStreamReader(is, Charset.forName(StandardCharsets.UTF_8.name())))) {

                int c = 0;

                // need to convert input stream to string
                // we'll be using StringBuilder
                StringBuilder sb = new StringBuilder();

                // read method from BufferedReader will return -1 when there's no more letters
                // left
                // we keep reading each letter until theres not more left
                while ((c = reader.read()) != -1) {
                    sb.append((char) c);
                }
                return sb.toString();
            }
        }


    public static void sendResponse(HttpExchange exchange, String response) throws IOException {
    // finally we return the response
    OutputStream os = exchange.getResponseBody();
    // we send the response header first
    exchange.sendResponseHeaders((200), response.getBytes().length);
    // then we write to the response body and close the connection
    os.write(response.getBytes());
    os.close();

    }


    public void getEmployees(HttpExchange exchange) throws IOException, SQLException {

        // we create a repo object
        EmployeeRepository repo = new EmployeeRepository();
        // then we create a list of employees to hold the data pulled from the db
        List<Employee> currentList = repo.getAllEmployees();
        // we convert the list of employees to a json obj
        // then we store it in a string to send in the response body
        ObjectMapper mapper = new ObjectMapper();
        String response = mapper.writeValueAsString(currentList);

        // now we retrieve the request body from the exchange
        exchange.getRequestBody();

        // then we create a response
        exchange.sendResponseHeaders(200, response.getBytes().length);
        // we have to save the string into a class that httpServer can handle
        OutputStream os = exchange.getResponseBody();

        System.out.println("These objects were retrieved from the DB..." + response);
        os.write(response.getBytes()); // writing inside the response body
        os.close(); // make sure to close the output stream

    }

    public void register(String jsonObj) throws JsonGenerationException, JsonMappingException, IOException {


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

    public void login(HttpExchange exchange) throws IOException, SQLException {

        // we create a repo object
        EmployeeRepository repo = new EmployeeRepository();

        String sb = convertRequest(exchange);

            // now we pass the string down to the repository for processing

            ObjectMapper mapper = new ObjectMapper();

            Employee e = repo.verify(sb.toString());

            if (e != null) {
                // System.out.println("LOGIN SUCCESSFUL");
                String response;

                // we print a manager or employee welcome based on the role id
                // role 1 employee, role 2 manager
                if (e.getRole() == 2) {
                    response = mapper
                            .writeValueAsString("LOGIN SUCCESSFUL - MANAGER ID:  " + e.getEmpId() + " " + e.getFname() +
                                    " " + e.getLname() + ": " + e.getEmail());
                } else {
                    response = mapper.writeValueAsString(
                            "LOGIN SUCCESSFUL - EMPLOYEE ID:  " + e.getEmpId() + " " + e.getFname() +
                                    " " + e.getLname() + ": " + e.getEmail());
                }

                System.out.println();

                // finally we return the response
                sendResponse(exchange, response);
            } else {
                System.out.println("ERROR: INCORRECT EMAIL OR PW...");
                String response = mapper.writeValueAsString("ERROR: INCORRECT EMAIL OR PW...");

                // finally we return the response
                sendResponse(exchange, response);
            }
        }


        public void managerLogin(HttpExchange exchange) throws IOException, SQLException {
            // we create a repo object
            EmployeeRepository repo = new EmployeeRepository();
            
            String sb = convertRequest(exchange);
            
            
                // now we pass the string down to the repository for processing
    
                ObjectMapper mapper = new ObjectMapper();
                Employee e = repo.verify(sb.toString());
    
                if (e != null) {
                    // System.out.println("LOGIN SUCCESSFUL");
                    // we add a new response for the manager
                    // which is a current list of all pending tickets
                    String welcomeManager = "LOGIN SUCCESSFUL - MANAGER ID:  " + e.getEmpId() + " " + e.getFname() +
                            " " + e.getLname() + ": " + e.getEmail();
    
                    // we print a manager or employee welcome based on the role id
                    // role 1 employee, role 2 manager
                    if (e.getRole() == 2) {
                        TicketRepository trepo = new TicketRepository();
                        List<Ticket> listofTickets = new ArrayList<Ticket>();
    
                        // if we detect a manager we immediately gather a list of pending tickets
                        // and we pass that list back to the client in the response body
                        listofTickets = trepo.getAllTickets();
    
                        String pendingTickets = listofTickets.toString();
    
                        // we send a response with the managers welcome and a list of pending tickets
                        String response = mapper.writeValueAsString(welcomeManager + pendingTickets);
    
                        // finally we return the response
                        sendResponse(exchange, response);
    
                    }
    
                 else {
                    System.out.println("ERROR: INCORRECT EMAIL OR PW...");
                    String response = mapper.writeValueAsString("ERROR: INCORRECT EMAIL OR PW...");
    
                    // finally we return the response
                    sendResponse(exchange, response);
                }
            }
        }
    }
