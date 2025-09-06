package com.example.chopinlist;

public class Product {
    private int id;
    private String name;
    private int quantity;
    private boolean checked;
    private boolean purchased;
    private String purchaseDate;

    public Product(int id, String name, int quantity, boolean checked, boolean purchased, String purchaseDate) {
        this.id = id;
        this.name = name;
        this.quantity = quantity;
        this.checked = checked;
        this.purchased = purchased;
        this.purchaseDate = purchaseDate;
    }

    public Product(String name, int quantity) {
        this.name = name;
        this.quantity = quantity;
        this.checked = false;
        this.purchased = false;
        this.purchaseDate = null;
    }

    // Getters y setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }
    public boolean isChecked() { return checked; }
    public void setChecked(boolean checked) { this.checked = checked; }
    public boolean isPurchased() { return purchased; }
    public void setPurchased(boolean purchased) { this.purchased = purchased; }
    public String getPurchaseDate() { return purchaseDate; }
    public void setPurchaseDate(String purchaseDate) { this.purchaseDate = purchaseDate; }
}

