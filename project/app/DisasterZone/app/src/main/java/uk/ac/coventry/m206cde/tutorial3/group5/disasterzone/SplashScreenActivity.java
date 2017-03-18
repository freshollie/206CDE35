package uk.ac.coventry.m206cde.tutorial3.group5.disasterzone;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

public class SplashScreenActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);
        findViewById(R.id.get_started_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((DisasterZoneApplication)getApplication()).goHome();
            }
        });

    }
}
