package service;

import config.DBConnection;
import model.Product;
import java.sql.*;

public class UtangServiceImpl implements UtangService {

    private InventoryService inventoryService;

    public UtangServiceImpl(InventoryService inventoryService) {
        this.inventoryService = inventoryService;
    }

    @Override
    public void addUtang(String customerName, String buyId, int qtyToBuy) {
        customerName = customerName.toUpperCase();

        Product p = inventoryService.getProductById(buyId);
        if (p == null || p.getQuantity() < qtyToBuy) {
            System.out.println("Error: Product not found or insufficient stock!");
            return;
        }

        double total = p.getPrice() * qtyToBuy;
        inventoryService.restockProduct(buyId, -qtyToBuy);

        String txQuery = "INSERT INTO total_sales (product_id, product_name, quantity, grossSales, collected, type) VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(txQuery)) {
            ps.setString(1, buyId);
            ps.setString(2, p.getName());
            ps.setInt(3, qtyToBuy);
            ps.setDouble(4, total);
            ps.setDouble(5, 0);
            ps.setString(6, "UTANG");
            ps.executeUpdate();
        } catch (Exception e) {
        }

        String utangQuery = "INSERT INTO utang_records (customerName, amount, status) VALUES (?, ?, 'UNPAID') "
                + "ON CONFLICT(customerName) DO UPDATE SET amount = amount + ?, status = 'UNPAID'";
        try (Connection conn = DBConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(utangQuery)) {
            ps.setString(1, customerName);
            ps.setDouble(2, total);
            ps.setDouble(3, total);
            ps.executeUpdate();
            System.out.printf("Credit recorded for %s. Amount: PHP %.2f\n", customerName, total);
        } catch (Exception e) {
            System.out.println("Utang Record Error: " + e.getMessage());
        }
    }

    @Override
    public void payUtang(String customerName, double amountPaid) {
        customerName = customerName.toUpperCase();

        String checkQuery = "SELECT amount, customerName FROM utang_records WHERE UPPER(customerName)=?";
        try (Connection conn = DBConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(checkQuery)) {

            ps.setString(1, customerName);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                double currentBal = rs.getDouble("amount");
                String exactDbName = rs.getString("customerName");
                double newBal = currentBal - amountPaid;

                if (newBal <= 0) {
                    double change = Math.abs(newBal);

                    String upQuery = "UPDATE utang_records SET amount = 0, status = 'PAID' WHERE customerName=?";
                    PreparedStatement upPs = conn.prepareStatement(upQuery);
                    upPs.setString(1, exactDbName);
                    upPs.executeUpdate();

                    System.out.printf("%s has fully paid! Change: PHP %.2f\n", customerName, change);
                } else {
                    String upQuery = "UPDATE utang_records SET amount=?, status = 'UNPAID' WHERE customerName=?";
                    PreparedStatement upPs = conn.prepareStatement(upQuery);
                    upPs.setDouble(1, newBal);
                    upPs.setString(2, exactDbName);
                    upPs.executeUpdate();

                    System.out.printf("%s paid PHP %.2f. Remaining balance: PHP %.2f\n", customerName, amountPaid, newBal);
                }
            } else {
                System.out.println("No credit record found for " + customerName);
            }
        } catch (Exception e) {
            System.out.println("Pay Utang Error: " + e.getMessage());
        }
    }

    @Override
    public void viewUtangBook() {
        util.ConsoleMenu.printHeader("DIGITAL CREDIT LEDGER");
        String query = "SELECT * FROM utang_records";

        System.out.printf("%-20s %-15s %-10s\n", "Account:", "Balance:", "Status:");
        System.out.println("---------------------------------------------------------");

        try (Connection conn = DBConnection.getConnection();
                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery(query)) {

            boolean hasUtang = false;
            while (rs.next()) {
                hasUtang = true;
                double bal = rs.getDouble("amount");
                String status = rs.getString("status");

                System.out.printf("%-20s PHP %-11.2f %-10s\n", rs.getString("customerName"), bal, status);
            }
            if (!hasUtang) {
                System.out.println("No pending credits!");
            }
            System.out.println("=========================================================");

        } catch (Exception e) {
            System.out.println("Utang Book Error: " + e.getMessage());
        }
    }
}
