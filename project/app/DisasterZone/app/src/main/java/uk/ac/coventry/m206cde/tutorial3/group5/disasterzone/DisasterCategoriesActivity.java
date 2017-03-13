package uk.ac.coventry.m206cde.tutorial3.group5.disasterzone;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;

public class DisasterCategoriesActivity extends AppCompatActivity {
    private DisasterZoneApplication application;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_disaster_categories);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        application = (DisasterZoneApplication) getApplication();

        setupPage();
    }

    private void setupPage() {
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.categories_recycler_view);

        // Make a grid of 2 items per row
        recyclerView.setLayoutManager(new GridLayoutManager(this, 2));

        DisasterCategoriesAdapter adapter = new DisasterCategoriesAdapter(this);
        adapter.setCategories(application.getDatabase().getDisasterCategories());
        recyclerView.setAdapter(adapter);

    }

    public void onCategoryClicked(int categoryId) {

    }

}
