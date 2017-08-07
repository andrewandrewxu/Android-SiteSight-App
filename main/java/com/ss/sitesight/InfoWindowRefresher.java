package com.ss.sitesight;

import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.widget.ImageView;

import com.google.android.gms.maps.model.Marker;
import com.squareup.picasso.Callback;

/**
 * Created by AJ on 6/29/2017.
 */

public class InfoWindowRefresher implements Callback {

    private Marker marker;
    private ImageView im;
    private MarkerInfo markerInfo;

    public InfoWindowRefresher(Marker m, ImageView image, MarkerInfo mi) {
        this.marker = m;
        this.im = image;
        markerInfo = mi;
    }

    @Override
    public void onSuccess() {
        if (marker != null && marker.isInfoWindowShown()) {
            marker.hideInfoWindow();
            marker.showInfoWindow();

            if(im != null){
                Bitmap bm =((BitmapDrawable)im.getDrawable()).getBitmap();
                markerInfo.setBitmap(bm);
            }

        }
    }

    @Override
    public void onError() {

    }
}
