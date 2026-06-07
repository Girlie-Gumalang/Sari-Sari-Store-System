package service;

public interface TransactionService {
    void processCashCheckout(String productId, int qty);
    void viewSalesAnalytics();
}