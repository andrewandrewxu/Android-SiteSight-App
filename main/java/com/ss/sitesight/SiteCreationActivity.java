package com.ss.sitesight;

import android.Manifest;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Base64;
import android.view.MotionEvent;
import android.view.View;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.VideoView;

import org.json.JSONException;
import org.json.JSONObject;


public class SiteCreationActivity extends AppCompatActivity {

    private static final int ACTION_TAKE_PHOTO_B = 1;
    private static final int ACTION_TAKE_PHOTO_S = 2;
    private static final int ACTION_TAKE_VIDEO = 3;

    private static final String BITMAP_STORAGE_KEY = "viewbitmap";
    private static final String IMAGEVIEW_VISIBILITY_STORAGE_KEY = "imageviewvisibility";
    private static final int MY_PERMISSIONS_REQUEST_WRITE_CALENDARS = 1;
    private static final int PERMISSION_ACCESS_CAMERA = 2;
    private ImageView mImageView;
    private Bitmap mImageBitmap;
    private EditText mTitleTextView;
    private EditText mDescriptionTextView;
    private EditText mRadiusTextView;
    private int mRadius;
    private static String CHECK_IMAGE = "";

    private Button mCreateBtn;

    private static final String VIDEO_STORAGE_KEY = "viewvideo";
    private static final String VIDEOVIEW_VISIBILITY_STORAGE_KEY = "videoviewvisibility";
    private VideoView mVideoView;
    private Uri mVideoUri;

    private String mCurrentPhotoPath;

    private static final String JPEG_FILE_PREFIX = "IMG_";
    private static final String JPEG_FILE_SUFFIX = ".jpg";

    private AlbumStorageDirFactory mAlbumStorageDirFactory = null;

    /* Photo album for this application */
    private String getAlbumName() {
        return getString(R.string.album_name);
    }


