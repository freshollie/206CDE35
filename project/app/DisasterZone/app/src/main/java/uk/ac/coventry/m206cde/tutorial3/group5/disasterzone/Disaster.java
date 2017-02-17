package uk.ac.coventry.m206cde.tutorial3.group5.disasterzone;

/**
 * Created by freshollie on 2/13/17.
 */

public class Disaster {
    private String name;
    private Item[] items;
    private String type;
    private int id;

    public Disaster(String disasterName, Item[] disasterItems,
                    String disasterType, int disasterId) {
        name = disasterName;
        items = disasterItems;
        type = disasterType;
        id = disasterId;
    }

    public String getName() {
        return name;
    }

    public Item[] getItems() {
        return items;
    }

    public String getType() {
        return type;
    }

    public int getId() {
        return id;
    }
}
