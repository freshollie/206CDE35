package uk.ac.coventry.m206cde.tutorial3.group5.disasterzone;

/**
 * Created by freshollie on 2/13/17.
 */

public class Item {
    private String name;
    private float price;
    private int id;

    public Item(int itemId, String itemName, float itemPrice) {
        name = itemName;
        id = itemId;
        price = itemPrice;
    }

    public String getName() {
        return name;
    }

    public int getId() {
        return id;
    }

    public float getPrice() {
        return price;
    }
}
