package uk.ac.coventry.m206cde.tutorial3.group5.disasterzone;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;

public class DisasterCategoriesActivity extends AppCompatActivity implements DisasterDatabase.DatabaseChangeListener {
    private DisasterZoneApplication application;
    private DisasterCategoriesAdapter adapter;
    private static String TAG = DisasterCategoriesActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_disaster_categories);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        application = (DisasterZoneApplication) getApplication();

        application.setFilterCategory(null);

        setupPage();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.categories_menu, menu);
        return true;
    }

    private void setupPage() {
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.categories_recycler_view);

        // Make a grid of 2 items per row
        recyclerView.setLayoutManager(new GridLayoutManager(this, 2));

        adapter = new DisasterCategoriesAdapter(this);
        adapter.setCategories(application.getDatabase().getDisasterCategories());
        recyclerView.setAdapter(adapter);

    }

    public void onCategoryClicked(int categoryId) {
        Log.v(TAG, "Category selected: " + String.valueOf(categoryId));
        application.setFilterCategory(application.getDatabase().getCategoryFromId(categoryId));
        startActivity(new Intent(this, DisasterListActivity.class));
    }

    @Override
    public void onDatabaseChanged() {
        if (adapter != null) {
            adapter.setCategories(application.getDatabase().getDisasterCategories());
        }
    }
}
