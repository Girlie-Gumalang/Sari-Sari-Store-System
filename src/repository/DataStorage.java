package repository;

import model.Product;
import model.UtangRecord;
import model.User;
import java.util.ArrayList;
import java.util.List;

public class DataStorage {
    public static List<Product> products = new ArrayList<>();
    public static List<UtangRecord> utangBook = new ArrayList<>();
    public static List<User> users = new ArrayList<>();
    
    public static double totalSales = 0.0;
    
    public static double totalCashSales = 0.0;
    public static double totalCreditCollected = 0.0;

    static {
        users.add(new User("admin", "1234", "ADMIN"));
        users.add(new User("cashier", "cashier123", "CASHIER"));
        
        products.add(new Product("P01", "Kopiko", 12.00, 20));
        products.add(new Product("P02", "Sardinas", 24.50, 4)); 
    }
}