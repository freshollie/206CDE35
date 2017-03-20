package uk.ac.coventry.m206cde.tutorial3.group5.disasterzone;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import noman.googleplaces.NRPlaces;
import noman.googleplaces.Place;
import noman.googleplaces.PlacesException;
import noman.googleplaces.PlacesListener;

public class ItemsMapActivity extends AppCompatActivity implements OnMapReadyCallback,
        ActivityCompat.OnRequestPermissionsResultCallback, GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener {

    private static int MAX_PLACES_PER_ITEM = 8;

    private static final String TAG = ItemsMapActivity.class.getSimpleName();

    private DisasterZoneApplication application;

    private GoogleMap map;
    private GoogleApiClient googleApiClient;
    private Location location;

    private ArrayList<String> plottedPlaceIds = new ArrayList<>();
    private ArrayList<Marker> plottedMarkers = new ArrayList<>();
    private HashMap<String, Integer> itemNumStoresPlotted = new HashMap<>();
    private int queryId = 0;

    private ProgressBar progressBar;

    private boolean locationPermissionGranted = false;
    private boolean checkedLocationPermission = false;

    private final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 0;
    private final int PLACE_PICKER_REQUEST = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item_map);

        application = (DisasterZoneApplication) getApplication();

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(R.string.title_activity_item_map);

        progressBar = (ProgressBar) findViewById(R.id.toolbar_progress_bar);

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        findViewById(R.id.location_fab).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (location != null) {
                    goToMyLocation(true);
                }
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.item_map_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.action_set_location:
                showLocationPicker();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private Location getSavedLocation() {
        SharedPreferences sharedPreferences = getSharedPreferences(
                getString(R.string.PREFERENCES_KEY),
                Context.MODE_PRIVATE
        );

        if (sharedPreferences.contains(getString(R.string.LAST_PICKED_LOCATION_LAT))) {
            Location location = new Location("pickedLocation");
            location.setLatitude(
                    Double.parseDouble(
                            sharedPreferences.getString(
                                    getString(R.string.LAST_PICKED_LOCATION_LAT),
                                    "0"
                            )
                    )
            );

            location.setLongitude(
                    Double.parseDouble(
                            sharedPreferences.getString(
                                    getString(R.string.LAST_PICKED_LOCATION_LONG),
                                    "0"
                            )
                    )
            );

            return location;
        } else {
            return null;
        }
    }

    private void showLocationPicker() {
        try {
            Intent placePickerIntent = new PlacePicker.IntentBuilder().build(this);
            startActivityForResult(placePickerIntent, PLACE_PICKER_REQUEST);
        } catch (
                GooglePlayServicesRepairableException |
                        GooglePlayServicesNotAvailableException e
                ) {
            Toast.makeText(this, "Play services not installed", Toast.LENGTH_LONG).show();
        }
    }


    private void requestLocationPermission() {
        checkedLocationPermission = true;
        if (ContextCompat.checkSelfPermission(this.getApplicationContext(),
                android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            locationPermissionGranted = true;
        } else {
            ActivityCompat.requestPermissions(this,
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                    PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
            locationPermissionGranted = false;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String permissions[],
                                           @NonNull int[] grantResults) {
        switch (requestCode) {
            case PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 &&
                        permissions[0].equals(Manifest.permission.ACCESS_FINE_LOCATION) &&
                        grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    locationPermissionGranted = true;
                }
                populateMap();
            }
        }
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PLACE_PICKER_REQUEST) {
            if (resultCode == RESULT_OK) {

                LatLng latLng = PlacePicker.getPlace(this, data).getLatLng();

                location = new Location("pickerLocation");
                location.setLongitude(latLng.longitude);
                location.setLatitude(latLng.latitude);

                SharedPreferences.Editor editor =
                        getSharedPreferences(
                                getString(R.string.PREFERENCES_KEY),
                                Context.MODE_PRIVATE
                        ).edit();

                editor.putString(
                        getString(R.string.LAST_PICKED_LOCATION_LAT),
                        String.valueOf(latLng.latitude)
                );
                editor.putString(
                        getString(R.string.LAST_PICKED_LOCATION_LONG),
                        String.valueOf(latLng.longitude)
                );

                editor.apply();
                populateMap();
            }
        }
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        Log.v(TAG, "Map Loaded");
        map = googleMap;

        googleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();

        if (application.isNetworkAvailable()) {
            populateMap();
        } else {
            new AlertDialog.Builder(this)
                    .setTitle(R.string.no_internet_title)
                    .setMessage(R.string.no_internet_message)
                    .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            finish();
                        }
                    })
                    .show();

        }
    }

    private void goToMyLocation(boolean animate) {
        LatLng myLocation = new LatLng(location.getLatitude(), location.getLongitude());

        if (animate) {
            map.animateCamera(CameraUpdateFactory.newLatLngZoom(myLocation, 15));
        } else {
            map.moveCamera(CameraUpdateFactory.newLatLngZoom(myLocation, 15));
        }
    }

    private void populateMap() {
        Log.v(TAG, "Populating map");

        if (!checkedLocationPermission) {
            Log.v(TAG, "Checking if we have permission for location");
            requestLocationPermission();
            if (!locationPermissionGranted) {
                return; // We need to wait for a response;
            }
        }

        if (locationPermissionGranted && location == null) {
            Log.v(TAG, "We have location permission, and no location, checking gps location");
            //I do the check to make sure we have permission for location

            //noinspection MissingPermission
            location = LocationServices.FusedLocationApi.getLastLocation(googleApiClient);
        }

        if (location == null) {
            Log.v(TAG, "We can't find a GPS location");
            // We still don't have a location
            Log.v(TAG, "Checking a saved location");
            location = getSavedLocation();

            if (location == null) {
                Log.v(TAG, "We don't have a saved location, showing picker");
                // Never got a location, offer alternative location input;
                Toast.makeText(this, "No Device location", Toast.LENGTH_SHORT).show();
                showLocationPicker();
                return;
            } else {
                Log.v(TAG, "We have found a saved location");
            }
        }

        Log.v(TAG, "We have a permission to check with");

        HashSet<String> storeLocationsNames = new HashSet<>();

        for (DisasterItem item: application.getCurrentDisasterItems()) {
            storeLocationsNames.addAll(Arrays.asList(item.getLocationNames()));
        }

        Log.v(TAG, "Collected stores we need to check");
        Log.v(TAG, Arrays.toString(storeLocationsNames.toArray()));

        map.clear();

        LatLng myLocation = new LatLng(location.getLatitude(), location.getLongitude());
        map.addCircle(
                new CircleOptions()
                        .center(myLocation)
                        .radius(30.0)
                        .strokeColor(Color.RED)
                        .fillColor(Color.BLUE)
                        .strokeWidth(3));

        goToMyLocation(false);

        plottedMarkers.clear();
        plottedPlaceIds.clear();
        itemNumStoresPlotted.clear();
        queryId++;

        Log.v(TAG, "Finding store locations");

        final int[] numQueries = {0};

        for (final String queryName: storeLocationsNames) {
            new NRPlaces.Builder()
                    .listener(new PlacesListener() {
                        private int id = queryId;
                        private String query = queryName;
                        private boolean failed = false;

                        private int numPlaces = 0;

                        @Override
                        public void onPlacesFailure(PlacesException e) {
                            if (!failed) {
                                failed = true;
                                Log.v(TAG, "Failed");

                                if (e.getMessage().equals("ZERO_RESULTS")) {
                                    Log.v(TAG, "Found no results for: " + query);
                                } else {
                                    Log.v(TAG, e.getMessage());
                                    Log.v(TAG, e.getStatusCode());
                                }
                                onPlacesFinished();
                            }
                        }

                        @Override
                        public void onPlacesStart() {
                            Log.v(TAG, "Starting search for: " + query);
                            numQueries[0]++;
                            progressBar.setVisibility(View.VISIBLE);
                        }

                        @Override
                        public void onPlacesSuccess(final List<Place> places) {
                            Log.v(TAG, "Found places for: " + query);
                            if (numPlaces < 3) {
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        plotPlaces(places, query, id);
                                    }
                                });
                            }
                            numPlaces += places.size();
                        }

                        @Override
                        public void onPlacesFinished() {
                            numQueries[0]--;
                            if (numQueries[0] < 1) {
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        progressBar.setVisibility(View.GONE);
                                    }
                                });
                            }
                        }
                    })
                    .key(getString(R.string.google_maps_key))
                    .latlng(location.getLatitude(), location.getLongitude())
                    .rankby("distance")
                    .name(queryName)
                    .build()
                    .execute();
        }
    }

    private void plotPlace(Place place, String querySearchName) {
        Log.v(TAG, "Plotting place");
        DisasterItem[] items = application.getCurrentDisasterItems();

        ArrayList<String> storeItemNames = new ArrayList<>();

        boolean shouldPlot = true; //TODO:

        for (DisasterItem item: items) {
            for (String locationName : item.getLocationNames()) {
                if (querySearchName.equals(locationName)) {
                    storeItemNames.add(item.getName());
                    if (itemNumStoresPlotted.containsKey(item.getName())) {
                        if (itemNumStoresPlotted.get(item.getName()) < MAX_PLACES_PER_ITEM) {
                            // Makes sure that there are 2 stores per item maximum
                            shouldPlot = true;
                            itemNumStoresPlotted.put(
                                    item.getName(),
                                    itemNumStoresPlotted.get(item.getName()) + 1
                            );
                        }
                    } else {
                        itemNumStoresPlotted.put(
                                item.getName(),
                                1
                        );
                        shouldPlot = true;
                    }
                }
            }
        }

        if (!shouldPlot) {
            return;
        }

        plottedPlaceIds.add(place.getPlaceId());

        Log.v(TAG, place.getName() + ": " + Arrays.toString(storeItemNames.toArray()));

        String infoString = TextUtils.join(", ", storeItemNames);

        Log.v(TAG, infoString);

        LatLng placeLocation = new LatLng(place.getLatitude(), place.getLongitude());
        Marker marker = map.addMarker(new MarkerOptions().position(placeLocation).title(place.getName()));
        marker.setSnippet(infoString);

        plottedMarkers.add(marker);
        plottedMarkers.get(0).showInfoWindow();

        LatLngBounds.Builder builder = new LatLngBounds.Builder();
        for (Marker plottedMarker: plottedMarkers) {
            builder.include(plottedMarker.getPosition());
        }

        LatLngBounds bounds = builder.build();

        int padding = 100; // offset from edges of the map in pixels
        CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, padding);

        map.animateCamera(cu);
    }

    private void plotPlaces(List<Place> places, String querySearchName, int id) {
        if (id != queryId) { // Ignores old queries
            return;
        }

        int numPlaces = 0;
        for (Place place: places) {
            if (numPlaces < 3) {
                numPlaces++;
                if (!plottedPlaceIds.contains(place.getPlaceId())) {
                    plotPlace(place, querySearchName);
                }
            } else {
                return;
            }
        }
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }
}
