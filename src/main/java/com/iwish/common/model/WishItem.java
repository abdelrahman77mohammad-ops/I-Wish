package com.iwish.common.model;

import java.io.Serializable;

/**
 * One entry on a user's wish list, together with how much money has
 * already been contributed toward buying it.
 */
public class WishItem implements Serializable {
    private static final long serialVersionUID = 1L;

    private int id;             // wish_items.id
    private int ownerId;        // whose wish list this belongs to
    private int catalogItemId;
    private String itemName;    // copied from the catalog for easy display
    private double price;       // target price
    private double contributed; // sum of contributions so far
    private boolean purchased;  // true once fully funded / bought

    public WishItem() { }

    public WishItem(int id, int ownerId, int catalogItemId, String itemName,
                    double price, double contributed, boolean purchased) {
        this.id = id;
        this.ownerId = ownerId;
        this.catalogItemId = catalogItemId;
        this.itemName = itemName;
        this.price = price;
        this.contributed = contributed;
        this.purchased = purchased;
    }

    /** How much money is still needed to complete this gift. */
    public double remaining() {
        double r = price - contributed;
        return r < 0 ? 0 : r;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getOwnerId() { return ownerId; }
    public void setOwnerId(int ownerId) { this.ownerId = ownerId; }

    public int getCatalogItemId() { return catalogItemId; }
    public void setCatalogItemId(int catalogItemId) { this.catalogItemId = catalogItemId; }

    public String getItemName() { return itemName; }
    public void setItemName(String itemName) { this.itemName = itemName; }

    public double getPrice() { return price; }
    public void setPrice(double price) { this.price = price; }

    public double getContributed() { return contributed; }
    public void setContributed(double contributed) { this.contributed = contributed; }

    public boolean isPurchased() { return purchased; }
    public void setPurchased(boolean purchased) { this.purchased = purchased; }

    @Override
    public String toString() {
        if (purchased) {
            return itemName + "  —  ✔ bought";
        }
        return itemName + "  —  " + contributed + " / " + price + " EGP";
    }
}
