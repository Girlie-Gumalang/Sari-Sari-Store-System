package config;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;

public class DBConnection {
    private static final String URL = "jdbc:sqlite:sarisaristore.db";

    public static Connection getConnection() {
        try {
            Class.forName("org.sqlite.JDBC");
            return DriverManager.getConnection(URL);
        } catch (Exception e) {
            System.out.println("[Error]: Database connection failed - " + e.getMessage());
            return null;
        }
    }

    public static void initializeDatabase() {
        
        // ERD Entity: PRODUCT
        String createProductsTable = "CREATE TABLE IF NOT EXISTS products (" +
                "id TEXT PRIMARY KEY, " +
                "name TEXT NOT NULL, " +
                "price REAL NOT NULL, " +
                "quantity INTEGER NOT NULL);";

        // ERD Entity: USER
        String createUsersTable = "CREATE TABLE IF NOT EXISTS users (" +
                "username TEXT PRIMARY KEY, " +
                "password TEXT NOT NULL, " +
                "role TEXT NOT NULL);";

        // ERD Entity: TOTAL_SALES
        String createTotalSalesTable = "CREATE TABLE IF NOT EXISTS total_sales (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "product_id TEXT NOT NULL, " +
                "product_name TEXT NOT NULL, " +
                "quantity INTEGER NOT NULL, " +
                "grossSales REAL NOT NULL, " + 
                "collected REAL NOT NULL, " + 
                "type TEXT NOT NULL, " +
                "timestamp DATETIME DEFAULT CURRENT_TIMESTAMP);";

        // ERD Entity: UTANG_RECORD
        String createUtangTable = "CREATE TABLE IF NOT EXISTS utang_records (" +
                "customerName TEXT PRIMARY KEY, " + 
                "amount REAL NOT NULL, " +         
                "status TEXT NOT NULL);";           

        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement()) {
            
            if (conn != null) {
                stmt.execute(createProductsTable);
                stmt.execute(createUsersTable);           
                stmt.execute(createTotalSalesTable);    
                stmt.execute(createUtangTable);           
                
                String insertDefaultUsers = "INSERT OR IGNORE INTO users (username, password, role) VALUES " +
                        "('admin', '1234', 'ADMIN'), " +
                        "('cashier', 'cashier123', 'CASHIER');";
                stmt.execute(insertDefaultUsers);

                System.out.println("[System]: Database and tables ready!");
            }
        } catch (Exception e) {
            System.out.println("[Error]: Table creation failed - " + e.getMessage());
        }
    }
}