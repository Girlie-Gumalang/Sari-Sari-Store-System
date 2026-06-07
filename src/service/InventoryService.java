package service;

import model.Product;

public interface InventoryService {
    void addProduct(Product product);                 // CREATE
    void viewAllProducts();                           // READ
    void updateProductPrice(String id, double price); // UPDATE (Price)
    void restockProduct(String id, int qty);          // UPDATE (Restock)
    void removeProduct(String id);                    // DELETE
    
    Product getProductById(String id);
}