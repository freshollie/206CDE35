package uk.ac.coventry.m206cde.tutorial3.group5.disasterzone;

/**
 * Created by Freshollie on 13/03/2017.
 */

public class DisasterCategory {
    private int id;
    private String name;
    private String imageResource;
    private String colour;

    public DisasterCategory(int categoryId, String categoryName,
                            String categoryImageResource, String categoryColour) {
        id = categoryId;
        name = categoryName;
        imageResource = imageResource;

    }
}
