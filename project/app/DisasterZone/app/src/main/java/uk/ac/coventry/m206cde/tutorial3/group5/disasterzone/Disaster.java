package uk.ac.coventry.m206cde.tutorial3.group5.disasterzone;

/**
 * Created by freshollie on 2/13/17.
 */

public class Disaster {
    private String name;
    private DisasterItem[] disasterItems;
    private DisasterCategory category;
    private String description;
    private int id;
    private String[] tips;
    private String imageResource;

    public Disaster(String disasterName, DisasterItem[] disasterDisasterItems,
                    DisasterCategory disasterCategory, int disasterId,
                    String disasterDescription, String[] disasterTips, String disasterImageResource) {
        name = disasterName;
        this.disasterItems = disasterDisasterItems;
        category = disasterCategory;
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

    public DisasterCategory getCategory() {
        return category;
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

    public String getImageResource() {
        return imageResource;
    }
}
