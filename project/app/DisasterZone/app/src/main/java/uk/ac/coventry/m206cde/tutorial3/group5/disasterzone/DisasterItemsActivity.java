package uk.ac.coventry.m206cde.tutorial3.group5.disasterzone;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;

import java.util.ArrayList;

public class DisasterItemsActivity extends AppCompatActivity {

    private DisasterZoneApplication application;
    private Disaster disaster;
    private DisasterDatabase database;
    private ArrayList<DisasterItem> checkedDisasterItems = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        application = DisasterZoneApplication.getInstance();

        setContentView(R.layout.activity_disaster_items_activity);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        disaster = application.getCurrentDisaster();
        database = application.getDatabase();

        if (disaster != null) {
            getSupportActionBar().setTitle(application.getCurrentDisaster().getName());
            setupPage();
        } else {
            application.goHome();
        }
    }



    public void showMap() {
        DisasterItem[] disasterItems = checkedDisasterItems.toArray(new DisasterItem[checkedDisasterItems.size()]);
        if (disasterItems.length > 0) {
            if (!application.isNetworkAvailable()) {
                new AlertDialog.Builder(this)
                        .setTitle(R.string.no_internet_title)
                        .setMessage(R.string.no_internet_message)
                        .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                            }
                        })
                        .show();
            } else {
                application.setCurrentDisasterItems(disasterItems);
                startActivity(new Intent(this, ItemsMapActivity.class));
            }
        } else {
            Snackbar.make(
                    findViewById(R.id.content_disaster_items_activity),
                    R.string.no_items_selected,
                    Snackbar.LENGTH_LONG)
                    .show();
        }
    }

    public void setupPage() {
        LinearLayout itemLayout = (LinearLayout) findViewById(R.id.essentials_list);
        itemLayout.removeAllViews();

        for (final DisasterItem disasterItem : disaster.getDisasterItems()) {
            CheckBox checkBox = new CheckBox(this);
            checkBox.setText(disasterItem.getName());

            checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if (isChecked) {
                        checkedDisasterItems.add(disasterItem);
                    } else {
                        checkedDisasterItems.remove(disasterItem);
                    }
                }
            });

            itemLayout.addView(checkBox);
        }

        findViewById(R.id.location_fab).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showMap();
            }
        });
    }

}
