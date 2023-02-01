package com.revature.repository;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import com.revature.model.*;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;

import com.revature.model.Employee;
import com.revature.utils.ConnectionUtil;

//here we will save the Employee object into a database
//first we'll start by saving a file passed down from a jsonObj locally

public class EmployeeRepository {

    private HttpExchange exchange;

    public EmployeeRepository(HttpExchange exchange) {
        this.exchange = exchange;
    }

    // we need a constructor with no parameter
    public EmployeeRepository() {

    }

    // here we will save each employee object that's created, locally
    public void log(String s) throws IOException {

        // the true parameter allows us to append to the end of the log file
        // we use BufferedWriter and PrintWriter to write to an existing file
        try (FileWriter fw = new FileWriter("./log.txt", true);
                BufferedWriter bw = new BufferedWriter(fw);
                PrintWriter pw = new PrintWriter(bw);)

        {
            // we add a line break between entries
            pw.println();
            pw.println(s.toString());
        }

        catch (IOException e) {
            e.printStackTrace();
        }

        // if this block of code executes, then we update the
        // log and return an http response to the end user
        exchange.getRequestBody();

        // we create the response in a new variable so we can verify the correct
        // response length
        // in the second response header parameter
        String response1 = "The local log has been updated with the the user: " + s.toString();

        exchange.sendResponseHeaders(200, response1.getBytes().length);

        // we create the stream and write it to the response body
        OutputStream os = exchange.getResponseBody();

        // we made sure to use response1 so the .length is correct
        os.write(response1.getBytes());
        os.close();
    }

    public void register(Employee e) throws SQLException {

        // this is the String we use to execute sql statements
        String sql = "insert into employee (fname, lname, address, email, pw) values (?,?,?,?,?)";

        try (Connection con = ConnectionUtil.getConnection()) {
            PreparedStatement ps = con.prepareStatement(sql);

            // we replace '?' into actual values from the obj we receive
            // this uses 1 based indexing!!
            ps.setString(1, e.getFname());
            ps.setString(2, e.getLname());
            ps.setString(3, e.getAddress());
            ps.setString(4, e.getEmail());
            ps.setString(5, e.getPassword());

            // execute() does not expect to return anything from the statement
            // executeQuery does expect something to result after executing the statement

            //if the statement executes successfully, we return the object and a confirmation in the response body
            if (!ps.execute()) {
                ObjectMapper mapper = new ObjectMapper();
                String response;
                try {
                    //we create a string with the confirmation message and add a new json obj at the end
                    response = "The DB was updated successfully!\n" + mapper.writeValueAsString(e);
                    exchange.sendResponseHeaders(200, response.getBytes().length);
                    OutputStream os = exchange.getResponseBody();
                    os.write(response.getBytes());
                    os.close();
                } catch (IOException e1) {
                    // TODO Auto-generated catch block
                    e1.printStackTrace();
                }
               
            } 


            //if the statement fails we send a 400 response and a message
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
                } catch (IOException e1) {
                    // TODO Auto-generated catch block
                    e1.printStackTrace();
                }
            }
        }

    }

    public List<Employee> getAllEmployees() throws SQLException {
        String sql = "select * from employees";
        List<Employee> listofEmployees = new ArrayList();

        try (Connection con = ConnectionUtil.getConnection()) {
            Statement s = con.createStatement(0, 0);
            ResultSet rs = s.executeQuery(sql);

            while (rs.next()) {
                Employee e = new Employee();
                e.setEmpId(rs.getInt(1));
                e.setFname(rs.getString(2));
                e.setLname(rs.getString(3));
                e.setAddress(rs.getString(4));
                e.setEmail(rs.getString(5));
                e.setPassword(rs.getString(6));

                listofEmployees.add(e);
            }
        }
        return listofEmployees;
    }

}
