package uk.ac.coventry.m206cde.tutorial3.group5.disasterzone;

import android.graphics.Color;

/**
 * Created by Freshollie on 13/03/2017.
 */

public class DisasterCategory {
    private int id;
    private String text;
    private String imageResource;
    private String colour;

    public DisasterCategory(int categoryId, String categoryText,
                            String categoryImageResource, String categoryColour) {
        id = categoryId;
        text = categoryText;
        imageResource = categoryImageResource;
        colour = categoryColour;
    }

    public int getId() {
        return id;
    }

    public String getText() {
        return text;
    }

    public String getImageResource() {
        return imageResource;
    }

    public int getColour() {
        return Color.parseColor(colour);
    }
}
