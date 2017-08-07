package com.ss.sitesight;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;

public class UserProfileActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Getting view from the layout file activity_user_profile
        View v = getLayoutInflater().inflate(R.layout.activity_user_profile, null);

        // Getting reference to the TextView to set profile name
        TextView prof_name = (TextView) v.findViewById(R.id.profile_name);

        // Getting reference to the TextView to set profile email
        TextView prof_email = (TextView) v.findViewById(R.id.profile_email);

        // Getting reference to the TextView to set profile number of uploads
        TextView prof_num_uploads = (TextView) v.findViewById(R.id.profile_num_uploads);

        // Getting reference to the TextView to set profile points
        TextView prof_points = (TextView) v.findViewById(R.id.profile_points);

        // Setting the profile name
        prof_name.setText("Name: " + User.getInstance().getName());

        // Setting the profile email
        prof_email.setText("Email: " + User.getInstance().getEmail());

        // Setting the profile's number of uploads
        prof_num_uploads.setText("Number of Uploads: " + User.getInstance().getNumUploads());

        // Setting the profile's points
        prof_points.setText("Points Received: " + User.getInstance().getPoints());

        setContentView(v);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

}
