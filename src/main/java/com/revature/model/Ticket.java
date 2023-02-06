package com.revature.model;

public class Ticket {

    private int id;
    private double amount = 0;
    private String desc;
    private String status = "pending";
    private int empId;

    //we need a constructor
    public Ticket () {

    }


    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }
    public double getAmount() {
        return amount;
    }
    public void setAmount(double amount) {
        this.amount = amount;
    }
    public String getDesc() {
        return desc;
    }
    public void setDesc(String desc) {
        this.desc = desc;
    }
    public String getStatus() {
        return status;
    }
    public void setStatus(String status) {
        this.status = status;
    }
    public int getEmpId() {
        return empId;
    }
    public void setEmpId(int empId) {
        
        this.empId = empId;
    }

    @Override
    public String toString() {
        return "Ticket [id=" + id + ", amount=" + amount + ", desc=" + desc + ", status=" + status + ", empid=" + empId
                + "]";
    }
    
    
}
