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

    //Used to store the objects by their ids
    private SparseArray<Disaster> disasters = new SparseArray<>();
    private SparseArray<Item> items = new SparseArray<>();

    // Keeps object as singleton that all activities can access with the same data

    // Don't perform another download if a download is already going;
    private boolean downloading = false;

    // Used to notify when the database has been updated from a download
    public interface DatabaseChangeListener {
        void onDatabaseChanged();
    }

    private ArrayList<DatabaseChangeListener> databaseChangeListeners = new ArrayList<>();


    public DisasterDatabase() {

    }

    private SharedPreferences getSharedPreferences(Context context) {
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
     * @param context
     * @return
     */
    private String getStringJsonDatabase(Context context, boolean forceRaw) {
        SharedPreferences sharedPreferences = getSharedPreferences(context);

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

            saveNewJsonDatabase(context, stringJsonDatabase);
        }

        return stringJsonDatabase;
    }

    // Default is to not force to load raw
    private String getStringJsonDatabase(Context context) {
        return getStringJsonDatabase(context, false);
    }

    /**
     * Saves the given json string to SharedPreferences
     * @param context
     * @param newJsonDatabase
     */
    private void saveNewJsonDatabase(Context context, String newJsonDatabase) {
        SharedPreferences sharedPreferences = getSharedPreferences(context);
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
        final String localStringJsonDatabase = getStringJsonDatabase(context);

        new DatabaseDownloaderService().execute(new DatabaseDownloaderService.DownloadCallback() {
            @Override
            public void onStarted() {

            }

            @Override
            public void onComplete(String newStringJsonDatabase) {
                if (!newStringJsonDatabase.equals(localStringJsonDatabase)) {
                    saveNewJsonDatabase(context, newStringJsonDatabase);

                    if (!loadDatabase(context, false)) {
                        // if the new database is not valid we need to set it back to the old
                        saveNewJsonDatabase(context, localStringJsonDatabase);
                    }
                }
            }
        });
    }

    /**
     * Loads the locally stored database into objects;
     *
     * @param context
     * @return true if loaded properly false if not
     */
    public boolean loadDatabase(Context context, boolean tryRaw) {
        String stringJsonDatabase = getStringJsonDatabase(context);

        // Process of converting that json string into objects
        for (int x = 0; x < 2; x++) {
            try {
                JSONObject jsonDatabase = new JSONObject(stringJsonDatabase);
                JSONArray jsonItems = jsonDatabase.getJSONArray("items"); // Get the items
                JSONArray jsonDisasters = jsonDatabase.getJSONArray("disasters"); // get the disasters

                items = new SparseArray<>();

                for (int i = 0; i < jsonItems.length(); i++) { // Go through all the items
                    JSONObject jsonItem = jsonItems.getJSONObject(i);

                    JSONArray jsonArrayItemLocations = jsonItem.getJSONArray("locations");

                    String[] itemLocations = new String[jsonArrayItemLocations.length()];

                    for (int j = 0; i < itemLocations.length; i++) {
                        // Each items location needs to be stored
                        itemLocations[j] = jsonArrayItemLocations.getString(j);
                    }

                    // Create an object from that information
                    Item item = new Item(
                            jsonItem.getInt("id"),
                            jsonItem.getString("name"),
                            jsonItem.getString("price"),
                            itemLocations
                    );

                    items.put(item.getId(), item);
                }


                disasters = new SparseArray<>();

                for (int i = 0; i < jsonDisasters.length(); i++) { // Go through all the disasters
                    JSONObject jsonDisaster = jsonDisasters.getJSONObject(i);

                    JSONArray jsonDisasterItemIds = jsonDisaster.getJSONArray("items");

                    JSONArray jsonDisasterTips = jsonDisaster.getJSONArray("tips");

                    // Store the integer value of those items
                    int[] disasterItems = new int[jsonDisasterItemIds.length()];
                    for (int j = 0; j < jsonDisasterItemIds.length(); j++) {
                        disasterItems[j] = jsonDisasterItemIds.getInt(j);
                    }

                    String[] disasterTips = new String[jsonDisasterTips.length()];
                    for (int j = 0; j < jsonDisasterTips.length(); j++) {
                        disasterTips[j] = jsonDisasterTips.getString(j);
                    }

                    disasters.put(jsonDisaster.getInt("id"),
                            new Disaster(
                                    jsonDisaster.getString("name"),
                                    disasterItems,
                                    jsonDisaster.getString("type"),
                                    jsonDisaster.getInt("id"),
                                    jsonDisaster.getString("description"),
                                    disasterTips
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
                    stringJsonDatabase = getStringJsonDatabase(context, true);
                } else {
                    break;
                }
            }
        }
        Log.e(TAG, "Database format error");
        return false;
    }

    public boolean loadDatabase(Context context) {
        return loadDatabase(context, true);
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

    public Item getItemFromId(int id) {
        return items.get(id);
    }

    public Item[] getItemsForDisaster(Disaster disaster) {
        int[] itemIds = disaster.getItems();

        Item[] itemArray = new Item[itemIds.length];

        for(int i = 0; i < itemIds.length; i++) {
            itemArray[i] = items.get(itemIds[i]);
        }

        return itemArray;
    }

    public Item[] getItemsForDisasterId(int id) {
        Disaster disaster = getDisasterFromId(id);

        return getItemsForDisaster(disaster);
    }

    public boolean isLoaded() {
        return disasters != null && disasters.size() > 0
                && items != null && items.size() > 0;
    }
}
