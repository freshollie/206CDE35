package uk.ac.coventry.m206cde.tutorial3.group5.disasterzone;

import android.content.Intent;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
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
            application.goHome();
        } else {
            setupPage();
        }
    }

    public void showItems() {
        Log.v(TAG, "Showing items");
        startActivity(new Intent(this, DisasterItemsActivity.class));
    }

    public void setupPage() {
        getSupportActionBar().setTitle(disaster.getName());

        String stringResId = disaster.getImageResource();
        if (!stringResId.isEmpty()) {
            if (!stringResId.contains(".")) {
                int resource =
                        DisasterZoneApplication.getRawIdFromString(
                                getApplicationContext(),
                                stringResId
                        );

                if (resource != -1){
                    ((ImageView) findViewById(R.id.disaster_information_image)).setImageResource(resource);
                }
            }
        }


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
