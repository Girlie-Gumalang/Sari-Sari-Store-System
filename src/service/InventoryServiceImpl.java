package service;

import model.Product;
import repository.DataStorage;

public class InventoryServiceImpl implements InventoryService {

    @Override
    public void addProduct(Product product) {
        for (Product p : repository.DataStorage.products) {
            if (p.getId().equalsIgnoreCase(product.getId())) {
                System.out.println("\nProduct ID '" + product.getId() + "' already exists in the database registry!");
                return; 
            }
        }
        
        repository.DataStorage.products.add(product);
        System.out.println("Product entry added successfully!");
    }
    
    @Override
    public void viewAllProducts() {
        // Adjusted  to perfectly match the width of the rows
        System.out.println("\n--------------------------------------------------------------------------");
        System.out.println("| No. | ID     | PRODUCT NAME         | UNIT PRICE  | STOCK STATUS         |");
        System.out.println("--------------------------------------------------------------------------");
        
        if (DataStorage.products.isEmpty()) {
            // Adjusted spacing to keep text centered inside the 74-character width table
            System.out.println("|                  Inventory registry is currently empty.                |");
        } else {
            int rowNum = 1; 
            for (Product p : DataStorage.products) {
                p.displayDetails(rowNum); 
                rowNum++; 
            }
        }
        System.out.println("--------------------------------------------------------------------------");
    }

    @Override
    public void updateProductPrice(String id, double price) {
        for (Product p : DataStorage.products) {
            if (p.getId().equalsIgnoreCase(id)) {
                p.setPrice(price);
                System.out.println("Success: Unit price for " + p.getName() + " updated to PHP " + price);
                return;
            }
        }
        System.out.println("System Error: Product ID reference mismatch: " + id);
    }

    @Override
    public void restockProduct(String id, int qty) {
        for (Product p : DataStorage.products) {
            if (p.getId().equalsIgnoreCase(id)) {
                p.setQuantity(p.getQuantity() + qty);
                System.out.println("Success: Replenishment successful. Current stock for " + p.getName() + " is " + p.getQuantity());
                return;
            }
        }
        System.out.println("System Error: Product ID reference mismatch: " + id);
    }
    
    @Override
    public void removeProduct(String id) {
        boolean removed = false;
        
        // Loop until sa dulo ng ArrayList
        for (int i = 0; i < DataStorage.products.size(); i++) {
            Product p = DataStorage.products.get(i);
            
            if (p.getId().equalsIgnoreCase(id)) {
                DataStorage.products.remove(i);
                removed = true;
                break; 
            }
        }

        if (removed) {
            System.out.println("Product entry terminated from the database successfully.");
        } else {
            System.out.println("System Error: Product ID reference mismatch: " + id);
        }
    }
    
    @Override
    public Product getProductById(String id) {
        for (Product p : DataStorage.products) {
            if (p.getId().equalsIgnoreCase(id)) {
                return p; 
            }
        }
        return null; 
    }
}