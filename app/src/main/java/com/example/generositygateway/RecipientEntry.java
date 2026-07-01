package com.example.generositygateway;

public class RecipientEntry {
    private String item;
    private int initialQuantity;
    private int receivedQuantity;
    private String donorUsername;

    public RecipientEntry(String item, int initialQuantity, int receivedQuantity, String donorUsername) {
        this.item = item;
        this.initialQuantity = initialQuantity;
        this.receivedQuantity = receivedQuantity;
        this.donorUsername = donorUsername;
    }

    public String getItem() {
        return item;
    }

    public int getInitialQuantity() {
        return initialQuantity;
    }

    public int getReceivedQuantity() {
        return receivedQuantity;
    }

    public String getDonorUsername() {
        return donorUsername;
    }
}
