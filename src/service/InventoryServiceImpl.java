package service;

import model.Product;
import repository.ProductRepository;
import java.util.List;

public class InventoryServiceImpl implements InventoryService {

    private ProductRepository productRepo = new ProductRepository();

    @Override
    public void viewAllProducts() {
        List<Product> products = productRepo.findAll();

        String line = "-------------------------------------------------------------------------------";

        System.out.println("\n" + line);
        System.out.println("| No. | ID     | PRODUCT NAME         | UNIT PRICE      | STOCK STATUS        |");
        System.out.println(line);

        if (products.isEmpty()) {
            System.out.printf("| %-75s |\n", "Database is currently empty. Please add a product first.");
        } else {
            int counter = 1; 
            
            for (Product p : products) {
                String stockMsg;
                if (p.getQuantity() <= 5) {
                    stockMsg = String.format("%-4d [LOW STOCK!]", p.getQuantity());
                } else {
                    stockMsg = String.valueOf(p.getQuantity());
                }

                System.out.printf("| %-3d | %-6s | %-20s | PHP %-11.2f | %-19s |\n",
                        counter, p.getId(), p.getName(), p.getPrice(), stockMsg);
                        
                counter++; 
            }
        }
        System.out.println(line);
    }

    @Override
    public void addProduct(Product product) {
        
        product.setId(product.getId().toUpperCase());
        
        // check if may same ID sa database
        Product existing = productRepo.findById(product.getId());
        if (existing != null) {
            System.out.println("[Error]: ID already exists in the database! Please use a different ID.");
            return;
        }
        
        productRepo.save(product);
    }

    @Override
    public void updateProductPrice(String id, double price) {
        id = id.toUpperCase(); 
        
        Product existing = productRepo.findById(id);
        if (existing == null) {
            System.out.println("[Error]: Product not found in the database!");
            return;
        }
        productRepo.updatePrice(id, price);
    }

    @Override
    public void restockProduct(String id, int addedQty) {
        id = id.toUpperCase();
        
        Product existing = productRepo.findById(id);
        if (existing == null) {
            System.out.println("[Error]: Product not found in the database!");
            return;
        }
        
        int newQty = existing.getQuantity() + addedQty;
        productRepo.updateQuantity(id, newQty);
    }

    @Override
    public void removeProduct(String id) {
        id = id.toUpperCase();
        
        Product existing = productRepo.findById(id);
        if (existing == null) {
            System.out.println("[Error]: Product not found in the database!");
            return;
        }
        productRepo.delete(id);
    }

    @Override
    public Product getProductById(String id) {
        id = id.toUpperCase();
        
        return productRepo.findById(id);
    }
}