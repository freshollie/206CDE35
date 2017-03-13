package uk.ac.coventry.m206cde.tutorial3.group5.disasterzone;

/**
 * Created by freshollie on 2/13/17.
 */

public class Disaster {
    private String name;
    private DisasterItem[] disasterItems;
    private String type;
    private String description;
    private int id;
    private String[] tips;
    private String imageResource;

    public Disaster(String disasterName, DisasterItem[] disasterDisasterItems,
                    String disasterType, int disasterId,
                    String disasterDescription, String[] disasterTips, String disasterImageResource) {
        name = disasterName;
        this.disasterItems = disasterDisasterItems;
        type = disasterType;
        id = disasterId;
        description = disasterDescription;
        tips = disasterTips;
        imageResource = disasterImageResource;
    }

    public String getName() {
        return name;
    }

    public DisasterItem[] getDisasterItems() {
        return disasterItems;
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
