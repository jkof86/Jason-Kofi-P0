package com.revature.repository;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;

import com.revature.model.Ticket;
import com.revature.utils.ConnectionUtil;
import com.sun.net.httpserver.HttpExchange;

public class TicketRepository {

    private HttpExchange exchange;
    private Ticket readValue;

    // we need an empty constructor
    public TicketRepository() {

    }

    // mvp 6 - we ned to grab all the pending tickets for managers to approve/deny
    public List<Ticket> getAllTickets() throws SQLException {

        //this is the sql statement to gather all pending tickets
        String sql = "select * from request where status = 'pending'";
        List<Ticket> listofTickets = new ArrayList();

        try (Connection con = ConnectionUtil.getConnection()) {
            Statement s = con.createStatement(0, 0);
            ResultSet rs = s.executeQuery(sql);

            while (rs.next()) {
                Ticket t = new Ticket();
                t.setId(rs.getInt(1));
                t.setDesc(rs.getString(2));
                t.setStatus(rs.getString(3));
                t.setEmpId(rs.getInt(4));
                t.setAmount(rs.getDouble(5));

                listofTickets.add(t);
                // for testing
                System.out.println("\nGathering data...");
            }

        }

        return listofTickets;

    }

    // we create a method to check the validity of a request and store it in DB
    public Ticket storeTicket(String jsonObj)
            throws SQLException, JsonParseException, JsonMappingException, IOException {
        // first we need to convert the jsonObj to a Ticket obj
        // first we create a new mapper and Ticket obj
        ObjectMapper mapper = new ObjectMapper();
        Ticket t = new Ticket();

        // then we convert the jsonObj to a Ticket using the mapper class
        t = mapper.readValue(jsonObj, Ticket.class);
        System.out.println("\nTicket object created.");
        System.out.println(t.toString());

        // then we validate by checking for an amount and description

        if ((t.getAmount() <= 0) || t.getDesc() == null) {
            return null;
        }

        // if the ticket is valid, we store it in the DB with pending statusd by default
        else {

            String sql = "insert into request (amount, description, empid, status) values (?,?,?,?)";

            try (Connection con = ConnectionUtil.getConnection()) {

                PreparedStatement ps = con.prepareStatement(sql);
                ps.setDouble(1, t.getAmount());
                ps.setString(2, t.getDesc());
                ps.setInt(3, t.getEmpId());
                ps.setString(4, t.getStatus());
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
