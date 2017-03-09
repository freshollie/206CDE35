package uk.ac.coventry.m206cde.tutorial3.group5.disasterzone;

import android.app.Application;
import android.content.Intent;

/**
 * Created by Freshollie on 02/03/2017.
 */

public class DisasterZoneApplication extends Application{
    private DisasterDatabase disasterDatabase;

    private Disaster currentDisaster;
    private Item[] currentItems = new Item[0];

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

    public Item[] getCurrentItems() {
        return currentItems;
    }

    public void setCurrentDisaster(Disaster disaster) {
        currentDisaster = disaster;
    }

    public void setCurrentItems(Item[] items) {
        currentItems = items;
    }

    public void clearCurrentItems() {
        currentItems = new Item[]{};
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

    public void goHome() {
        Intent intent = new Intent(this, DisasterListActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }
}
