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

import com.revature.model.Employee;
import com.revature.model.Ticket;
import com.revature.utils.ConnectionUtil;
import com.sun.net.httpserver.HttpExchange;

public class TicketRepository {

    private HttpExchange exchange;

    // we need to be able to pull a ticket from the DB by its ID
    public Ticket getTicket(String jsonObj) throws SQLException {
        // first we need to convert the jsonObj to a Ticket obj
        // we create a new mapper and Ticket obj

        ObjectMapper mapper = new ObjectMapper();
        Ticket t = new Ticket();

        // then we convert the jsonObj to a Ticket using the mapper class
        try {
            t = mapper.readValue(jsonObj, Ticket.class);
        } catch (JsonParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (JsonMappingException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        System.out.println("\nTicket object created.");
        System.out.println(t.toString());

        // this is the sql query to select the given ticket
        String sql = "select * from request where requestid = (?)";

        // we execute the above query
        try (Connection con = ConnectionUtil.getConnection()) {
            PreparedStatement ps = con.prepareStatement(sql);

            ps.setInt(1, t.getId());

            ResultSet rs = ps.executeQuery();

            Ticket t2 = new Ticket();

            // then we copy the resulting tickets data to a Ticket obj
            while (rs.next()) {
                // for testing
                System.out.println("\nGathering data...");

                t2.setId(rs.getInt("requestid"));
                t2.setDesc(rs.getString("description"));
                t2.setStatus(rs.getString("status"));
                t2.setEmpId(rs.getInt("empid"));
                t2.setAmount(rs.getDouble("amount"));
            }

            // finally we return the Ticket obj
            System.out.println(t2.toString());
            return t2;
        }

    }

    public List<Ticket> getAllTickets() throws SQLException {

        // this is the sql statement to gather all pending tickets
        String sql = "select * from request where status = 'pending'";
        List<Ticket> listofTickets = new ArrayList<Ticket>();

        try (Connection con = ConnectionUtil.getConnection()) {
            Statement s = con.createStatement();
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

    // mvp 8 - finally we need employees to get a list of prior tickets
    public List<Ticket> getPastTickets(String jsonObj) throws SQLException {
        Ticket t = new Ticket();
        ObjectMapper mapper = new ObjectMapper();
        try {
            t = mapper.readValue(jsonObj, Ticket.class);
        } catch (JsonParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (JsonMappingException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        // this is the sql statement to gather all pending tickets
        String sql = "select * from request r join employee e on r.empid = e.empid where r.empid = (?) and status = (?)";

        List<Ticket> listofTickets = new ArrayList<Ticket>();

        try (Connection con = ConnectionUtil.getConnection()) {
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setInt(1, t.getEmpId());
            ps.setString(2, t.getStatus());

            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                // we need to create a new ticket for each iteration
                Ticket t2 = new Ticket();
                t2.setId(rs.getInt(1));
                t2.setDesc(rs.getString(2));
                t2.setStatus(rs.getString(3));
                t2.setEmpId(rs.getInt(4));
                t2.setAmount(rs.getDouble(5));

                // then we add each new ticket to the list
                listofTickets.add(t2);

                // for testing
                System.out.println("\nGathering data...");
            }

        }

        return listofTickets;

    }

    public Ticket process(String jsonObj) throws SQLException {

        // first we need to convert the jsonObj to a Ticket obj
        // first we create a new mapper and Ticket obj
        ObjectMapper mapper = new ObjectMapper();
        Ticket t1 = new Ticket();
        Ticket t2 = new Ticket();
        TicketRepository trepo = new TicketRepository();

        // System.out.println("test");
        // then we convert the jsonObj to a Ticket using the mapper class
        try {
            //this is the ticket pulled from the DB
            t1 = trepo.getTicket(jsonObj);
            
            //this is the ticket request from the client
            t2 = mapper.readValue(jsonObj, Ticket.class);
        } catch (JsonParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (JsonMappingException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        System.out.println("\nTicket object created.");
        System.out.println(t1.toString());

        // if the ticket pulled from the DB is not pending, we can't access it, therefore we return null;
        if (!t1.getStatus().equalsIgnoreCase("pending")) {
            return null;
        } else {
            // if the current ticket is pending...
            // if the manager has sent an approval request then we set the status of the
            // object to "approved"
            if (t1.getStatus().equals("approved")) {
                t1.setStatus("approved");
            }
            // if the manager denies, we set the status to "denied"
            else if (t1.getStatus().equals("denied")) {
                t1.setStatus("denied");
            }
        }

        // this sql query will require a PreparedStatement and an executeUpdate() for
        // confirmation
        String sql = "UPDATE request set status = (?) where requestid = (?)";

        try (Connection con = ConnectionUtil.getConnection()) {

            // here we copy the status of the Ticket obj over to the DB
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setString(1, t1.getStatus());
            ps.setInt(2, t1.getId());
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
                System.out.println("TICKET PROCESSED SUCCESSFULLY");
                System.out.println(t1.toString());
                return t1;
            } else {
                return null;
            }
        }
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
