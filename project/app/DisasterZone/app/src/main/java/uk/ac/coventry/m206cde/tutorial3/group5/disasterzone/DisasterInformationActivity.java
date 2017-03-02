package uk.ac.coventry.m206cde.tutorial3.group5.disasterzone;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

public class DisasterInformationActivity extends AppCompatActivity {

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
            getSupportActionBar().setTitle(disaster.getName());
        }
    }
}
