package uk.ac.coventry.m206cde.tutorial3.group5.disasterzone;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.SparseArray;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

/**
 * Created by freshollie on 2/13/17.
 */

public class DisasterDatabase {
    private SparseArray<Disaster> disasters = new SparseArray<>();
    private SparseArray<Item> items = new SparseArray<>();

    private static DisasterDatabase INSTANCE = new DisasterDatabase();

    private boolean downloading = false;

    public interface DownloadCallback {
        void onStarted();
        void onComplete();
    }

    public static DisasterDatabase getInstance() {
        return INSTANCE;
    }

    private DisasterDatabase() {

    }

    private Item getItemFromId(int id) {
        return items.get(id);
    }

    private SharedPreferences getSharedPreferences(Context context) {
        return context.getSharedPreferences(
                        context.getString(R.string.PREFERENCES_KEY),
                        Context.MODE_PRIVATE
                );
    }

    /** Starts the database download task
     *
     * @param context
     */
    public void downloadDatabase(Context context, DownloadCallback callback) {
        //todo
        // Probably an async task that downloads the database and
        // then loads them into this instance
        SharedPreferences sharedPreferences = getSharedPreferences(context);
    }

    /**
     * Returns true if the database is loaded from local and false if it needs to be downloaded
     * @param context
     * @return
     */
    public boolean loadDatabase(Context context) {
        SharedPreferences sharedPreferences = getSharedPreferences(context);

        String stringJsonDatabase = sharedPreferences.getString(
                context.getString(R.string.SAVED_DATABASE_JSON),
                null
        );

        if (stringJsonDatabase == null) {
            // database is not saved so we need to inform that we need to download
            return false;
        }

        try {
            JSONObject jsonDatabase = new JSONObject(stringJsonDatabase);
            JSONArray jsonItems = jsonDatabase.getJSONArray("items");
            JSONArray jsonDisasters = jsonDatabase.getJSONArray("disasters");

            for (int i = 0; i < jsonItems.length(); i++) {
                JSONObject jsonItem = jsonItems.getJSONObject(i);

                JSONArray jsonArrayItemLocations = jsonItem.getJSONArray("locations");

                String[] itemLocations = new String[jsonArrayItemLocations.length()];

                for (int j = 0; i < itemLocations.length; i++) {
                    itemLocations[j] = jsonArrayItemLocations.getString(j);
                }

                Item item = new Item(
                        jsonItem.getInt("id"),
                        jsonItem.getString("name"),
                        jsonItem.getString("price"),
                        itemLocations
                );

                items.put(item.getId(), item);
            }


            disasters = new SparseArray<>();

            for (int i = 0;  i < jsonDisasters.length(); i++) {
                JSONObject jsonDisaster = jsonDisasters.getJSONObject(i);

                JSONArray jsonDisasterItemIds = jsonDisaster.getJSONArray("items");

                JSONArray jsonDisasterTips = jsonDisaster.getJSONArray("tips");

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


        } catch (JSONException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    public void setDatabase(SparseArray<Disaster> newDisasters) {
        disasters = newDisasters;
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

    public Disaster getDisaster(int id) {
        return disasters.get(id);
    }

    public Item getItem(int id) {
        return items.get(id);
    }

    public Item[] getItemsForDisaster(int id) {
        Disaster disaster = getDisaster(id);

        int[] itemIds = disaster.getItems();

        Item[] itemArray = new Item[itemIds.length];

        for(int i = 0; i < itemIds.length; i++) {
            itemArray[i] = items.get(itemIds[i]);
        }

        return itemArray;
    }

    public boolean isLoaded() {
        return disasters != null && disasters.size() > 0
                && items != null && items.size() > 0;
    }
}
