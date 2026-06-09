package service;

import model.Product;
import repository.DataStorage;
import util.ConsoleMenu;

public class TransactionServiceImpl implements TransactionService {
    private InventoryService invService;

    public TransactionServiceImpl(InventoryService invService) {
        this.invService = invService;
    }

    @Override
    public void processCashCheckout(String productId, int qty) {
        Product p = invService.getProductById(productId);
        if (p != null && p.getQuantity() >= qty) {
            double total = p.getPrice() * qty;
            p.setQuantity(p.getQuantity() - qty); 
            DataStorage.totalSales += total;     
            DataStorage.totalCashSales += total;
            
            System.out.printf("Transaction Authorized: PHP %.2f rendered for %d units of %s.\n", total, qty, p.getName());
        } else {
            System.out.println("Authorization Failed: Insufficient stock level or invalid Product ID reference!");
        }
    }
    
    @Override
    public void viewSalesAnalytics() {
        int lowStockCount = 0;
        for (Product p : repository.DataStorage.products) {
            if (p.isLowStock()) {
                lowStockCount++;
            }
        }

        ConsoleMenu.printHeader("FINANCIAL SALES REPORT");
        
        System.out.printf("  Total Gross Revenue     : PHP %,.2f\n", repository.DataStorage.totalSales);
        System.out.println("  ---------------------------------------------------------");
        System.out.printf("  Direct Cash Sales  : PHP %,.2f\n", repository.DataStorage.totalCashSales);
        System.out.printf("  Credit Collections : PHP %,.2f\n", repository.DataStorage.totalCreditCollected);
        System.out.println("  ---------------------------------------------------------");
        System.out.printf("  Critical Low Stock Items : %d product(s) need restocking!\n", lowStockCount);
        
        System.out.println("===========================================================");
    }
}