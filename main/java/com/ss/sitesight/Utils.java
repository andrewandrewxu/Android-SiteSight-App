package com.ss.sitesight;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by AJ on 6/28/2017.
 */

public class Utils {

    public static Bitmap decodeRemoteBitmap(String urlString) throws MalformedURLException, IOException {
        URL url = new URL(urlString);
        InputStream inputStream = url.openStream();
        Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
        return bitmap;
    }
}