    private File getAlbumDir() {
        File storageDir = null;

        if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {

            storageDir = mAlbumStorageDirFactory.getAlbumStorageDir(getAlbumName());

            if (storageDir != null) {
                if (! storageDir.mkdirs()) {
                    if (! storageDir.exists()){
                        Log.d("CameraSample", "failed to create directory");
                        return null;
                    }
                }
            }

        } else {
            Log.v(getString(R.string.app_name), "External storage is not mounted READ/WRITE.");
        }

        return storageDir;
    }

    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = JPEG_FILE_PREFIX + timeStamp + "_";
        File albumF = getAlbumDir();
        File imageF = File.createTempFile(imageFileName, JPEG_FILE_SUFFIX, albumF);
        return imageF;
    }

    private File setUpPhotoFile() throws IOException {

        File f = createImageFile();
        mCurrentPhotoPath = f.getAbsolutePath();

        return f;
    }

    private void setPic() {
		/* Associate the Bitmap to the ImageView */
		if(mImageBitmap != null){
            mImageView.setImageBitmap(mImageBitmap);
            mImageView.setVisibility(View.VISIBLE);
        }
    }

    private void galleryAddPic() {
        Intent mediaScanIntent = new Intent("android.intent.action.MEDIA_SCANNER_SCAN_FILE");
        File f = new File(mCurrentPhotoPath);
        Uri contentUri = Uri.fromFile(f);
        mediaScanIntent.setData(contentUri);
        this.sendBroadcast(mediaScanIntent);
    }

    private void requestCameraPermission(){
        System.out.println("Here, thisActivity is the current activity");
        System.out.println(ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_CALENDAR));
        System.out.println(PackageManager.PERMISSION_GRANTED);

        // Here, thisActivity is the current activity
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_CALENDAR)
                != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.WRITE_CALENDAR)) {

                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
                System.out.println("Show an explanation to the user *asynchronously* -- don't block");

            } else {

                // No explanation needed, we can request the permission.
                System.out.println("No explanation needed, we can request the permission.");
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.WRITE_CALENDAR},
                        MY_PERMISSIONS_REQUEST_WRITE_CALENDARS);

                // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                // app-defined int constant. The callback method gets the
                // result of the request.
            }
        }
    }
    /* Dispatch take picture intent on actionCode*/
    private void dispatchTakePictureIntent(int actionCode) {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Assume thisActivity is the current activity
        //int permissionCheck = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_CALENDAR);

        switch(actionCode) {
            case ACTION_TAKE_PHOTO_B:
                File f = null;
                mCurrentPhotoPath = null;
//                 try {
//                     f = setUpPhotoFile();
//                     mCurrentPhotoPath = f.getAbsolutePath();
//                     System.out.println("mCurrentPhotoPath" + mCurrentPhotoPath);
//                     takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(f));
//                 } catch (IOException e) {
//                     e.printStackTrace();
//                     f = null;
//                     mCurrentPhotoPath = null;
//                 }
                break;
            default:
                break;
        }

        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.CAMERA ) != PackageManager.PERMISSION_GRANTED ) {
            ActivityCompat.requestPermissions(this, new String[] { android.Manifest.permission.CAMERA  }, PERMISSION_ACCESS_CAMERA);
        } else {
            startActivityForResult(takePictureIntent, actionCode);
        }
        // switch
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_ACCESS_CAMERA: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    dispatchTakePictureIntent(ACTION_TAKE_PHOTO_B);
                } else {
                    // request denied. go back to maps screen
                    startActivity(new Intent(this, MapsActivity.class));
                }
                return;
            }
            // other 'case' lines to check for other
            // permissions this app might request
        }
    }

    private void dispatchImportImageIntent() {
        Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
        photoPickerIntent.setType("image/*");
        startActivityForResult(photoPickerIntent, 1);
    }

    // convert from bitmap to byte array
    public byte[] getBytesFromBitmap(Bitmap bitmap) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 10, stream);
        return stream.toByteArray();
    }

    private void handleSmallCameraPhoto(Intent intent) {
        Bundle extras = intent.getExtras();
        mImageBitmap = (Bitmap) extras.get("data");
        mImageView.setImageBitmap(mImageBitmap);
        mVideoUri = null;
        mImageView.setVisibility(View.VISIBLE);
        mVideoView.setVisibility(View.INVISIBLE);

        // Saving photo on result
    }

    private JSONObject getPostRequestObject(Bitmap bitmap){
        JSONObject request = new JSONObject();

        try {
            String base64Str = "";
            request.put("latitude", Double.toString(User.getInstance().getLatitude()));
            request.put("longitude", Double.toString(User.getInstance().getLongitude()));
            request.put("title", mTitleTextView.getText().toString());
            request.put("description", mDescriptionTextView.getText().toString());
//            request.put("r", "20");
            request.put("r", mRadius);

            base64Str = Base64.encodeToString(getBytesFromBitmap(bitmap),  Base64.NO_WRAP);
            CHECK_IMAGE = base64Str;
            System.out.println("LLLLLLLLLLLLLLLL" + CHECK_IMAGE);
            System.out.println("TTTTTTTTTTTTTTTT"+ base64Str);

            request.put("image", base64Str);
            //System.out.println(request.toString());

        } catch (JSONException e) {
            // TODO Auto-generated catch block
            System.out.println(e);
        }
        return request;
    }

    public void dispatchCreateSiteRequest(){
        try {

            JSONObject postRequest = getPostRequestObject(mImageBitmap);

            new RequestAddMarker().execute(postRequest);

            Intent intent1 = new Intent(SiteCreationActivity.this, MapsActivity.class);
            //intent1.putExtra("image", byteArray);
            startActivity(intent1);

        }catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void handleBigCameraPhoto(Intent intent) {
        Bundle extras = intent.getExtras();
        //Write file
        mImageBitmap = (Bitmap) extras.get("data");

        if(mImageBitmap == null){
            Intent startMaps = new Intent(this, MapsActivity.class);

            startActivity(startMaps);
        }else{
            setPic();
        }
    }
    //Declaration
    private class GenericTextWatcher implements TextWatcher {

        private View view;
        private GenericTextWatcher(View view) {
            this.view = view;
        }

        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            mCreateBtn.setEnabled(false);

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            String tmpTitle = mTitleTextView.getText().toString();
            String tmpDescription = mDescriptionTextView.getText().toString();
            if(tmpTitle.length()==0 || tmpDescription.length()==0 || mImageBitmap==null){
                //CHECK_IMAGE = null;
                mCreateBtn.setEnabled(false);
            } else {
                //CHECK_IMAGE = null;
                mCreateBtn.setEnabled(true);
            }
        }

        public void afterTextChanged(Editable editable) {
            String text = editable.toString();

            if(text.length() == 0 ){
                mCreateBtn.setEnabled(false);
            }else{
                System.out.println(text);
            }


        }
    }

    Button.OnClickListener mCreateSiteOnClickListener =
            new Button.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dispatchCreateSiteRequest();
                }
            };

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_site_creation);

        mImageView = (ImageView) findViewById(R.id.imageView1);

        mImageView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View arg0, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN: {
                        dispatchTakePictureIntent(ACTION_TAKE_PHOTO_B);
                        break;
                    }
                    case MotionEvent.ACTION_UP:
                    case MotionEvent.ACTION_CANCEL: {
                        break;
                    }
                }

                return false;

            }
        });
        mTitleTextView = (EditText) findViewById(R.id.editTextTitle);
        mDescriptionTextView = (EditText) findViewById(R.id.editTextDes);
        mRadiusTextView = (EditText) findViewById(R.id.radiusText);

        mTitleTextView.addTextChangedListener(new GenericTextWatcher(mTitleTextView));
        mDescriptionTextView.addTextChangedListener(new GenericTextWatcher(mDescriptionTextView));


        mCreateBtn = (Button) findViewById(R.id.btnCreateSite);
        mCreateBtn.setEnabled(false);

        setBtnListenerOrDisable(
                mCreateBtn,
                mCreateSiteOnClickListener,
                MediaStore.ACTION_IMAGE_CAPTURE
        );

        mRadius = 10;
        final SeekBar sk=(SeekBar) findViewById(R.id.seekBar);
        sk.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

                                          @Override
                                          public void onStopTrackingTouch(SeekBar seekBar) {
                                              // TODO Auto-generated method stub
                                          }

                                          @Override
                                          public void onStartTrackingTouch(SeekBar seekBar) {
                                              // TODO Auto-generated method stub
                                          }

                                          @Override
                                          public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                                              // TODO Auto-generated method stub
                                              mRadius = (int)(10 + (progress) * 20);
                                              if (mRadius > 500) mRadius = 500;
                                              mRadiusTextView.setText("Radius : " + mRadius + " meters");

                                          }
                                      }
            );

            dispatchTakePictureIntent(ACTION_TAKE_PHOTO_B);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case ACTION_TAKE_PHOTO_B: {
                if (resultCode == RESULT_OK) {
                    handleBigCameraPhoto(data);
                }else if( resultCode == RESULT_CANCELED){
                    if(mImageBitmap == null){
                        Intent startMaps = new Intent(this, MapsActivity.class);
                        startActivity(startMaps);
                    }
                }
                break;
            } // ACTION_TAKE_PHOTO_B

        } // switch
    }

    // Some lifecycle callbacks so that the image can survive orientation change
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putParcelable(BITMAP_STORAGE_KEY, mImageBitmap);
        outState.putParcelable(VIDEO_STORAGE_KEY, mVideoUri);
        outState.putBoolean(IMAGEVIEW_VISIBILITY_STORAGE_KEY, (mImageBitmap != null) );
        outState.putBoolean(VIDEOVIEW_VISIBILITY_STORAGE_KEY, (mVideoUri != null) );
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        mImageBitmap = savedInstanceState.getParcelable(BITMAP_STORAGE_KEY);
        mVideoUri = savedInstanceState.getParcelable(VIDEO_STORAGE_KEY);
        mImageView.setImageBitmap(mImageBitmap);
        mImageView.setVisibility(
                savedInstanceState.getBoolean(IMAGEVIEW_VISIBILITY_STORAGE_KEY) ?
                        ImageView.VISIBLE : ImageView.INVISIBLE
        );
        mVideoView.setVideoURI(mVideoUri);
        mVideoView.setVisibility(
                savedInstanceState.getBoolean(VIDEOVIEW_VISIBILITY_STORAGE_KEY) ?
                        ImageView.VISIBLE : ImageView.INVISIBLE
        );
    }

    /**
     * Indicates whether the specified action can be used as an intent. This
     * method queries the package manager for installed packages that can
     * respond to an intent with the specified action. If no suitable package is
     * found, this method returns false.
     * http://android-developers.blogspot.com/2009/01/can-i-use-this-intent.html
     *
     * @param context The application's environment.
     * @param action The Intent action to check for availability.
     *
     * @return True if an Intent with the specified action can be sent and
     *         responded to, false otherwise.
     */
    public static boolean isIntentAvailable(Context context, String action) {
        final PackageManager packageManager = context.getPackageManager();
        final Intent intent = new Intent(action);
        List<ResolveInfo> list =
                packageManager.queryIntentActivities(intent,
                        PackageManager.MATCH_DEFAULT_ONLY);
        return list.size() > 0;
    }

    private void setBtnListenerOrDisable(
            Button btn,
            Button.OnClickListener onClickListener,
            String intentName
    ) {
        if (isIntentAvailable(this, intentName)) {
            btn.setOnClickListener(onClickListener);
        } else {
            btn.setText(
                    getText(R.string.cannot).toString() + " " + btn.getText());
            btn.setClickable(false);
        }
    }

    public static String encodeURIComponent(String s) {
        String result;

        try {
            result = URLEncoder.encode(s, "UTF-8")
                    .replaceAll("\\+", "%20")
                    .replaceAll("\\%21", "!")
                    .replaceAll("\\%27", "'")
                    .replaceAll("\\%28", "(")
                    .replaceAll("\\%29", ")")
                    .replaceAll("\\%7E", "~");
        } catch (UnsupportedEncodingException e) {
            result = s;
        }

        return result;
    }

    private class RequestAddMarker extends AsyncTask<JSONObject, Boolean, Boolean  > {

        @Override
        protected void onPreExecute(){
            super.onPreExecute();
        }

        @Override
        protected Boolean doInBackground(JSONObject... jsonObj) {
            Boolean result = false;

            try {
                final String url = Settings.SERVER_URL + "/api/v1/addmarker";  // server address
                URL serverUrl = new URL(url);

                HttpURLConnection con = (HttpURLConnection) serverUrl.openConnection();

                con.setDoOutput(true);
                con.setDoInput(true);
                con.setRequestProperty("Cookie", User.COOKIE);
                con.setRequestProperty("Accept", "application/json");
                con.setRequestProperty("Content-type", "application/json");
                con.setRequestMethod("POST");
                con.connect();

                //Write
                OutputStreamWriter wr = new OutputStreamWriter(con.getOutputStream());
                System.out.println(jsonObj[0].toString());
                wr.write(jsonObj[0].toString());
                wr.flush();
                wr.close();

                //display what returns the POST request
                StringBuilder sb = new StringBuilder();
                int HttpResult = con.getResponseCode();
                if (HttpResult == HttpURLConnection.HTTP_CREATED) {
                    BufferedReader br = new BufferedReader(new InputStreamReader(con.getInputStream(), "utf-8"));
                    String line;
                    while ((line = br.readLine()) != null) {
                        sb.append(line + "\n");
                    }
                    br.close();
                    System.out.println("server response OK : " + sb.toString());
                    result = true;
                } else {
                    System.out.println("server response BAD : " + con.getResponseMessage());
                    result = false;
                }

            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            //                      makeRequest(WebServiceConstants.getMethodUrl(WebServiceConstants.METHOD_UPDATEVENDER), jsonObj.toString());
            return result;
        }

        @Override
        protected void onPostExecute(Boolean result) {
            if(result) {
                Intent intent1 = new Intent(SiteCreationActivity.this, MapsActivity.class);
                intent1.putExtra("path", mCurrentPhotoPath);

                startActivity(intent1);

            }else{
                System.out.println("failed");
            }

        }
    }

}