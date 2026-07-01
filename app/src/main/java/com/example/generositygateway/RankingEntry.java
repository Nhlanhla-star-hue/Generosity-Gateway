package com.example.generositygateway;

public class RankingEntry {
    private String rank;
    private String username;
    private String totalQuantityDonated;

    public RankingEntry(String rank, String username, String totalQuantityDonated) {
        this.rank = rank;
        this.username = username;
        this.totalQuantityDonated = totalQuantityDonated;
    }

    public String getRank() {
        return rank;
    }

    public String getUsername() {
        return username;
    }

    public String getTotalQuantityDonated() {
        return totalQuantityDonated;
    }
}
