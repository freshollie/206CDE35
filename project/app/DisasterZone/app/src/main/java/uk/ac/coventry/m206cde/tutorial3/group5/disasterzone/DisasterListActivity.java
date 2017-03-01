package uk.ac.coventry.m206cde.tutorial3.group5.disasterzone;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.widget.Toast;

public class DisasterListActivity extends AppCompatActivity {

    private String TAG = this.getClass().getSimpleName();

    private DisasterDatabase disasterDatabase;
    private DisasterListAdapter disasterListAdapter;
    private RecyclerView disastersRecyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_disaster_list);

        setSupportActionBar(new Toolbar(this));

        disasterDatabase = DisasterDatabase.getInstance();

        if (!disasterDatabase.isLoaded()) {
            disasterDatabase.loadDatabase(this);
        }

        disasterListAdapter = new DisasterListAdapter(this);
        disasterListAdapter.setDisasters(disasterDatabase.getDisastersAsArray());

        disastersRecyclerView = (RecyclerView) findViewById(R.id.disaster_recycler_view);
        disastersRecyclerView.setAdapter(disasterListAdapter);
    }

    public void onItemClicked(int itemId) {
        Log.v(TAG, "Item clicked " + String.valueOf(itemId));
    }
}
