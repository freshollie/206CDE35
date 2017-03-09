package uk.ac.coventry.m206cde.tutorial3.group5.disasterzone;

/**
 * Created by freshollie on 2/13/17.
 */

public class Disaster {
    private String name;
    private Item[] items;
    private String type;
    private String description;
    private int id;
    private String[] tips;

    public Disaster(String disasterName, Item[] disasterItems,
                    String disasterType, int disasterId,
                    String disasterDescription, String[] disasterTips) {
        name = disasterName;
        items = disasterItems;
        type = disasterType;
        id = disasterId;
        description = disasterDescription;
        tips = disasterTips;
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

    public String getDescription() {
        return description;
    }

    public String[] getTips() {
        return tips;
    }
}
