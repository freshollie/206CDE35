package uk.ac.coventry.m206cde.tutorial3.group5.disasterzone;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import android.util.SparseArray;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringWriter;
import java.io.Writer;
import java.util.ArrayList;

/**
 * Created by freshollie on 2/13/17.
 */

public class DisasterDatabase {

    private static final String TAG = DisasterDatabase.class.getSimpleName();

    private Context context;

    //Used to store the objects by their ids
    private SparseArray<Disaster> disasters = new SparseArray<>();
    private SparseArray<DisasterItem> disasterItems = new SparseArray<>();
    private SparseArray<DisasterCategory> disasterCategories = new SparseArray<>();

    // Keeps object as singleton that all activities can access with the same data

    // Don't perform another download if a download is already going;
    private boolean downloading = false;

    // Used to notify when the database has been updated from a download
    public interface DatabaseChangeListener {
        void onDatabaseChanged();
    }

    private ArrayList<DatabaseChangeListener> databaseChangeListeners = new ArrayList<>();


    public DisasterDatabase(Context context) {
        this.context = context;
        loadDatabase();
    }

    private SharedPreferences getSharedPreferences() {
        return context.getSharedPreferences(
                        context.getString(R.string.PREFERENCES_KEY),
                        Context.MODE_PRIVATE
                );
    }

    /**
     * Retrieves a JSON copy of the database from either shared preferences or RAW,
     * depending on which is available.
     *
     * If shared preferences doesn't have the string stored it will save it after loading from raw.
     *
     * @param forceRaw Load the default database if no other shored DB
     * @return
     */
    private String getStringJsonDatabase(boolean forceRaw) {
        SharedPreferences sharedPreferences = getSharedPreferences();

        String stringJsonDatabase = sharedPreferences.getString(
                context.getString(R.string.SAVED_DATABASE_JSON),
                null
        );

        if (stringJsonDatabase == null || forceRaw) {
            // Only stored in RAW so load

            InputStream is = context.getResources().openRawResource(R.raw.disaster_database);
            Writer writer = new StringWriter();
            char[] buffer = new char[1024];
            try {
                Reader reader = new BufferedReader(new InputStreamReader(is, "UTF-8"));
                int n;
                while ((n = reader.read(buffer)) != -1) {
                    writer.write(buffer, 0, n);
                }
                is.close();
            } catch (IOException e) {
                e.printStackTrace();
            }

            stringJsonDatabase = writer.toString();

            saveNewJsonDatabase(stringJsonDatabase);
        }

        return stringJsonDatabase;
    }

    // Default is to not force to load raw
    private String getStringJsonDatabase() {
        return getStringJsonDatabase(false);
    }

    /**
     * Saves the given json string to SharedPreferences
     *
     * @param newJsonDatabase
     */
    private void saveNewJsonDatabase(String newJsonDatabase) {
        SharedPreferences sharedPreferences = getSharedPreferences();
        SharedPreferences.Editor editor = sharedPreferences.edit();

        editor.putString(context.getString(R.string.SAVED_DATABASE_JSON), newJsonDatabase);

        editor.apply();
    }

    /**
     * Refreshes the local copy of the database from the server
     *
     * @param context
     */
    public void downloadDatabase(final Context context) {
        final String localStringJsonDatabase = getStringJsonDatabase();

        new DatabaseDownloaderService().execute(new DatabaseDownloaderService.DownloadCallback() {
            @Override
            public void onStarted() {

            }

            @Override
            public void onComplete(String newStringJsonDatabase) {
                if (!newStringJsonDatabase.equals(localStringJsonDatabase)) {
                    saveNewJsonDatabase(newStringJsonDatabase);

                    if (!loadDatabase(false)) {
                        // if the new database is not valid we need to set it back to the old
                        saveNewJsonDatabase(localStringJsonDatabase);
                    }
                }
            }
        });
    }

