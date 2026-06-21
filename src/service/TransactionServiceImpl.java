package service;

import config.DBConnection;
import model.Product;
import java.sql.*;

public class TransactionServiceImpl implements TransactionService {
    private InventoryService inventoryService;

    public TransactionServiceImpl(InventoryService inventoryService) {
        this.inventoryService = inventoryService;
    }

    @Override
    public void processCashCheckout(String buyId, int qtyToBuy) {
        Product p = inventoryService.getProductById(buyId);
        if (p == null) {
            System.out.println("Error: Product not found!");
            return;
        }
        if (p.getQuantity() < qtyToBuy) {
            System.out.println("Error: Insufficient stock!");
            return;
        }

        double total = p.getPrice() * qtyToBuy;
        inventoryService.restockProduct(buyId, -qtyToBuy);

        String query = "INSERT INTO total_sales (product_id, product_name, quantity, grossSales, collected, type) VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(query)) {
            
            ps.setString(1, buyId);
            ps.setString(2, p.getName());
            ps.setInt(3, qtyToBuy);
            ps.setDouble(4, total);      
            ps.setDouble(5, total);     
            ps.setString(6, "CASH");
            
            ps.executeUpdate();
            System.out.printf("Success! Total Amount Paid: PHP %.2f\n", total);
        } catch (Exception e) {
            System.out.println("Transaction Error: " + e.getMessage());
        }
    }

    @Override
    public void viewSalesAnalytics() {
        util.ConsoleMenu.printHeader("FINANCIAL SALES REPORT");

        double cashSales = 0;
        double totalUtangOriginated = 0;
        double currentUtangBalance = 0;
        int lowStockCount = 0;

        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement()) {

            ResultSet rs1 = stmt.executeQuery("SELECT SUM(grossSales) AS cash FROM total_sales WHERE type='CASH'");
            if (rs1.next()) cashSales = rs1.getDouble("cash");

            ResultSet rs2 = stmt.executeQuery("SELECT SUM(grossSales) AS utang FROM total_sales WHERE type='UTANG'");
            if (rs2.next()) totalUtangOriginated = rs2.getDouble("utang");

            ResultSet rs3 = stmt.executeQuery("SELECT SUM(amount) AS unpaid FROM utang_records");
            if (rs3.next()) currentUtangBalance = rs3.getDouble("unpaid");

            ResultSet rs4 = stmt.executeQuery("SELECT COUNT(*) AS low_stock FROM products WHERE quantity <= 5");
            if (rs4.next()) lowStockCount = rs4.getInt("low_stock");

        } catch (Exception e) {
            System.out.println("Analytics Error: " + e.getMessage());
        }

        double creditCollections = totalUtangOriginated - currentUtangBalance;
        if (creditCollections < 0) creditCollections = 0; 
        
        double totalRevenue = cashSales + creditCollections;

        System.out.printf("  Total Gross Revenue      : PHP %,.2f\n", totalRevenue);
        System.out.println("  ---------------------------------------------------------");
        System.out.printf("  Direct Cash Sales  : PHP %,.2f\n", cashSales);
        System.out.printf("  Credit Collections : PHP %,.2f\n", creditCollections);
        System.out.println("  ---------------------------------------------------------");
        System.out.printf("  Critical Low Stock Items : %d product(s) need restocking!\n", lowStockCount);
        
        System.out.println("===========================================================");
    }
}