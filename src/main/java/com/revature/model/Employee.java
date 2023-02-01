package com.revature.model;

//here we define our Employee objects
public class Employee {

    private int empId;
    private String fname;
    private String lname;
    private String address;
    private String email;
    private String password;
    
    //this constructor caused issues with the logic, so we'll remove for now
    // public Employee(int empId, String fname, String lname, String address, String email, String password){
    //     this.empId = empId;
    //     this.fname = fname;
    //     this.lname = lname;
    //     this.address = address;
    //     this.email = email;
    //     this.password = password;
    // }

    public int getEmpId() {
        return empId;
    }

    public void setEmpId(int empId) {
        this.empId = empId;
    }

    public String getFname() {
        return fname;
    }

    public void setFname(String fname) {
        this.fname = fname;
    }

    public String getLname() {
        return lname;
    }

    public void setLname(String lname) {
        this.lname = lname;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    //we override the toString() method to display the details of each emplyee object created
    @Override
    public String toString(){
        return //"\n\nEmployee ID: " + empId +
               "\nName:  " + fname + " " + lname + 
               "\nAddress: " + address +
               "\nEmail: " + email +
               "\nPassword: " + password;
    }

    

    


    
}
