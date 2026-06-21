package main;

import config.DBConnection;
import util.ConsoleMenu;
import model.Product;
import model.User;
import service.InventoryService;
import service.InventoryServiceImpl;
import service.UtangService;
import service.UtangServiceImpl;
import service.TransactionService;
import service.TransactionServiceImpl;
import service.AuthService;
import service.AuthServiceImpl;

import java.util.Scanner;

public class MainApp {
    private static final Scanner scanner = new Scanner(System.in);
    private static final InventoryService inventoryService = new InventoryServiceImpl(); 
    private static final UtangService utangService = new UtangServiceImpl(inventoryService);
    private static final TransactionService transactionService = new TransactionServiceImpl(inventoryService);
    private static final AuthService authService = new AuthServiceImpl();

    public static void main(String[] args) {
        
        DBConnection.initializeDatabase();
         
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
                    handleCashierLogin();
                    break;
                case 3:
                    System.out.println("System closed!");
                    System.exit(0);
                    break;
                default:
                    System.out.println("Invalid choice! Select 1 to 3.");
            }
        }
    }

    // ADMIN LOGIN
    private static void handleAdminLogin() {
        System.out.println("\n----------------------- ADMIN LOGIN -----------------------");
        System.out.print("Username: ");
        String username = getValidStringInput();
        
        System.out.print("Password: ");
        String password = getValidStringInput();

        User user = authService.login(username, password);

        if (user != null && user.getRole().equalsIgnoreCase("ADMIN")) {
            System.out.println("\nLogin success! Welcome, Admin.");
            showAdminMenu();
        } else {
            System.out.println("\nLogin failed! Wrong credentials or unauthorized access.");
        }
    }

    // ADMIN MENU (CRUD, RESTOCK, ANALYTICS, UTANG)
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
                    String customerName = getValidStringInput(); 
                    
                    System.out.print("Enter Amount Paid (PHP): ");
                    double paymentAmount = getValidDoubleInput(); 
                    
                    utangService.payUtang(customerName, paymentAmount); 
                    break;
                case 9: 
                    System.out.println("Logged out from Admin Dashboard.");
                    return; 
                default:
                    System.out.println("Invalid choice! Select 1 to 9.");
            }
        }
    }
    
    // CASHIER LOGIN
    private static void handleCashierLogin() {
        System.out.println("\n----------------------- CASHIER LOGIN -----------------------");
        System.out.print("Username: ");
        String username = getValidStringInput();
        
        System.out.print("Password: ");
        String password = getValidStringInput();

        User user = authService.login(username, password);

        if (user != null && user.getRole().equalsIgnoreCase("CASHIER")) {
            System.out.println("Login success! Welcome, Cashier.");
            showCashierMenu();
        } else {
            System.out.println("Login failed! Invalid credentials.");
        }
    }

    // CASHIER / CUSTOMER MODE 
    private static void showCashierMenu() {
        while (true) {
            ConsoleMenu.printHeader("CASHIER TERMINAL");
            inventoryService.viewAllProducts(); 
            
            System.out.print("\nEnter Product ID to buy (or type 'BACK' to logout): ");
            String buyId = getValidStringInput(); 
            
            if (buyId.equalsIgnoreCase("BACK")) {
                System.out.println("Logged out from Cashier Terminal.");
                return;
            }
            
            Product targetProduct = inventoryService.getProductById(buyId);
            
            if (targetProduct == null) {
                System.out.println("Error: Product not found!");
                continue; 
            }
            
            System.out.print("Enter Quantity for " + targetProduct.getName() + ": ");
            int qtyToBuy = getValidIntegerInput();
            
            System.out.print("Select Type (CASH / UTANG): ");
            String paymentType = getValidStringInput();
            
            if (paymentType.equalsIgnoreCase("CASH")) {
                transactionService.processCashCheckout(buyId, qtyToBuy);
            } else if (paymentType.equalsIgnoreCase("UTANG")) {
                System.out.print("Enter Customer Name: ");
                String customerName = getValidStringInput(); 
                
                utangService.addUtang(customerName, buyId, qtyToBuy);
            } else {
                System.out.println("Error: Invalid option! Transaction cancelled.");
            }
        }
    }
    
    // SALES ANALYTICS 
    private static void showSalesAnalytics() {
        transactionService.viewSalesAnalytics(); 
    }
    
    // UTANG BOOK
    private static void showUtangBook() {
        utangService.viewUtangBook();
    }

    // DATA VALIDATION HELPERS 
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