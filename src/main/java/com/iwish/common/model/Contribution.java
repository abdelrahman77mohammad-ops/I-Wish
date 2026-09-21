package com.iwish.common.model;

import java.io.Serializable;

/** One payment made by a friend toward a wish-list item. */
public class Contribution implements Serializable {
    private static final long serialVersionUID = 1L;

    private int id;
    private int wishItemId;
    private int buyerId;
    private String buyerName;
    private double amount;
    private String createdAt;

    public Contribution() { }

    public Contribution(int id, int wishItemId, int buyerId, String buyerName,
                        double amount, String createdAt) {
        this.id = id;
        this.wishItemId = wishItemId;
        this.buyerId = buyerId;
        this.buyerName = buyerName;
        this.amount = amount;
        this.createdAt = createdAt;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getWishItemId() { return wishItemId; }
    public void setWishItemId(int wishItemId) { this.wishItemId = wishItemId; }

    public int getBuyerId() { return buyerId; }
    public void setBuyerId(int buyerId) { this.buyerId = buyerId; }

    public String getBuyerName() { return buyerName; }
    public void setBuyerName(String buyerName) { this.buyerName = buyerName; }

    public double getAmount() { return amount; }
    public void setAmount(double amount) { this.amount = amount; }

    public String getCreatedAt() { return createdAt; }
    public void setCreatedAt(String createdAt) { this.createdAt = createdAt; }

    @Override
    public String toString() {
        return buyerName + " contributed " + amount + " EGP";
    }
}
