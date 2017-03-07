package uk.ac.coventry.m206cde.tutorial3.group5.disasterzone;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

public class DisasterInformationActivity extends AppCompatActivity {

    private String TAG = this.getClass().getSimpleName();

    private DisasterZoneApplication application;
    private DisasterDatabase database;

    private Disaster disaster;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_disaster_information);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        application = DisasterZoneApplication.getInstance();
        database = application.getDatabase();

        disaster = application.getCurrentDisaster();

        if (disaster == null) { // No reason to be here
            Intent intent = new Intent(this, DisasterListActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
        } else {
            setupPage();
        }
    }

    public void showItems() {
        Log.v(TAG, "Showing items");
    }

    public void setupPage() {
        getSupportActionBar().setTitle(disaster.getName());

        ((TextView) findViewById(R.id.disaster_description_text))
                .setText(disaster.getDescription());

        LinearLayout tipsList = (LinearLayout) findViewById(R.id.disaster_tips_layout);
        tipsList.removeAllViews();

        for (String tip: disaster.getTips()) {
            TextView tipText = new TextView(this);
            tipText.setText(getString(R.string.tip_bullet_point, tip));
            tipsList.addView(tipText);
        }

        findViewById(R.id.view_items_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showItems();
            }
        });
    }
}