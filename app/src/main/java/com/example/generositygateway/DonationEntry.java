package com.example.generositygateway;

public class DonationEntry {
    private String item;
    private int donatedQuantity;
    private String recipientName;

    public DonationEntry(String item, int donatedQuantity,String recipientName) {
        this.item = item;
        this.donatedQuantity = donatedQuantity;
        this.recipientName = recipientName;
    }

    public String getItem() {
        return item;
    }

    public int getDonatedQuantity() {
        return donatedQuantity;
    }

    public String getRecipientName() {
        return recipientName;
    }
}
