package com.ss.sitesight;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by AJ on 6/7/2017.
 */

public class User {

    private String mID;
    private String mName;
    private String mEmail;
    private String mPassword;
    private int mUploads;
    private int mPoints;
    private Double mLongitude;
    private Double mLatitude;
    private Boolean mTrekMode;
    private List<String> mMarkerIds = new ArrayList<String>();

    private static User mUser = null;

    public static String COOKIE = "";

    private String mTargetMarkerId;

    public static User getInstance() {
        if (mUser == null) {
            mUser = new User();
        }
        return mUser;
    }

    private User() {
        mID = "";
        mName = "";
        mEmail = "";
        mPassword = "";
        mUploads = 0;
        mPoints = 0;
        mLongitude = 0.0;
        mLatitude = 0.0;
        mTrekMode = false;
        mTargetMarkerId = "";
    }

    public Boolean getTrekMode(){
        return mTrekMode;
    }


    public void setTrekMode(Boolean trekMode){
        mTrekMode = trekMode;
    }
    public void setTarget(String marker_id){
        mTargetMarkerId = marker_id;
    }

    public String getTargetMarker(){
        return mTargetMarkerId;
    }

    public void addDiscoveredMarkerId(String markerId){
        mMarkerIds.add(markerId);
    }

    public List getDiscoveredMarkerIds(){
        return mMarkerIds;
    }

    public String getName() {
        return mName;
    }

    public void setName(String name) {
        mName = name;
    }

    public String getEmail() {
        return mEmail;
    }

    public void setEmail(String email) {
        mEmail = email;
    }

    public String getPassword() {
        return mPassword;
    }

    public void setPassword(String password) {
        mPassword = password;
    }

    public void setID(String id) {
        mID = id;
    }

    public String getID() {
        return mID;
    }

    public void setNumUploads(int uploads) {
        mUploads = uploads;
    }

    public int getNumUploads() {
        return mUploads;
    }

    public void setPoints(int points) { mPoints = points; }

    public void incrementPoints(){
        mPoints += 1;
    }

    public int getPoints() { return mPoints; }

    public void setLatitude(Double latitude) { mLatitude = latitude; }

    public Double getLatitude() { return mLatitude; }

    public void setLongitude(Double longitude) { mLongitude = longitude; }

    public Double getLongitude() { return mLongitude; }


    public JSONObject getJSON() throws JSONException {
        JSONObject res = new JSONObject();
        res.put("name", mName);
        res.put("email", mEmail);
        res.put("password", mPassword);

        return res;
    }

}
