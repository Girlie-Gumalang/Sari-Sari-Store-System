package repository;

import config.DBConnection;
import model.Product;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ProductRepository {

    public List<Product> findAll() {
        List<Product> list = new ArrayList<>();
        String query = "SELECT * FROM products";
        
        // Close connection after use
        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            while (rs.next()) {
                list.add(new Product(
                        rs.getString("id"),
                        rs.getString("name"),
                        rs.getDouble("price"),
                        rs.getInt("quantity")
                ));
            }
        } catch (Exception e) {
            System.out.println("Error in findAll: " + e.getMessage());
        }
        return list;
    }

    public Product findById(String id) {
        String query = "SELECT * FROM products WHERE id=?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(query)) {

            ps.setString(1, id);
            
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new Product(
                            rs.getString("id"),
                            rs.getString("name"),
                            rs.getDouble("price"),
                            rs.getInt("quantity")
                    );
                }
            }
        } catch (Exception e) {
            System.out.println("Error in findById: " + e.getMessage());
        }
        return null;
    }

    public void save(Product p) {
        String query = "INSERT INTO products VALUES(?,?,?,?)";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(query)) {

            ps.setString(1, p.getId());
            ps.setString(2, p.getName());
            ps.setDouble(3, p.getPrice());
            ps.setInt(4, p.getQuantity());

            ps.executeUpdate();
            System.out.println("[System]: Product saved successfully!");

        } catch (Exception e) {
            System.out.println("Error in save: " + e.getMessage());
        }
    }

    public void updatePrice(String id, double price) {
        String query = "UPDATE products SET price=? WHERE id=?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(query)) {

            ps.setDouble(1, price);
            ps.setString(2, id);

            ps.executeUpdate();
            System.out.println("[System]: Price updated successfully!");

        } catch (Exception e) {
            System.out.println("Error in updatePrice: " + e.getMessage());
        }
    }

    public void updateQuantity(String id, int qty) {
        String query = "UPDATE products SET quantity=? WHERE id=?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(query)) {

            ps.setInt(1, qty);
            ps.setString(2, id);

            ps.executeUpdate();
            System.out.println("[System]: Quantity updated successfully!");

        } catch (Exception e) {
            System.out.println("Error in updateQuantity: " + e.getMessage());
        }
    }

    public void delete(String id) {
        String query = "DELETE FROM products WHERE id=?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(query)) {

            ps.setString(1, id);

            ps.executeUpdate();
            System.out.println("[System]: Product deleted successfully!");

        } catch (Exception e) {
            System.out.println("Error in delete: " + e.getMessage());
        }
    }
}