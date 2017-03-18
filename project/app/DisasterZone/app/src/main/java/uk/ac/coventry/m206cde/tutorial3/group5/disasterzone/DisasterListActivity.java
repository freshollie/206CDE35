package uk.ac.coventry.m206cde.tutorial3.group5.disasterzone;

import android.content.Intent;
import android.preference.PreferenceFragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.widget.Toast;

import java.util.ArrayList;

public class DisasterListActivity extends AppCompatActivity implements
        DisasterDatabase.DatabaseChangeListener{

    private String TAG = this.getClass().getSimpleName();

    private DisasterZoneApplication application;
    private DisasterDatabase disasterDatabase;
    private DisasterListAdapter disasterListAdapter;
    private RecyclerView disastersRecyclerView;
    private DisasterCategory filterCategory;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Log.v(TAG, "onCreate");
        setContentView(R.layout.activity_disaster_list);

        Toolbar toolbar = (Toolbar) findViewById(R.id.disaster_list_activity_toolbar);
        setSupportActionBar(toolbar);

        application = DisasterZoneApplication.getInstance();
        application.clearCurrentDisaster();
        application.clearCurrentItems();

        filterCategory = application.getFilterCategory();
        if (filterCategory != null) {
            getSupportActionBar().setTitle(filterCategory.getText());
        } else {
            getSupportActionBar().setTitle(getString(R.string.title_activity_disasters));
        }

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        disasterDatabase = application.getDatabase();
        disasterDatabase.registerDatabaseChangeListener(this);

        disasterListAdapter = new DisasterListAdapter(this);

        disastersRecyclerView = (RecyclerView) findViewById(R.id.disaster_recycler_view);
        disastersRecyclerView.setAdapter(disasterListAdapter);
        disastersRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        onDatabaseChanged();
    }

    public Disaster[] filterDisasters(Disaster[] disasters) {
        if (filterCategory == null) {
            return disasters;
        }

        ArrayList<Disaster> filteredDisasters = new ArrayList<>();
        for (Disaster disaster: disasters) {
            if (disaster.getCategory() == filterCategory) {
                filteredDisasters.add(disaster);
            }
        }

        return filteredDisasters.toArray(new Disaster[filteredDisasters.size()]);
    }

    @Override
    public void onDatabaseChanged() {
        Log.v(TAG, "Disasters database changed");
        disasterListAdapter.setDisasters(filterDisasters(disasterDatabase.getDisastersAsArray()));
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        disasterDatabase.unregisterDatabaseChangeListener(this);
    }

    public void onDisasterClicked(int disasterId) {
        Log.v(TAG, "DisasterItem clicked " + String.valueOf(disasterId));
        application.setCurrentDisaster(disasterDatabase.getDisasterFromId(disasterId));

        startActivity(new Intent(this, DisasterInformationActivity.class));
    }

}
