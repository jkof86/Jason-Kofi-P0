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
import java.util.List;

import org.codehaus.jackson.map.ObjectMapper;

import com.revature.model.Employee;
import com.revature.repository.EmployeeRepository;
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
                }

            case "POST":
                postRequest(exchange);
                break;

            case "PUT":
                putRequest(exchange);
                break;

            default:
                otherRequest(exchange);
                break;
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

            if (repo.verify(sb.toString())) {
                System.out.println("LOGIN SUCCESSFUL");
                String response = mapper.writeValueAsString("LOGIN SUCCESSFUL");

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

    public void postRequest(HttpExchange exchange) throws IOException {

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
