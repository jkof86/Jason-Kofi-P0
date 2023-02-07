package com.revature.repository;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;

import com.revature.model.Employee;
import com.revature.utils.ConnectionUtil;
import com.sun.net.httpserver.HttpExchange;

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
        
        // this is the String we use to execute sql statements
        String sql = "insert into employee (fname, lname, address, email, pw, roleid) values (?,?,?,?,?,?)";
        System.out.println("Checking for existing user...");

        // we use this to determine if there was a duplicate found with all 3 conditions
        // we return false as a default to avoid accidentally inserting new records
        boolean dup = false;

        // now we iterate through the employee list and check for a duplicate entry
        // if the full name matches, OR the email matches an existing one, we set dup to
        // true and cancel the registration
        // we will only allow unique email addresses in the DB
        for (Employee employee : currentList) {
            System.out.println(employee.toString());
            if (employee.getFname().equalsIgnoreCase(e.getFname()) &&
                    employee.getLname().equalsIgnoreCase(e.getLname()) ||
                    employee.getEmail().equalsIgnoreCase(e.getEmail())) {
                // System.out.println("Duplicate found...");
                dup = true;
                break;
            }
        }

        // if there's a duplicate, we immediately return false and cancel registration
        if (dup == true) {
            System.out.println("Duplicate DB Entry was found...");
            return false;
        }
        
        System.out.println("Accessing DB...");

        try (Connection con = ConnectionUtil.getConnection()) {

            // System.out.println("Accessing DB...");
            PreparedStatement ps = con.prepareStatement(sql);

            // we replace '?' into actual values from the obj we receive
            // this uses 1 based indexing!!
            ps.setString(1, e.getFname());
            ps.setString(2, e.getLname());
            ps.setString(3, e.getAddress());
            ps.setString(4, e.getEmail());
            ps.setString(5, e.getPassword());

            // role (1) = Employee
            // role (2) = Manager
            // we make sure to add the default "employee role" to the new registration
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

    public List<Employee> getAllEmployees() throws SQLException {
        String sql = "select * from employee";
        List<Employee> listofEmployees = new ArrayList();

        try (Connection con = ConnectionUtil.getConnection()) {
            Statement s = con.createStatement();
            ResultSet rs = s.executeQuery(sql);

                while (rs.next()) {
                    Employee e = new Employee();
                    e.setEmpId(rs.getInt(1));
                    e.setFname(rs.getString(2));
                    e.setLname(rs.getString(3));
                    e.setAddress(rs.getString(4));
                    e.setEmail(rs.getString(5));
                    e.setPassword(rs.getString(6));
                    e.setRole(rs.getInt(7));

                    listofEmployees.add(e);
                    // for testing
                    System.out.println("Gathering data...");
                }
        }
        return listofEmployees;
    }

    public Employee verify(String jsonObj) throws SQLException {

        // EmployeeRepository repo = new EmployeeRepository();
        ObjectMapper mapper = new ObjectMapper();
        Employee login = new Employee();

        // we gather all entries in the DB and match them with the login info
        List<Employee> currentList = getAllEmployees();

        //System.out.println(currentList.toString());

        // we convert the jsonObj to an Employee obj
    
        try {
            login = mapper.readValue(jsonObj, Employee.class);
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
        System.out.println("The object created for verification: \n" + login);

        // then compare it to all the objects the DB created
        for (Employee employee : currentList) {
            System.out.println("Verifying...");
            if (employee.getEmail().equals(login.getEmail()) &&
                    employee.getPassword().equals(login.getPassword())) {
                // if we find a match, we immediately return true
                // Manager login gets seperate message
                if (employee.getRole() == 2) {
                    System.out.println(
                            "LOGIN SUCCESSFUL - MANAGER ID:  " + employee.getEmpId() + " " + employee.getFname() +
                                    " " + employee.getLname() + ": " + employee.getEmail());
                    return employee;
                } else {
                    System.out.println(
                            "LOGIN SUCCESSFUL - EMPLOYEE ID:  " + employee.getEmpId() + " " + employee.getFname() +
                                    " " + employee.getLname() + ": " + employee.getEmail());
                    return employee;
                }

            }

        }
        return null;

    }
}