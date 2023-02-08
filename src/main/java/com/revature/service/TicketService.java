package com.revature.service;

import java.io.IOException;
import java.io.OutputStream;
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

public class TicketService {

    private HttpExchange exchange;

    public TicketService(HttpExchange exchange) {
        this.exchange = exchange;
    }

    private EmployeeRepository repo = new EmployeeRepository(exchange);
    private ObjectMapper mapper = new ObjectMapper();
    private Employee emp = new Employee();

    public void employeeTickets(HttpExchange exchange) throws IOException {

        String response;
        String sb = EmployeeService.convertRequest(exchange);

        ObjectMapper mapper = new ObjectMapper();
        TicketRepository trepo = new TicketRepository();
        List<Ticket> listofTickets = new ArrayList<Ticket>();
        try {
            listofTickets = trepo.getPastTickets(sb.toString());
        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        System.out.println("List of past tickets: " + listofTickets.toString());

        String pastTickets = listofTickets.toString();

        response = mapper.writeValueAsString(pastTickets);
        EmployeeService.sendResponse(exchange, response);
    }

    public void submitTicket(HttpExchange exchange)
            throws JsonParseException, JsonMappingException, IOException, SQLException {
        ObjectMapper mapper = new ObjectMapper();
        TicketRepository repo = new TicketRepository();
        Ticket t = new Ticket();

        String sb = EmployeeService.convertRequest(exchange);

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
            EmployeeService.sendResponse(exchange, response);
        } else {
            // ObjectMapper mapper = new ObjectMapper();
            System.out.println("ERROR: INVALID TICKET FORMAT");
            String response = mapper.writeValueAsString("ERROR: INVALID TICKET FORMAT");

            // finally we return the response
            EmployeeService.sendResponse(exchange, response);
        }
    }

    public void processTicket(HttpExchange exchange) throws IOException, SQLException {
        ObjectMapper mapper = new ObjectMapper();
        TicketRepository repo = new TicketRepository();
        Ticket t = new Ticket();

        String sb = EmployeeService.convertRequest(exchange);

        // now we pass the string down to the repository for processing
        System.out.println("Passing request to repository layer");
        t = repo.process(sb.toString());

        // If we have a valid ticket processing we continue as normal
        if (t != null ) {

            // then we locate the updated ticket
            t = repo.getTicket(sb.toString());

            // we print a confirmation message
            String response = mapper.writeValueAsString("TICKET PROCESSED SUCCESSFUL - " + t.toString());
            System.out.println();

            // finally we return the response
            EmployeeService.sendResponse(exchange, response);
        } else {
            System.out.println("ERROR: INVALID TICKET PROCESSING REQUEST");
            String response = mapper.writeValueAsString("ERROR: INVALID TICKET PROCESSING REQUEST");

            // finally we return the response
            EmployeeService.sendResponse(exchange, response);
        }
    }
}
