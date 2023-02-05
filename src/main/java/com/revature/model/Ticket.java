package com.revature.model;

public class Ticket {

    private int id;
    private double amount = 0;
    private String desc;
    private String status = "pending";
    private int empid;


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
        return empid;
    }
    public void setEmpId() {
        
        this.empid = empid;
    }

    @Override
    public String toString() {
        return "Ticket [id=" + id + ", amount=" + amount + ", desc=" + desc + ", status=" + status + ", empid=" + empid
                + "]";
    }
    
    
}
