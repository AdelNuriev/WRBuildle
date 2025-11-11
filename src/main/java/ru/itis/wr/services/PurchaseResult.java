package ru.itis.wr.services;

import ru.itis.wr.entities.UserPurchase;

public class PurchaseResult {
    private final boolean success;
    private final String message;
    private final UserPurchase purchase;

    private PurchaseResult(boolean success, String message, UserPurchase purchase) {
        this.success = success;
        this.message = message;
        this.purchase = purchase;
    }

    public static PurchaseResult success(UserPurchase purchase, String message) {
        return new PurchaseResult(true, message, purchase);
    }

    public static PurchaseResult error(String message) {
        return new PurchaseResult(false, message, null);
    }

    // Getters
    public boolean isSuccess() { return success; }
    public String getMessage() { return message; }
    public UserPurchase getPurchase() { return purchase; }
}
