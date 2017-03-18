package uk.ac.coventry.m206cde.tutorial3.group5.disasterzone;

import android.app.Application;
import android.content.Intent;

import java.lang.reflect.Field;

/**
 * Created by Freshollie on 02/03/2017.
 */

public class DisasterZoneApplication extends Application{
    private DisasterDatabase disasterDatabase;

    private Disaster currentDisaster;
    private DisasterItem[] currentDisasterItems = new DisasterItem[0];

    private static DisasterZoneApplication INSTANCE;

    @Override
    public void onCreate() {
        super.onCreate();
        disasterDatabase = new DisasterDatabase(getApplicationContext());
        INSTANCE = this;
    }

    public DisasterDatabase getDatabase() {
        return disasterDatabase;
    }

    public Disaster getCurrentDisaster() {
        return currentDisaster;
    }

    public DisasterItem[] getCurrentDisasterItems() {
        return currentDisasterItems;
    }

    public void setCurrentDisaster(Disaster disaster) {
        currentDisaster = disaster;
    }

    public void setCurrentDisasterItems(DisasterItem[] disasterItems) {
        currentDisasterItems = disasterItems;
    }

    public void clearCurrentItems() {
        currentDisasterItems = new DisasterItem[]{};
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
        Intent intent = new Intent(this, DisasterCategoriesActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }

    public static int getResId(String resName, Class<?> c) {
        try {
            Field idField = c.getDeclaredField(resName);
            return idField.getInt(idField);
        } catch (Exception e) {
            e.printStackTrace();
            return -1;
        }
    }
}
