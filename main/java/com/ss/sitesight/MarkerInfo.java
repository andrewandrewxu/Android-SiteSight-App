package com.ss.sitesight;

import android.graphics.Bitmap;

import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.clustering.ClusterItem;

/**
 * Created by AJ on 7/1/2017.
 */

public class MarkerInfo implements ClusterItem {

    private LatLng mLatLng;
    private int mRadius;
    private String mImageName;
    private String mUploaderID;
    private String mID;
    private String mTitle;
    private String mDescription;
    private LatLng mCircleCenter;
    private Bitmap mBitmap;
    private Boolean mVisible;
    private String mSnippet;
    private int mIcon;

    public MarkerInfo(String id, double lat, double lng, double circleLat, double circleLng, int radius, String imgName, String uploaderID, int icon) {
        mID = id;
        mLatLng = new LatLng(lat, lng);
        mCircleCenter = new LatLng(circleLat, circleLng);
        mRadius = radius;
        mImageName = imgName;
        mUploaderID = uploaderID;
        mBitmap = null;
        mVisible = true;
        mSnippet = imgName;
        mIcon = icon;
    }

    public int getIcon() {
        return mIcon;
    }

    public void setVisible(Boolean visible){
        mVisible = visible;
    }

    public Boolean isVisible(){
        return mVisible;
    }

    public LatLng getLatLng() {
        return mLatLng;
    }

    public LatLng getCircleCenter() {
        return mCircleCenter;
    }

    public int getRadius() {
        return mRadius;
    }

    public String getImageName() {
        return mImageName;
    }

    @Override
    public LatLng getPosition() {
        if (isUploader()) {
            return mLatLng;
        } else {
            return mCircleCenter;
        }
//        return mLatLng;
    }

    @Override
    public String getTitle() {
        return mTitle;
    }

    @Override
    public String getSnippet() {
        return mSnippet;
    }

    public void setTitle(String title){
        mTitle = title;
    }

    public String getDescription(){
        return mDescription;
    }

    public void setDescription(String description){
        mDescription = description;
    }

    public String getUploadID() {
        return mUploaderID;
    }

    // checks if the id passed in is the uploader
    public boolean isUploader(String id) {
        return (mUploaderID.equals(id));
    }

    public boolean isUploader() {
        return (mUploaderID.equals(User.getInstance().getID()));
    }

    public String getID() {
        return mID;
    }

    public Bitmap getBitmap(){ return mBitmap;}

    public void setBitmap(Bitmap bitmap){ mBitmap = bitmap;}

}
