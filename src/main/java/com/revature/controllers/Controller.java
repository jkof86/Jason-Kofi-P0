package com.revature.controllers;

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

import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;

import com.revature.model.Employee;
import com.revature.model.Ticket;
import com.revature.repository.EmployeeRepository;
import com.revature.repository.TicketRepository;
import com.revature.service.EmployeeService;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

//This class holds the methods to handle http requests
public class Controller implements HttpHandler {

    // Handle method will execute once I receive the right request
    @Override
    public void handle(HttpExchange exchange) throws IOException {

        String httpVerb = exchange.getRequestMethod();

        switch (httpVerb) {
            case "GET":
                // these if statements allows us to access different methods
                // depending on the context path of the request
                if (exchange.getHttpContext().getPath().equals("/testUrl")) {
                    try {
                        System.out.println(exchange.getHttpContext().getPath());
                        getRequest(exchange);
                    } catch (SQLException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                }

                // these if statements allows us to access different methods
                // depending on the context path of the request
                else if (exchange.getHttpContext().getPath().equals("/testUrl/login")) {
                    try {
                        System.out.println(exchange.getHttpContext().getPath());
                        login(exchange);
                    } catch (SQLException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                    break;
                } else if (exchange.getHttpContext().getPath().equals("/testUrl/tickets")) {
                    try {
                        System.out.println(exchange.getHttpContext().getPath());

                        // the manager logs in with credentials
                        managerLogin(exchange);
                        // then proceeds to getting a pending ticket list

                    } catch (SQLException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                    break;
                }

            case "POST":
                if (exchange.getHttpContext().getPath().equals("/testUrl")) {
                    registration(exchange);

                } else if (exchange.getHttpContext().getPath().equals("/testUrl/submit")) {
                    try {
                        System.out.println(exchange.getHttpContext().getPath());
                        submitTicket(exchange);
                    } catch (SQLException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                    break;
                }

            case "PUT":
                if (exchange.getHttpContext().getPath().equals("/testUrl/tickets")) {
                    try {
                        System.out.println(exchange.getHttpContext().getPath());

                        // here we allow the manager to approve or deny a pending ticket
                        processTicket(exchange);
                    } catch (SQLException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                    break;
                }

            default:
                otherRequest(exchange);
                break;
        }
    }

    public void processTicket(HttpExchange exchange) throws IOException, SQLException {
        ObjectMapper mapper = new ObjectMapper();
        TicketRepository repo = new TicketRepository();
        Ticket t = new Ticket();

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

            // now we pass the string down to the repository for processing
            System.out.println("Passing request to repository layer");
            t = repo.process(sb.toString());

            //If we have a valid ticket processing we continue as normal
            if (t != null) {

                // then we locate the updated ticket
                t = repo.getTicket(sb.toString());

                // we print a confirmation message
                String response = mapper.writeValueAsString("TICKET PROCESSED SUCCESSFUL - " + t.toString());
                System.out.println();

                // finally we return the response
                OutputStream os = exchange.getResponseBody();
                // we send the response header first
                exchange.sendResponseHeaders((200), response.getBytes().length);
                // then we write to the response body and close the connection
                os.write(response.getBytes());
                os.close();
            } else {
                System.out.println("ERROR: INVALID TICKET PROCESSING REQUEST");
                String response = mapper.writeValueAsString("ERROR: INVALID TICKET PROCESSING REQUEST");

                // finally we return the response
                OutputStream os = exchange.getResponseBody();
                // we send the response header first
                exchange.sendResponseHeaders((400), response.getBytes().length);
                // then we write to the response body and close the connection
                os.write(response.getBytes());
                os.close();
            }
        }
    }

    public void submitTicket(HttpExchange exchange)
            throws JsonParseException, JsonMappingException, IOException, SQLException {
        ObjectMapper mapper = new ObjectMapper();
        TicketRepository repo = new TicketRepository();
        Ticket t = new Ticket();

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

            // now we pass the string down to the repository for processing
            System.out.println("Passing request to repository layer");
            System.out.println(sb.toString());
            t = repo.storeTicket(sb.toString());

            if (t != null) {
                // we print a confirmation message
                // ObjectMapper mapper = new ObjectMapper();
                String response = mapper.writeValueAsString("TICKET SUBMISSION SUCCESSFUL - " + t.toString());
                System.out.println();

                // finally we return the response
                OutputStream os = exchange.getResponseBody();
                // we send the response header first
                exchange.sendResponseHeaders((200), response.getBytes().length);
                // then we write to the response body and close the connection
                os.write(response.getBytes());
                os.close();
            } else {
                // ObjectMapper mapper = new ObjectMapper();
                System.out.println("ERROR: INVALID TICKET FORMAT");
                String response = mapper.writeValueAsString("ERROR: INVALID TICKET FORMAT");

                // finally we return the response
                OutputStream os = exchange.getResponseBody();
                // we send the response header first
                exchange.sendResponseHeaders((400), response.getBytes().length);
                // then we write to the response body and close the connection
                os.write(response.getBytes());
                os.close();
            }
        }
    }

    public void managerLogin(HttpExchange exchange) throws IOException, SQLException {
        // we create a repo object
        EmployeeRepository repo = new EmployeeRepository();

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

            // now we pass the string down to the repository for processing

            ObjectMapper mapper = new ObjectMapper();

            Employee e = repo.verify(sb.toString());

            if (e != null) {
                // System.out.println("LOGIN SUCCESSFUL");
                String response;
                // we add a new response for the manager
                // which is a current list of all pending tickets
                String welcomeManager = "LOGIN SUCCESSFUL - MANAGER ID:  " + e.getEmpId() + " " + e.getFname() +
                        " " + e.getLname() + ": " + e.getEmail();
                String welcomeEmployee = "LOGIN SUCCESSFUL - EMPLOYEE ID:  " + e.getEmpId() + " " + e.getFname() +
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
                    response = mapper.writeValueAsString(welcomeManager + pendingTickets);
                } 
                //if the login is an employee we send back a list of their past tickets
                else {
                    TicketRepository trepo = new TicketRepository();
                    List<Ticket> listofTickets = new ArrayList<Ticket>();
                    listofTickets = trepo.getPastTickets(e);
                    System.out.println("List of past tickets: " + listofTickets.toString());

                    String pastTickets = listofTickets.toString();

                    response = mapper.writeValueAsString(welcomeEmployee + pastTickets);
                }

                System.out.println();

                // finally we return the response
                OutputStream os = exchange.getResponseBody();
                // we send the response header first
                exchange.sendResponseHeaders((200), response.getBytes().length);
                // then we write to the response body and close the connection
                os.write(response.getBytes());
                os.close();
            } else {
                System.out.println("ERROR: INCORRECT EMAIL OR PW...");
                String response = mapper.writeValueAsString("ERROR: INCORRECT EMAIL OR PW...");

                // finally we return the response
                OutputStream os = exchange.getResponseBody();
                // we send the response header first
                exchange.sendResponseHeaders((400), response.getBytes().length);
                // then we write to the response body and close the connection
                os.write(response.getBytes());
                os.close();
            }
        }
    }

    public void login(HttpExchange exchange) throws IOException, SQLException {

        // we create a repo object
        EmployeeRepository repo = new EmployeeRepository();

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
                OutputStream os = exchange.getResponseBody();
                // we send the response header first
                exchange.sendResponseHeaders((200), response.getBytes().length);
                // then we write to the response body and close the connection
                os.write(response.getBytes());
                os.close();
            } else {
                System.out.println("ERROR: INCORRECT EMAIL OR PW...");
                String response = mapper.writeValueAsString("ERROR: INCORRECT EMAIL OR PW...");

                // finally we return the response
                OutputStream os = exchange.getResponseBody();
                // we send the response header first
                exchange.sendResponseHeaders((400), response.getBytes().length);
                // then we write to the response body and close the connection
                os.write(response.getBytes());
                os.close();
            }
        }
    }

    public void getRequest(HttpExchange exchange) throws IOException, SQLException {

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

    public void registration(HttpExchange exchange) throws IOException {

        // we'll send a response once we reach the repository level
        // therefore we need to pass down the exchange
        EmployeeService es = new EmployeeService(exchange);

        // not a string
        // has a bunch of bytes
        // need to convert input stream to string
        // we'll be using StringBuilder
        InputStream is = exchange.getRequestBody();

        // a mutable version of a string, more efficient
        StringBuilder sb = new StringBuilder();
        // converts our binary into letters
        // try_resource block will automatically close the resource within the
        // parentheses

        try (Reader reader = new BufferedReader(
                new InputStreamReader(is, Charset.forName(StandardCharsets.UTF_8.name())))) {

            int c = 0;

            // read method from BufferedReader will return -1 when there's no more letters
            // left
            // we keep reading each letter until theres not more left
            while ((c = reader.read()) != -1) {
                sb.append((char) c);
            }
        }

        // for now, let's send the new string to the service level for registration

        // print to console for testing
        System.out.println("Exchange passed down to service level...");
        // for now, let's send the new string to the service level for registration
        es.register(sb.toString());

    }

    public void putRequest(HttpExchange exchange) throws IOException {

        String getResponse = "You selected the \"PUT\" response";

        // first we retrieve the request body from the exchange
        exchange.getRequestBody();

        // then we create a response
        exchange.sendResponseHeaders(200, getResponse.getBytes().length);
        // we have to save the string into a class that httpServer can handle
        OutputStream os = exchange.getResponseBody();

        os.write(getResponse.getBytes()); // writing inside the response body
        os.close(); // make sure to close the output stream

    }

    public void otherRequest(HttpExchange exchange) throws IOException {

        String getResponse = "Sorry, we're only accepting \"GET\", \"POST\", and \"PUT\" requests at the moment.";

        // first we retrieve the request body from the exchange
        exchange.getRequestBody();

        // then we create a response
        exchange.sendResponseHeaders(400, getResponse.getBytes().length);
        // we have to save the string into a class that httpServer can handle
        OutputStream os = exchange.getResponseBody();

        os.write(getResponse.getBytes()); // writing inside the response body
        os.close(); // make sure to close the output stream

    }

}
