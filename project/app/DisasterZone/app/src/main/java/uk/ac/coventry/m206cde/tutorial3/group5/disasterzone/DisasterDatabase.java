package uk.ac.coventry.m206cde.tutorial3.group5.disasterzone;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by freshollie on 2/13/17.
 */

public class DisasterDatabase {
    private Context context;
    private Disaster[];
    private static DisasterDatabase INSTANCE = new DisasterDatabase();

    public static void init(Context newContext) {
        INSTANCE.context = newContext;
    }

    private DisasterDatabase(){

    }

    public DisasterDatabase getInstance() {
        return INSTANCE;
    }

    private SharedPreferences getSharedPreferences() {
        return context.getSharedPreferences(
                        context.getString(R.string.PREFERENCES_KEY),
                        Context.MODE_PRIVATE
                );
    }

    public void downloadDatabase() {
        //todo
        SharedPreferences sharedPreferences = getSharedPreferences();
    }

    public boolean loadDatabase(Context context) {
        //Todo
        return true;
    }

    public void setDatabase(Disaster[] newDisasters) {
        //Todo

    }
}
