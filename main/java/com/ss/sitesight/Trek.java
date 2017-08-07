package com.ss.sitesight;

import com.google.android.gms.maps.model.LatLng;

/**
 * Created by AJ on 7/2/2017.
 */

public class Trek {

    private LatLng mDest;
    private LatLng mCurrentPos;
    private double mOffsetTolerance;

    public Trek() {
        // mOffsetTolerance = 0.005; //km
        mOffsetTolerance = 5; //km
        mDest = new LatLng(0, 0);
        mCurrentPos = new LatLng(0, 0);
    }

    public void setDest(LatLng dest) {
        mDest = dest;
    }

    public void setPos(LatLng pos) {
        mCurrentPos = pos;
    }

    public LatLng getPos() {return mCurrentPos;}

    public boolean checkCompletion() {
        double dist = distance(); //km
        System.out.println("DISTANCE : " + dist + " | TOLERANCE : " + mOffsetTolerance);

        return (distance() < mOffsetTolerance);
    }

    public double distance() {
        double lon1 = mCurrentPos.longitude;
        double lon2 = mDest.longitude;
        double lat1 = mCurrentPos.latitude;
        double lat2 = mDest.latitude;

        double theta = lon1 - lon2;
        double dist = Math.sin(Math.toRadians(lat1)) * Math.sin(Math.toRadians(lat2)) + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) * Math.cos(Math.toRadians(theta));
        dist = Math.acos(dist);
        dist = Math.toDegrees(dist);
        dist = dist * 60 * 1.1515;
        return (dist * 1.609344);
    }


}
