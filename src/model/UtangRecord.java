package model;

public class UtangRecord {
    private String customerName;
    private double amount;
    private String status; 

    public UtangRecord(String customerName, double amount) {
        this.customerName = customerName;
        this.amount = amount;
        this.status = "UNPAID"; 
    }

    public String getCustomerName() { return customerName; }
    
    public double getAmount() { return amount; }
    
    public void addUtang(double amt) { this.amount += amt; }
    
    public String getStatus() { return status; }
    
    public void setStatus(String status) { this.status = status; }
    
    public double payUtang(double payment) {
        double sukli = 0.0;
        
        if (payment > this.amount) {
            sukli = payment - this.amount; 
            this.amount = 0;               
            this.status = "PAID";
        } else {
            this.amount -= payment;
            if (this.amount == 0) {
                this.status = "PAID";
            }
        }
        
        return sukli; 
    }
}