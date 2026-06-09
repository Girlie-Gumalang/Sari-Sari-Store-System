package service;

import model.Product;

public interface InventoryService {
    void addProduct(Product product);                
    void viewAllProducts();                           
    void updateProductPrice(String id, double price); 
    void restockProduct(String id, int qty);       
    void removeProduct(String id);               
    
    Product getProductById(String id);
}