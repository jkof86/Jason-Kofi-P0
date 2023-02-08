package com.revature.controllers;

import java.io.IOException;
import java.io.OutputStream;
import java.sql.SQLException;
import com.revature.service.EmployeeService;
import com.revature.service.TicketService;
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
                        EmployeeService es = new EmployeeService(exchange);
                        //this returns a full list of employees
                        es.getEmployees(exchange);
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
                        EmployeeService es = new EmployeeService(exchange);
                        //this is the normal login
                        es.login(exchange);
                    } catch (SQLException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                    break;
                } else if (exchange.getHttpContext().getPath().equals("/testUrl/tickets")) {
                    try {
                        System.out.println(exchange.getHttpContext().getPath());
                        EmployeeService es = new EmployeeService(exchange);

                        // the manager logs in with credentials
                        es.managerLogin(exchange);
                        // then proceeds to getting a pending ticket list

                    } catch (SQLException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                    break;
                } else if (exchange.getHttpContext().getPath().equals("/testUrl/pastTickets")) {
                    System.out.println(exchange.getHttpContext().getPath());
                    TicketService ts = new TicketService(exchange);

                    //this is where an employee can view their past tickets
                    ts.employeeTickets(exchange);
                    // then proceeds to getting a pending ticket list
                    break;
                }

            case "POST":
                if (exchange.getHttpContext().getPath().equals("/testUrl")) {
                    EmployeeService es = new EmployeeService(exchange);
                    //this will register a new user
                    //this was the first method implemented, so it acts a little differently
                    //here we need to convert the exchange to a string from the controller 
                    //before passing it to the service layer
                    String sb = EmployeeService.convertRequest(exchange);
                    es.register(sb);

                } else if (exchange.getHttpContext().getPath().equals("/testUrl/submit")) {
                    try {
                        System.out.println(exchange.getHttpContext().getPath());
                        TicketService ts = new TicketService(exchange);
                        //this is where we submit a new ticket
                        ts.submitTicket(exchange);
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
                        TicketService ts = new TicketService(exchange);

                        // here we allow the manager to approve or deny a pending ticket
                       ts.processTicket(exchange);
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
    















    








    // public void registration(HttpExchange exchange) throws IOException {

    //     // we'll send a response once we reach the repository level
    //     // therefore we need to pass down the exchange
    //     EmployeeService es = new EmployeeService(exchange);

    //     String sb = convertRequest(exchange);

    //     // for now, let's send the new string to the service level for registration

    //     // print to console for testing
    //     System.out.println("Exchange passed down to service level...");
    //     // for now, let's send the new string to the service level for registration
    //     es.register(sb.toString());

    // }



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