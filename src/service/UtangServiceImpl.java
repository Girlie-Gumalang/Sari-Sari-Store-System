package service;

import model.Product;
import model.UtangRecord;
import repository.DataStorage;
import util.ConsoleMenu;

public class UtangServiceImpl implements UtangService {
    private InventoryService invService;

    public UtangServiceImpl(InventoryService invService) {
        this.invService = invService;
    }

    @Override
    public void addUtang(String customerName, String productId, int qty) {
        Product p = invService.getProductById(productId);
        if (p != null && p.getQuantity() >= qty) {
            double total = p.getPrice() * qty;
            p.setQuantity(p.getQuantity() - qty);
            
            // Check for existing credit record
            for (UtangRecord u : DataStorage.utangBook) {
                if (u.getCustomerName().equalsIgnoreCase(customerName)) {
                    u.addUtang(total);
                    System.out.println("Success: Balance appended to existing record for " + customerName);
                    return;
                }
            }
            // Register as a new customer entry if record does not exist
            DataStorage.utangBook.add(new UtangRecord(customerName, total));
            System.out.println("Success: New credit account registered for " + customerName);
        } else {
             System.out.println("Authorization Failed: Insufficient stock level or invalid Product ID reference.");
        }
    }

    @Override
    public void viewUtangBook() {
        ConsoleMenu.printHeader("DIGITAL CREDIT LEDGER");
        if (DataStorage.utangBook.isEmpty()) {
            System.out.println("No outstanding accounts receivable detected.");
            return;
        }
        for (UtangRecord u : DataStorage.utangBook) {
            System.out.printf("Account: %-15s | Balance: PHP %.2f | Status: %s\n", 
                    u.getCustomerName(), u.getAmount(), u.getStatus());
        }
    }
    
    @Override
    public void payUtang(String customerName, double amount) {
        // Utilizing index-based loop for safe object termination during list traversal
        for (int i = 0; i < DataStorage.utangBook.size(); i++) {
            UtangRecord u = DataStorage.utangBook.get(i);
            
            if (u.getCustomerName().equalsIgnoreCase(customerName)) {
                
                if (u.getStatus().equals("PAID")) {
                    System.out.println("Notice: Outstanding balance for " + customerName + " is already fully settled.");
                    return;
                }
                
                double kasalukuyangUtang = u.getAmount(); 
                double sukli = u.payUtang(amount); 
                
                // Update cumulative gross revenue analytics
                double totoongPumasokNaPera = (sukli > 0) ? kasalukuyangUtang : amount;
                DataStorage.totalSales += totoongPumasokNaPera; 
                
                DataStorage.totalCreditCollected += totoongPumasokNaPera;
                
                System.out.println("Success: Payment processed for account holder: " + customerName);
                if (sukli > 0) {
                    System.out.printf("   Change Rendered: PHP %.2f\n", sukli);
                }
                
                if (u.getStatus().equals("PAID")) {
                    // Account record termination upon full liability settlement
                    DataStorage.utangBook.remove(i);
                    System.out.println("System Notification: Credit account record for " + customerName + " has been terminated due to full settlement.");
                } else {
                    // Account statement notification for partial balance settlement
                    System.out.printf("   Remaining Balance: PHP %.2f | Status: %s\n", u.getAmount(), u.getStatus());
                }
                return;
            }
        }
        System.out.println("System Error: Account holder name reference mismatch: " + customerName);
    }
}