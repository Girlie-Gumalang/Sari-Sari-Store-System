package model;

public class Product extends AbstractItem {
    private int quantity;
    private final int LOW_STOCK_THRESHOLD = 5; // Alert kapag 5 pababa ang stock

    public Product(String id, String name, double price, int quantity) {
        super(id, name, price); // call constructor of parent (AbstractItem)
        this.quantity = quantity;
    }

    // Getters and Setters
    public String getId() { return id; }
    public String getName() { return name; }
    
    public double getPrice() { return price; }
    public void setPrice(double price) { this.price = price; }

    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }

    // Helper method for Low Stock Alert
    public boolean isLowStock() { 
        return this.quantity <= LOW_STOCK_THRESHOLD; 
    }

    @Override
    public void displayDetails(int rowNum) {
        String alert = isLowStock() ? "       [LOW STOCK!]" : "";
        // Combine quantity and alert into a single string
        String stockInfo = quantity + alert; 

        // Using %-20s ensures that regardless of the content, the column takes exactly 20 spaces
        System.out.printf("| %-3d | %-6s | %-20s | PHP %-7.2f | %-20s |\n", 
                rowNum, id, name, price, stockInfo);
    }
}