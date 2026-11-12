package com.online.flyer.design.postermaker.Leaflet_activities;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

/**
 * Forwarder activity to ensure backward compatibility.
 * Redirects directly to Leaflet_OnboardingStep1Activity.
 */
public class Leaflet_OnboardingActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = new Intent(this, Leaflet_OnboardingStep1Activity.class);
        if (getIntent() != null && getIntent().getExtras() != null) {
            intent.putExtras(getIntent().getExtras());
        }
        startActivity(intent);
        finish();
    }
}
