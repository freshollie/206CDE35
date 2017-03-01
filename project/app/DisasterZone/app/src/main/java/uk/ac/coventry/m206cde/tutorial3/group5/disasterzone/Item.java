package uk.ac.coventry.m206cde.tutorial3.group5.disasterzone;

/**
 * Created by freshollie on 2/13/17.
 */

public class Item {
    private String name;
    private String price;
    private int id;
    private String[] locations;

    public Item(int itemId, String itemName, String itemPrice, String[] itemLocations) {
        name = itemName;
        id = itemId;
        price = itemPrice;
        locations = itemLocations;

    }

    public String getName() {
        return name;
    }

    public int getId() {
        return id;
    }

    public String getPrice() {
        return price;
    }
}