    /**
     * Loads the locally stored database into objects;
     *
     * @return true if loaded properly false if not
     */
    public boolean loadDatabase(boolean tryRaw) {
        Log.v(TAG, "Loading database");
        String stringJsonDatabase = getStringJsonDatabase();

        // Process of converting that json string into objects
        for (int x = 0; x < 2; x++) {
            try {
                JSONObject jsonDatabase = new JSONObject(stringJsonDatabase);
                JSONArray jsonItems = jsonDatabase.getJSONArray("items"); // Get the items
                JSONArray jsonDisasters = jsonDatabase.getJSONArray("disasters"); // get the disasters
                JSONArray jsonCategories = jsonDatabase.getJSONArray("categories");


                disasterCategories = new SparseArray<>();

                for (int i = 0; i < jsonCategories.length(); i++) { // Go through all the disasterItems
                    JSONObject jsonCategory = jsonCategories.getJSONObject(i);

                    // Create an object from that information
                    DisasterCategory disasterCategory = new DisasterCategory(
                            jsonCategory.getInt("id"),
                            jsonCategory.getString("text"),
                            jsonCategory.getString("image"),
                            jsonCategory.getString("colour")
                    );
                    disasterCategories.put(disasterCategory.getId(), disasterCategory);
                }

                disasterItems = new SparseArray<>();

                for (int i = 0; i < jsonItems.length(); i++) { // Go through all the disasterItems
                    JSONObject jsonItem = jsonItems.getJSONObject(i);

                    JSONArray jsonArrayItemLocations = jsonItem.getJSONArray("locations");

                    String[] itemLocations = new String[jsonArrayItemLocations.length()];

                    for (int j = 0; j < itemLocations.length; j++) {
                        // Each disasterItems location needs to be stored
                        itemLocations[j] = jsonArrayItemLocations.getString(j).toLowerCase();
                    }

                    // Create an object from that information
                    DisasterItem disasterItem = new DisasterItem(
                            jsonItem.getInt("id"),
                            jsonItem.getString("name"),
                            jsonItem.getString("price"),
                            itemLocations
                    );
                    disasterItems.put(disasterItem.getId(), disasterItem);
                }


                disasters = new SparseArray<>();

                for (int i = 0; i < jsonDisasters.length(); i++) { // Go through all the disasters
                    JSONObject jsonDisaster = jsonDisasters.getJSONObject(i);

                    JSONArray jsonDisasterItemIds = jsonDisaster.getJSONArray("items");

                    JSONArray jsonDisasterTips = jsonDisaster.getJSONArray("tips");

                    // Store the integer value of those disasterItems
                    DisasterItem[] disasterDisasterItems = new DisasterItem[jsonDisasterItemIds.length()];
                    for (int j = 0; j < jsonDisasterItemIds.length(); j++) {
                        disasterDisasterItems[j] = getItemFromId(jsonDisasterItemIds.getInt(j));
                    }

                    String[] disasterTips = new String[jsonDisasterTips.length()];
                    for (int j = 0; j < jsonDisasterTips.length(); j++) {
                        disasterTips[j] = jsonDisasterTips.getString(j);
                    }

                    disasters.put(jsonDisaster.getInt("id"),
                            new Disaster(
                                    jsonDisaster.getString("name"),
                                    disasterDisasterItems,
                                    getDisasterCategory(jsonDisaster.getInt("category")),
                                    jsonDisaster.getInt("id"),
                                    jsonDisaster.getString("description"),
                                    disasterTips,
                                    jsonDisaster.getString("image")
                            )
                    );
                }

                Log.v(TAG, "Database loaded");
                notifyDatabaseChanged();
                return true;
            } catch (JSONException e) {
                e.printStackTrace();
                Log.e(TAG, "Error loading current database, trying the default database");
                if (tryRaw) {
                    stringJsonDatabase = getStringJsonDatabase(true);
                } else {
                    break;
                }
            }
        }
        Log.e(TAG, "Database format error");
        return false;
    }

    private DisasterCategory getDisasterCategory(int categoryId) {
        return disasterCategories.get(categoryId);
    }

    public DisasterCategory[] getDisasterCategories() {
        DisasterCategory[] arrayDisasterCategories =
                new DisasterCategory[disasterCategories.size()];


        for (int i = 0; i < disasterCategories.size(); i++) {
            arrayDisasterCategories[i] = disasterCategories.valueAt(i);
        }

        return arrayDisasterCategories;
    }

    public DisasterCategory getCategoryFromId(int categoryId) {
        return disasterCategories.get(categoryId);
    }

    public boolean loadDatabase() {
        return loadDatabase(true);
    }

    /**
     * Notify all listeners that the database has changed
     */
    private void notifyDatabaseChanged() {
        for (DatabaseChangeListener listener: databaseChangeListeners) {
            listener.onDatabaseChanged();
        }
    }

    public void registerDatabaseChangeListener(DatabaseChangeListener listener) {
        databaseChangeListeners.add(listener);
    }

    public void unregisterDatabaseChangeListener(DatabaseChangeListener listener) {
        databaseChangeListeners.remove(listener);
    }

    public SparseArray<Disaster> getDisasters() {
        return disasters;
    }

    /**
     * @return an array of disasters as apposed to a sparse array;
     */
    public Disaster[] getDisastersAsArray() {
        Disaster[] disastersArray = new Disaster[disasters.size()];

        for(int i = 0; i < disastersArray.length; i++) {
            disastersArray[i] = disasters.get(disasters.keyAt(i));
        }

        return disastersArray;
    }

    public Disaster getDisasterFromId(int id) {
        return disasters.get(id);
    }

    public DisasterItem getItemFromId(int id) {
        return disasterItems.get(id);
    }

    public DisasterItem[] getItemsForStore(String store) {
        ArrayList<DisasterItem> items = new ArrayList<>();

        for (int i = 0; i < disasterItems.size(); i++) {
            items.add(disasterItems.valueAt(i));
        }

        return items.toArray(new DisasterItem[items.size()]);
    }
}
