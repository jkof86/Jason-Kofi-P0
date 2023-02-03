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

    // we declare register a boolean treturn type so we can send response back at
    // the service level
    public boolean register(Employee e)
            throws SQLException, JsonGenerationException, JsonMappingException, IOException {

        // in order to check for duplicate entries in the existing table, first we must
        // grab all the employee objects
        // and compare fname, lname, and email
        List<Employee> currentList = getAllEmployees();

        // we use this to determine if there was a duplicate found with all 3 conditions
        // we false as a default to avoid accidentally inserting new records
        boolean dup = false;

        // now we iterate through the employee list and check for a duplicate entry
        //if the full name matches, OR the email matches an existing one, we set dup to true and cancel the registration
        //we will only allow unique email addresses in the DB
        for (Employee employee : currentList) {
            if (employee.getFname().equalsIgnoreCase(e.getFname()) &&
                    employee.getLname().equalsIgnoreCase(e.getLname()) ||
                    employee.getEmail().equalsIgnoreCase(e.getEmail())) {
                System.out.println("Duplicate found...");
                dup = true;
            } else {
                System.out.println("No Duplicates found...");
                System.out.println("Proceeding to INSERT operation");
                dup = false;
            }
        }

        // if there is no duplicates, we continue as normal
        if (dup == false) {

            ObjectMapper mapper = new ObjectMapper();
            // this is the String we use to execute sql statements
            String sql = "insert into employee (fname, lname, address, email, pw, roleid) values (?,?,?,?,?,?)";

            try (Connection con = ConnectionUtil.getConnection()) {
                PreparedStatement ps = con.prepareStatement(sql);

                // we replace '?' into actual values from the obj we receive
                // this uses 1 based indexing!!
                ps.setString(1, e.getFname());
                ps.setString(2, e.getLname());
                ps.setString(3, e.getAddress());
                ps.setString(4, e.getEmail());
                ps.setString(5, e.getPassword());
                
                //role (1) = Employee
                //role (2) = Manager
                //we make sure to add the default "employee role" to the new registration
                ps.setInt(6, 1);

                // execute() does not expect to return anything from the statement
                // executeQuery does expect something to result after executing the statement

                // if the statement executes successfully, we return the object and a
                // confirmation in the response body
                // executeUpdate() returns an int for the amount of rows affected, 0 for nothing
                // returned

                int result = ps.executeUpdate();
                // testing result return value
                System.out.println(result);

                // if the operation is successful we return true back to the service layer
                if (result > 0) {
                    return true;
                } else {
                    return false;
                }
            }
        } 

        // if there is a duplicate entry found, we just return false back to the service
        // layer
        
        else {
            System.out.println("Duplicate DB Entry was found...");
            return false;
        }
    }

    public List<Employee> getAllEmployees() throws SQLException {
        String sql = "select * from employee";
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

}
