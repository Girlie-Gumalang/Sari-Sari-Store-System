package main;

import util.ConsoleMenu;
import model.Product;
import model.UtangRecord;
import repository.DataStorage;
import service.InventoryService;
import service.InventoryServiceImpl;
import service.UtangService;
import service.UtangServiceImpl;
import service.TransactionService;
import service.TransactionServiceImpl;

import java.util.Scanner;

public class MainApp {
    private static final Scanner scanner = new Scanner(System.in);
    private static final InventoryService inventoryService = new InventoryServiceImpl(); 
    private static final UtangService utang = new UtangServiceImpl(inventoryService);
    private static TransactionService transactionService = new TransactionServiceImpl(inventoryService);

    public static void main(String[] args) {
        while (true) {
            ConsoleMenu.printHeader("SARI-SARI STORE SYSTEM");
            System.out.println("[1] Admin Dashboard");
            System.out.println("[2] Cashier Terminal");
            System.out.println("[3] Exit");
            System.out.print("\nSelect Mode: ");
            
            int choice = getValidIntegerInput();
            
            switch (choice) {
                case 1:
                    handleAdminLogin();
                    break;
                case 2:
                    showCashierMenu();
                    break;
                case 3:
                    System.out.println("System closed.");
                    System.exit(0);
                    break;
                default:
                    System.out.println("Invalid choice! Select 1 to 3.");
            }
        }
    }

    // --- SECURITY LAYER (ADMIN LOGIN) ---
    private static void handleAdminLogin() {
        System.out.println("\n----------------------- ADMIN LOGIN -----------------------");
        System.out.print("Username: ");
        String username = getValidStringInput();
        System.out.print("Password: ");
        String password = getValidStringInput();

        if (username.equals("admin") && password.equals("1234")) {
            System.out.println("Login success!");
            showAdminMenu();
        } else {
            System.out.println("Login failed! Wrong credentials.");
        }
    }

    // --- VIEW LAYER: ADMIN MENU (CRUD, RESTOCK, ANALYTICS, UTANG) ---
    private static void showAdminMenu() {
        while (true) {
            ConsoleMenu.printHeader("ADMIN DASHBOARD");
            System.out.println("[1] View All Products");
            System.out.println("[2] Add Product");
            System.out.println("[3] Update Product Price");
            System.out.println("[4] Restock Product");
            System.out.println("[5] Delete Product");
            System.out.println("[6] View Sales Analytics");
            System.out.println("[7] View Credit Ledger");
            System.out.println("[8] Settle Customer Balance"); 
            System.out.println("[9] Logout"); 
            System.out.print("\nSelect Option: ");
            
            int action = getValidIntegerInput();

            switch (action) {
                case 1:
                    inventoryService.viewAllProducts();
                    break;
                case 2:
                    System.out.print("Enter Product ID (e.g., P03): ");
                    String id = getValidStringInput(); 

                    System.out.print("Enter Name: ");
                    String name = getValidStringInput();
                    
                    System.out.print("Enter Price (PHP): ");
                    double price = getValidDoubleInput();
                    System.out.print("Enter Stock Quantity: ");
                    int qty = getValidIntegerInput();
                    
                    inventoryService.addProduct(new Product(id, name, price, qty));
                    break;
                case 3:
                    System.out.print("Enter Product ID: ");
                    String uId = getValidStringInput();
                    System.out.print("Enter New Price: ");
                    double newPrice = getValidDoubleInput();
                    
                    inventoryService.updateProductPrice(uId, newPrice);
                    break;
                case 4:
                    System.out.print("Enter Product ID: ");
                    String rId = getValidStringInput(); 
                    System.out.print("Enter Quantity to Add: ");
                    int addedQty = getValidIntegerInput();
                    
                    inventoryService.restockProduct(rId, addedQty);
                    break;
                case 5:
                    System.out.print("Enter Product ID to delete: ");
                    String dId = getValidStringInput(); 
                    
                    inventoryService.removeProduct(dId);
                    break;
                case 6:
                    showSalesAnalytics();
                    break;
                case 7:
                    showUtangBook();
                    break;
                case 8: 
                    System.out.print("Enter Customer Name: ");
                    String sukiName = getValidStringInput(); 
                    System.out.print("Enter Amount Paid (PHP): ");
                    double bayad = getValidDoubleInput(); 
                    
                    utang.payUtang(sukiName, bayad); 
                    break;
                case 9: 
                    System.out.println("Logged out.");
                    return; 
                default:
                    System.out.println("Invalid choice! Select 1 to 9.");
            }
        }
    }
    
    // --- VIEW LAYER: CASHIER / CUSTOMER MODE ---
    private static void showCashierMenu() {
        ConsoleMenu.printHeader("CASHIER TERMINAL");
        inventoryService.viewAllProducts(); 
        
        System.out.print("\nEnter Product ID to buy (or type 'BACK' to exit): ");
        String buyId = getValidStringInput(); // Fixed: Ginamit ang helper imbes na scanner.nextLine()
        
        if (buyId.equalsIgnoreCase("BACK")) return;
        
        Product targetProduct = inventoryService.getProductById(buyId);
        
        if (targetProduct == null) {
            System.out.println("Error: Product not found!");
            return;
        }
        
        System.out.print("Enter Quantity for " + targetProduct.getName() + ": ");
        int qtyToBuy = getValidIntegerInput();
        
        System.out.print("Select Type (CASH / UTANG): ");
        String paymentType = getValidStringInput(); // Fixed: Ginamit ang helper imbes na scanner.nextLine()
        
        if (paymentType.equalsIgnoreCase("CASH")) {
            transactionService.processCashCheckout(buyId, qtyToBuy);
        } else if (paymentType.equalsIgnoreCase("UTANG")) {
            System.out.print("Enter Customer Name: ");
            String sukiName = getValidStringInput(); // Fixed: Ginamit ang helper imbes na scanner.nextLine()
            
            utang.addUtang(sukiName, buyId, qtyToBuy);
        } else {
            System.out.println("Error: Invalid option!");
        }
    }

    // --- CORE LOGIC: SALES ANALYTICS ---
    private static void showSalesAnalytics() {
        transactionService.viewSalesAnalytics(); 
    }
    
    // --- CORE LOGIC: UTANG BOOK ---
    private static void showUtangBook() {
        utang.viewUtangBook();
    }

    // --- DATA VALIDATION HELPERS (Para Iwas System Crash) ---
    private static int getValidIntegerInput() {
        while (true) {
            try {
                int val = scanner.nextInt();
                scanner.nextLine(); 
                return val;
            } catch (Exception e) {
                System.out.print("Invalid input!\nEnter a number: ");
                scanner.nextLine(); 
            }
        }
    }

    private static double getValidDoubleInput() {
        while (true) {
            try {
                double val = scanner.nextDouble();
                scanner.nextLine(); 
                return val;
            } catch (Exception e) {
                System.out.print("Invalid input!\nEnter a decimal: ");
                scanner.nextLine(); 
            }
        }
    }
    
    private static String getValidStringInput() {
        while (true) {
            String input = scanner.nextLine();
            if (input.trim().isEmpty()) {
                System.out.print("Error: Input cannot be empty!\nEnter text again: ");
            } else {
                return input.trim();
            }
        }
    }
}