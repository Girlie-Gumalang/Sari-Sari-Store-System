package service;

public interface UtangService {
    void addUtang(String customerName, String productId, int qty);
    void viewUtangBook();
    void payUtang(String customerName, double amount);
}