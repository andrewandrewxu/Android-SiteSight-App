package com.ss.sitesight;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.ImageView.ScaleType;

import com.squareup.picasso.Picasso;

public class SiteDetailsActivity extends AppCompatActivity {

    private String mMarkerId;

    public void startTrek(View view){
        //TODO: do trek stuff here

        //pass the marker image name back to maps activity


        // set trek flag
        User.getInstance().setTrekMode(true);

        Intent intent = new Intent(this, MapsActivity.class);
        intent.putExtra("marker_id", mMarkerId);
        startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //String title = extras.getString("markertitle");
//        byte[] byteArray = getIntent().getByteArrayExtra("image");
//        Bitmap bitmap = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length);
//

        // Getting view from the layout file activity_user_profile
        View v = getLayoutInflater().inflate(R.layout.activity_site_details, null);

        Bundle bundle = getIntent().getParcelableExtra("bundle");
        Bitmap bitmap = bundle.getParcelable("bitmap");

        String description = getIntent().getStringExtra("description");
        mMarkerId = getIntent().getStringExtra("marker_id");
        String title = getIntent().getStringExtra("Title");

        TextView titleView = (TextView) v.findViewById(R.id.Title);
        TextView descriptionView = (TextView) v.findViewById(R.id.description);

        titleView.setText(title);
        descriptionView.setText(description);

       final ImageView site_image = (ImageView) v.findViewById(R.id.imageViewSiteDetails);

        if(bitmap != null){
            site_image.setImageBitmap(bitmap);
            site_image.setScaleType(ScaleType.FIT_XY);
            //iv.setScaleType(ScaleType.FIT_XY);
        } else {
            Picasso.with(getApplicationContext())
                    .load(Settings.SERVER_URL + "/api/v1/image/" + mMarkerId)
                    .into(site_image);
        }

        //Bundle bdle = getIntent().getParcelableExtra("bundle");
        String uploader = getIntent().getStringExtra("uploaderID");

        String CurrentID = User.getInstance().getID();
        System.out.println("UPLOADER+++++++++" + uploader);
        System.out.println("UPLOADER+++++++++" + CurrentID);
        if (uploader.equals(CurrentID)){
            Button submit = (Button) findViewById(R.id.button4);
            submit.setEnabled(false);
        }


//        // Getting reference to the TextView to set profile email

        //TextView Site_coordinates = (TextView) v.findViewById(R.id.Set_coordinates);

        // Getting reference to the TextView to set profile number of uploads
        //TextView Set_nameofPic = (TextView) v.findViewById(R.id.Set_nameofPic);
//        TextView Set_Description = (TextView) v.findViewById(R.id.Set_Description);
//        TextView Set_SearchTime = (TextView) v.findViewById(R.id.Set_SearchTime);
//        TextView Set_Name = (TextView) v.findViewById(R.id.Set_Name);


//        Set_Name.setText(title);

//        // Setting the profile name
//        Site_Latitude.setText("Latitude: " );
//        Site_Longitude.setText("Longitude: " + "13");
//        Site_PicName.setText("Treasure Name: " + "23");

        //setContentView(R.layout.activity_site_creation);
        setContentView(v);
//        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
//        setSupportActionBar(toolbar);
        //FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
//        fab.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
//            }
//        });
//        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

}
