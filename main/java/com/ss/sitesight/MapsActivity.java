package com.ss.sitesight;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.GroundOverlay;
import com.google.android.gms.maps.model.GroundOverlayOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.maps.android.clustering.Cluster;
import com.google.maps.android.clustering.ClusterItem;
import com.google.maps.android.clustering.ClusterManager;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.ref.WeakReference;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, OnTaskCompleted {
    private GoogleMap mMap;
    private String result = "";
    private List<Marker> mMarkers = new ArrayList<Marker>();
    private HashMap<String, MarkerInfo> markerHM = new HashMap<String, MarkerInfo>();
    private Trek mTrek;
    private ListView mDrawerList;
    private ArrayAdapter<String> mAdapter;
    private DrawerLayout mDrawerLayout;
    private RelativeLayout mDrawerContent;

    private ClusterManager<MarkerInfo> mClusterManager;

    private Button mCancelButton;
    private Button mCreateSiteButton;

    GroundOverlay mGroundOverlay;

    public void onBackPressed() {
        // your code.
       if(User.getInstance().getEmail().length() > 0){
           Intent startMain = new Intent(Intent.ACTION_MAIN);
           startMain.addCategory(Intent.CATEGORY_HOME);
           startMain.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
           startActivity(startMain);
       }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        mTrek = new Trek();  // initialzie trek

        mDrawerList = (ListView)findViewById(R.id.navList);
        mAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1);
        mDrawerList.setAdapter(mAdapter);
        View v = getLayoutInflater().inflate(R.layout.content_logout_button, null);
        mDrawerList.addFooterView(v);

        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerContent = (RelativeLayout) findViewById(R.id.navListLayout);
        mDrawerLayout.addDrawerListener(new DrawerLayout.DrawerListener() {
            @Override
            public void onDrawerSlide(View drawerView, float slideOffset) {
                updateDrawerItems();
            }

            @Override
            public void onDrawerOpened(View drawerView) {
                mDrawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);
            }

            @Override
            public void onDrawerClosed(View drawerView) {
                mDrawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
            }

            @Override
            public void onDrawerStateChanged(int newState) {

            }
        });

        mDrawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);

        mCreateSiteButton = (Button) findViewById(R.id.button_create_site);
        mCancelButton = (Button) findViewById(R.id.button_cancel);
        mCancelButton.setVisibility(View.GONE);
    }

    private void updateDrawerItems() {
        mAdapter.clear();
        String[] profileArray = { "Name: " + User.getInstance().getName(), "Email: " + User.getInstance().getEmail(), "Number of Uploads: " + User.getInstance().getNumUploads(), "Points Received: " + User.getInstance().getPoints() };
        mAdapter.addAll(profileArray);
        mAdapter.notifyDataSetChanged();
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

        new RequestGetUserInfo().execute();

        mMap = googleMap;

        mMap.getUiSettings().setMapToolbarEnabled(false);

        setUpClusterer(mMap);
//        mMap.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {

        mMap.setInfoWindowAdapter(mClusterManager.getMarkerManager());

        mClusterManager.getMarkerCollection().setOnInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {
            // Use default InfoWindow frame
            @Override
            public View getInfoWindow(Marker arg0) {
                return null;
            }

            // Defines the contents of the InfoWindow
            @Override
            public View getInfoContents(Marker arg0) {
                // Getting view from the layout file info_window_layout
                View v = getLayoutInflater().inflate(R.layout.windowlayout, null);

                // Getting the position from the marker
                LatLng latLng = arg0.getPosition();

                TextView tvImg = (TextView) v.findViewById(R.id.iw_image);

                // Getting reference to the TextView to set latitude
                TextView tvLat = (TextView) v.findViewById(R.id.tv_lat);

                // Getting reference to the TextView to set longitude
                TextView tvLng = (TextView) v.findViewById(R.id.tv_lng);

                MarkerInfo m = markerHM.get(arg0.getSnippet());

                tvImg.setText("Image:" + arg0.getSnippet());
                tvLat.setText("Title : " + m.getTitle());

                // Setting the longitude
                tvLng.setText("Description : "+ m.getDescription());

                System.out.println("MARKER NAME : " + m.getImageName());

                if (!User.getInstance().getTrekMode()) {
                    drawCircle(latLng, m.getRadius());
                }

                ImageView image = (ImageView) v.findViewById(R.id.imageView);

                if (image != null) {
                    Picasso.with(getApplicationContext())
                            .load(Settings.SERVER_URL + "/api/v1/image/" + arg0.getSnippet())
                            .into(image, new InfoWindowRefresher(arg0, image, m));
                }

                // Returning the view containing InfoWindow contents
                return v;

            }
        });

        mMap.setOnInfoWindowCloseListener(new GoogleMap.OnInfoWindowCloseListener() {
            @Override
            public void onInfoWindowClose(Marker marker) {
                if (!User.getInstance().getTrekMode()) {
                    mGroundOverlay.remove();
                }
            }
        });

        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION ) != PackageManager.PERMISSION_GRANTED ) {
            ActivityCompat.requestPermissions(this, new String[] { android.Manifest.permission.ACCESS_COARSE_LOCATION  },
                    Settings.PERMISSION_ACCESS_COURSE_LOCATION);
        }

        mMap.setMyLocationEnabled(true);

        LocationManager lm = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
        Criteria criteria = new Criteria();

        Location location = lm.getLastKnownLocation(lm.getBestProvider(criteria, false));
        if (location != null)
        {
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(location.getLatitude()+ 0.005, location.getLongitude()), 13));

            CameraPosition cameraPosition = new CameraPosition.Builder()
                    .target(new LatLng(location.getLatitude(), location.getLongitude()))      // Sets the center of the map to location user
                    .zoom(13)                   // Sets the zoom
                    .bearing(0)                // Sets the orientation of the camera to north
                    .tilt(0)                   // Sets the tilt of the camera to 0 degrees
                    .build();                   // Creates a CameraPosition from the builder
            mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
        }
        lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 0, new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                // set lat, lng to user
                User.getInstance().setLatitude(location.getLatitude());
                User.getInstance().setLongitude(location.getLongitude());
                String markerId = User.getInstance().getTargetMarker();

                if (User.getInstance().getTrekMode()) {
                    System.out.println("Location : " + location.toString());
//                    MarkerInfo mi = markerHM.get(markerId);
//                    double radius = mi.getRadius();
                    mMap.moveCamera(CameraUpdateFactory.newLatLng(new LatLng(location.getLatitude(), location.getLongitude())));
                    mTrek.setPos(new LatLng(location.getLatitude(), location.getLongitude()));

                    mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(location.getLatitude(), location.getLongitude()), getZoomLevel(mTrek.distance()) ));

                    if (mTrek.checkCompletion()) {
                        //current marker disapears setVisible(false)
                        //add marker id to users found markers array
                        System.out.println("COMPLETED!!!!");
                        Toast.makeText(MapsActivity.this,"Congratulations, you did it! +1 point!", Toast.LENGTH_LONG).show();
                        if(markerId.length() > 0){
                            System.out.println("markerId = " + markerId);
                            MarkerInfo mi = markerHM.get(markerId);
                            String siteId = mi.getID();
//                            trekCompleteShowMarkers(markerId);
                            User.getInstance().incrementPoints();
                            ServerRequests sr = new ServerRequests();
                            sr.updateVisitedSites(siteId);
                            
                            sr.incrementPoints(1, new OnTaskCompleted(){
                                @Override
                                public void onTaskCompleted() {
                                    String markerId = User.getInstance().getTargetMarker();

                                    trekCompleteShowMarkersItems(markerId);
                                    User.getInstance().setTrekMode(false);
                                    //Display back all the markers
                                    mCreateSiteButton.setVisibility(View.VISIBLE);
                                    mCancelButton.setVisibility(View.GONE);
                                }
                            });
                        }

                    }
                }
            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {

            }

            @Override
            public void onProviderEnabled(String provider) {

            }

            @Override
            public void onProviderDisabled(String provider) {

            }
        });

        new RequestGetMarkers().execute("/api/v1/markers");


        // check if in trek mode
        // if in trek mode hide all markers that aren't the one that you are looking for
        // begin polling location and checking if marker is found
        // if found set marker as found and add points to users
        // reset trek mode flag and refresh maps view

        //testing set dest to marker in vic

        mMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
            @Override
            public void onInfoWindowClick(Marker marker) {
                // start new intent to show detailed info..
                // TODO : ANDREW XU START NEW INTENT HERE AND WORK ON YOUR PART
                if(!User.getInstance().getTrekMode()){
    //                ServerRequests sr = new ServerRequests();
    //                sr.incrementPoints(4);
                    MarkerInfo mi = markerHM.get(marker.getSnippet());
    //                sr.putVisitedSite(mi.getID());
    //                sr.incrementPoints(mi.getUploadID(), 6);

                    System.out.println("TODO : start new intent");
                    Intent intent1 = new Intent(MapsActivity.this, SiteDetailsActivity.class);

                    Bundle args = new Bundle();

                    args.putParcelable("bitmap", mi.getBitmap());

    //                String uplder = mi.getUploadID();
                    intent1.putExtra("bundle", args);
                    intent1.putExtra("Title", mi.getTitle());
                    intent1.putExtra("description", mi.getDescription());
                    intent1.putExtra("marker_id", marker.getSnippet());
                    intent1.putExtra("uploaderID",mi.getUploadID());






                    startActivity(intent1);
                    // TESTING ... REMOVE
                    //mTrek.setDest(marker.getPosition());
                }
            }
        });


    }


    private void setUpClusterer(GoogleMap map) {
        if(map != null){
            mClusterManager = new ClusterManager<MarkerInfo>(this, map);

            map.setOnCameraIdleListener(mClusterManager);
            map.setOnMarkerClickListener(mClusterManager);
            mClusterManager.setRenderer(new ClusterMarkerIcon(getApplicationContext(), map, mClusterManager));

            mClusterManager.setOnClusterClickListener(new ClusterManager.OnClusterClickListener<MarkerInfo>() {
                        @Override
                        public boolean onClusterClick(final Cluster<MarkerInfo> cluster) {
                            LatLngBounds.Builder builder = LatLngBounds.builder();
                            for (ClusterItem item : cluster.getItems()) {
                                builder.include(item.getPosition());
                            }
                            final LatLngBounds bounds = builder.build();
                            mMap.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds, 100));

                            return true;
                        }
                    });

    //        mClusterManager.setAlgorithm(new GridBasedAlgorithm<MarkerInfo>());
        }
    }

    public int getZoomLevel(double radius) {
        System.out.println("radius" );
        System.out.println(radius );

        int zoomLevel = 0;
        if (radius > 0.0){
            double scale = radius*1000 / 500;
            zoomLevel =(int) (16 - Math.log(scale) / Math.log(2));
        }
        return zoomLevel;
    }

    public void drawCircle(LatLng latLng, int radiusM){
        int d = 500; // diameter
        Bitmap bm = Bitmap.createBitmap(d, d, Bitmap.Config.ARGB_8888);
        Canvas c = new Canvas(bm);
        Paint p = new Paint();
        p.setColor(Color.BLUE);
        c.drawCircle(d/2, d/2, d/2, p);
        // generate BitmapDescriptor from circle Bitmap
        BitmapDescriptor bmD = BitmapDescriptorFactory.fromBitmap(bm);

        // mapView is the GoogleMap
        mGroundOverlay = mMap.addGroundOverlay(new GroundOverlayOptions().
                image(bmD).
                position(latLng, radiusM*2, radiusM*2).
                transparency(0.4f));
    }

    public void navigateToUserProfile(View view){
//        mDrawerLayout.openDrawer(mDrawerList);
        mDrawerLayout.openDrawer(mDrawerContent);
        //Intent intent = new Intent(this, UserProfileActivity.class);
        //startActivity(intent);
    }

    public void navigateToCameraView(View view){
        Intent intent = new Intent(this, SiteCreationActivity.class);
        startActivity(intent);
    }

    public void navigateToLoginView(View view){
        User.getInstance().setTrekMode(false);
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
    }

    private void trekStartHideMarkers(String key){
        for(Marker marker: mMarkers){
            if(marker.getSnippet().equals(key)){
                marker.setVisible(true);
            }else{
                marker.setVisible(false);
            }
        }
    }

    private void trekStartHideMarkerItems(String key) {

        for (MarkerInfo mi : markerHM.values()) {
            if (!mi.getSnippet().equals(key)) {
                mClusterManager.removeItem(mi);
            } else {
                // show circle
                drawCircle(mi.getPosition(), mi.getRadius());
            }
        }
    }

    private void trekCompleteShowMarkers(){
        mGroundOverlay.remove();
        mClusterManager.clearItems();
        for(MarkerInfo mi : markerHM.values()){
            mClusterManager.addItem(mi);
        }

        mClusterManager.cluster();
    }

    private void trekCompleteShowMarkersItems(String key){
        mGroundOverlay.remove();
        mClusterManager.clearItems();

        System.out.println("Size of the map: "+ markerHM.size());
        if (markerHM.get(key) != null) {
            markerHM.remove(key);
        }
        System.out.println("Size of the map: "+ markerHM.size());

        for(MarkerInfo mi : markerHM.values()){
            System.out.println("markerInfo "+ mi.getID());
            mClusterManager.addItem(mi);
        }

        mClusterManager.cluster();
    }

    @Override
    public void onTaskCompleted() {

    }


    private class RequestGetMarkers extends AsyncTask<String, Void, String> {

        @Override
        protected void onPreExecute(){
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            System.out.println("output is : " + result);

            try {
                mClusterManager.clearItems();
                JSONArray markers = new JSONArray(result);
                for (int i = 0; i < markers.length(); i++) {
                    System.out.println(i);

                    JSONObject mark = markers.getJSONObject(i);

                    Double latitude = mark.getDouble("latitude");
                    Double longitude = mark.getDouble("longitude");
                    Double circleLat = mark.getDouble("circle_lat");
                    Double circleLng = mark.getDouble("circle_lon");

                    Double circleLng2 = longitude;
                    Double circleLat2 = latitude;
                    String title = mark.getString("title");
                    String description = mark.getString("description");

                    int r = mark.getInt("radius");

                    System.out.println(longitude);
                    System.out.println(latitude);
                    System.out.println(r);

                    String imageLoc = mark.getString("picture");
                    String uploaderID = mark.getString("markedBy");
                    String markerID = mark.getString("_id");

                    String uploader = User.getInstance().getID();
                    int iconResource = uploaderID.equals(uploader) ? R.drawable.ic_marker_2 : R.drawable.ic_marker;

                    MarkerInfo m = uploaderID.equals(uploader) ? new MarkerInfo(markerID, latitude, longitude, circleLat2, circleLng2, r, imageLoc, uploaderID, iconResource) : new MarkerInfo(markerID, latitude, longitude, circleLat, circleLng, r, imageLoc, uploaderID, iconResource);

                    //MarkerInfo m = new MarkerInfo(markerID, latitude, longitude, circleLat, circleLng, r, imageLoc, uploaderID, iconResource);
                    m.setTitle(title);
                    m.setDescription(description);

                    MarkerInfo mi = markerHM.get(imageLoc);
                    if (mi == null) {
                        markerHM.put(imageLoc, m);
                    }


//                    Marker marker = mMap.addMarker(new MarkerOptions()
//                            .position(new LatLng(uploaderID.equals(uploader) ? latitude : circleLat, uploaderID.equals(uploader) ? longitude : circleLng))
//                            .title("test")
//                            .snippet(imageLoc)
//                            .icon(BitmapDescriptorFactory.fromResource(iconResource))
//                    );

                    if (!User.getInstance().getTrekMode()) {
                        mClusterManager.addItem(m);
                    }

                }
                mClusterManager.cluster();

                if(User.getInstance().getTrekMode()){
                    //trek mode enabled
                    String markerId = getIntent().getStringExtra("marker_id");// marker that you are looking for

                    User.getInstance().setTarget(markerId);

                    MarkerInfo mi = markerHM.get(markerId); // get marker info

//                    trekStartHideMarkers(markerId); //hide other markers
                    mClusterManager.addItem(mi);
                    trekStartHideMarkerItems(markerId);
                    mTrek.setDest(mi.getLatLng());

                    mCancelButton.setVisibility(View.VISIBLE);
                    mCreateSiteButton.setVisibility(View.GONE);

                }

            } catch(JSONException e) {
                e.printStackTrace();
            }

        }

        @Override
        protected String doInBackground(String... params) {
            try {
                final String url = Settings.SERVER_URL + params[0];  // server address
                URL serverUrl = new URL(url);
                HttpURLConnection con = (HttpURLConnection) serverUrl.openConnection();
                con.setRequestProperty("Cookie", User.COOKIE);
                con.setRequestMethod("GET");
                con.connect();  // Sending request to server

                BufferedReader bf = new BufferedReader(new InputStreamReader(con.getInputStream()));

                String value = bf.readLine();
                System.out.println("value = " + value);
                result = value;



            } catch (Exception e) {
                Log.e("MainActivity", e.getMessage(), e);
            }
            return result;
        }
    }


    private class RequestGetImage extends AsyncTask<String, Void, Bitmap> {

        private WeakReference<View> vRef;
        private WeakReference<Marker> mRef;

        public RequestGetImage(View v, Marker m) {
            vRef = new WeakReference<View>(v);
            mRef = new WeakReference<Marker>(m);
        }


        @Override
        protected void onPreExecute(){
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(Bitmap b) {
            super.onPostExecute(b);

            if (vRef != null && b != null) {
                View v = vRef.get();
                Marker m = mRef.get();
                if (v != null && m != null) {
                    TextView tvImg = (TextView)v.findViewById(R.id.iw_image);

                    tvImg.setText("Image:" + " IN ASYNC.. SETTING IMAGE");

                    ImageView iv = (ImageView)v.findViewById(R.id.imageView);
                    iv.setImageBitmap(b);

                    m.hideInfoWindow();
                    m.showInfoWindow();
                }

            }
        }

        @Override
        protected Bitmap doInBackground(String... params) {
            Bitmap bmp = null;
            try {
                System.out.println("GETTING IMAGE : " + params[0]);
                final String url = Settings.SERVER_URL + "/api/v1/image/" + params[0];  // server address
                URL serverUrl = new URL(url);
                //HttpURLConnection con = (HttpURLConnection) serverUrl.openConnection();
                //con.setRequestProperty("Cookie", User.COOKIE);
                //con.connect();  // Sending request to server

                InputStream inputStream = serverUrl.openStream();
                Bitmap bitmap = BitmapFactory.decodeStream(inputStream);

                bmp = bitmap;

            } catch (Exception e) {
                Log.e("MainActivity", e.getMessage(), e);
            }

            return bmp;
        }
    }


    private class RequestGetUserInfo extends AsyncTask<String, Void, String> {

        @Override
        protected void onPreExecute(){
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            System.out.println("output is : " + s);

            try {
                JSONObject user = new JSONObject(s);

                String id = user.getString("_id");
                String name = user.getString("name");
                int num_uploads = user.getInt("num_uploads");
                int points = user.getInt("points");

                User.getInstance().setName(name);
                User.getInstance().setID(id);
                User.getInstance().setNumUploads(num_uploads);
                User.getInstance().setPoints(points);

            } catch(JSONException e) {
                e.printStackTrace();
            }

        }

        @Override
        protected String doInBackground(String... params) {
            String res = "";
            try {

                final String url = Settings.SERVER_URL + "/api/v1/users/" + User.getInstance().getEmail();  // server address
                URL serverUrl = new URL(url);
                HttpURLConnection con = (HttpURLConnection) serverUrl.openConnection();
                con.setRequestProperty("Cookie", User.COOKIE);
                con.setRequestMethod("GET");
                con.connect();  // Sending request to server

                BufferedReader bf = new BufferedReader(new InputStreamReader(con.getInputStream()));

                String value = bf.readLine();
                System.out.println("value = " + value);
                res = value;
            } catch (Exception e) {
                Log.e("MainActivity", e.getMessage(), e);
            }
            return res;
        }
    }

    public void cancelTrekMode(View view){
        User.getInstance().setTrekMode(false);
        trekCompleteShowMarkers(); // Show all markers
        mCancelButton.setVisibility(View.GONE);
        mCreateSiteButton.setVisibility(View.VISIBLE);
    }

}