package uk.ac.coventry.m206cde.tutorial3.group5.disasterzone;

import android.content.Intent;
import android.os.Bundle;
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
    private ArrayList<Item> checkedItems = new ArrayList<>();

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
        Item[] items = checkedItems.toArray(new Item[checkedItems.size()]);
        application.setCurrentItems(items);
        startActivity(new Intent(this, ItemsMapActivity.class));
    }

    public void setupPage() {
        LinearLayout itemLayout = (LinearLayout) findViewById(R.id.essentials_list);
        itemLayout.removeAllViews();

        for (final Item item: disaster.getItems()) {
            CheckBox checkBox = new CheckBox(this);
            checkBox.setText(item.getName());

            checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if (isChecked) {
                        checkedItems.add(item);
                    } else {
                        checkedItems.remove(item);
                    }
                }
            });

            itemLayout.addView(checkBox);
        }

        findViewById(R.id.view_stores_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showMap();
            }
        });
    }

}
