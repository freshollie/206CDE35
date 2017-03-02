package uk.ac.coventry.m206cde.tutorial3.group5.disasterzone;

import android.app.Application;

/**
 * Created by Freshollie on 02/03/2017.
 */

public class DisasterZoneApplication extends Application{
    private DisasterDatabase disasterDatabase;

    private Disaster currentDisaster;
    private Item currentItem;

    private static DisasterZoneApplication INSTANCE;

    @Override
    public void onCreate() {
        disasterDatabase = new DisasterDatabase();
        INSTANCE = this;
    }

    public DisasterDatabase getDatabase() {
        return disasterDatabase;
    }

    public Disaster getCurrentDisaster() {
        return currentDisaster;
    }

    public Item getCurrentItem() {
        return currentItem;
    }

    public void setCurrentDisaster(Disaster disaster) {
        currentDisaster = disaster;
    }

    public void setCurrentItem(Item item) {
        currentItem = item;
    }

    public void clearCurrentItem() {
        currentItem = null;
    }

    public void clearCurrentDisaster() {
        currentDisaster = null;
    }

    public static DisasterZoneApplication getInstance() {
        return INSTANCE;
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
    }
}
