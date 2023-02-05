package com.revature.repository;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;

import com.revature.model.Ticket;
import com.revature.utils.ConnectionUtil;
import com.sun.net.httpserver.HttpExchange;

public class TicketRepository {

    private HttpExchange exchange;
    private Ticket readValue;

    //we need an empty constructor
    public TicketRepository() {

    }
    //we create a method to check the validity of a request and store it in DB
    public Ticket storeTicket(String jsonObj) throws JsonParseException, JsonMappingException, IOException, SQLException {
        ObjectMapper mapper = new ObjectMapper();

        //first we need to convert the jsonObj to a Ticket obj
        Ticket t = mapper.readValue(jsonObj, Ticket.class);
        System.out.println("Ticket object created.");
        System.out.println(t.toString());

        //then we validate by checking for an amount and description

        if ( (t.getAmount() <= 0) || t.getDesc() == null ) { return null; }

        //if the ticket is valid, we store it in the DB  with pending statusd by default
        else {

            String sql = "insert into request (amount, description, empid) values (?,?,?)";

            try(Connection con = ConnectionUtil.getConnection()){

                PreparedStatement ps = con.prepareStatement(sql); 
                ps.setDouble(1, t.getAmount());
                ps.setString(2, t.getDesc());
                ps.setInt(3, t.getEmpId());
                // if the statement executes successfully, we return the object and a
                // confirmation in the response body
                // executeUpdate() returns an int for the amount of rows affected, 0 for nothing
                // returned

                int result = ps.executeUpdate();
                // testing result return value
                System.out.println(result);

                // if the operation is successful we return the ticket object
                // else we return null
                if (result > 0) {
                    System.out.println("TICKET SUBMITTED SUCCESSFULLY");
                    System.out.println(t.toString());
                    return t;
                } else {
                    return null;
                }
        
                }
            }
        }

    }
