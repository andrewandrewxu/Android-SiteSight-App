package com.ss.sitesight;

import android.content.Context;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.maps.android.clustering.ClusterManager;
import com.google.maps.android.clustering.view.DefaultClusterRenderer;

/**
 * Created by AJ on 7/20/2017.
 */

public class ClusterMarkerIcon extends DefaultClusterRenderer<MarkerInfo> {

    public ClusterMarkerIcon(Context context, GoogleMap map, ClusterManager<MarkerInfo> clusterManager) {
        super(context, map, clusterManager);
    }

    @Override
    protected void onBeforeClusterItemRendered(MarkerInfo item, MarkerOptions markerOptions) {
        markerOptions.icon(BitmapDescriptorFactory.fromResource(item.getIcon()));
        markerOptions.snippet(item.getSnippet());
        markerOptions.title(item.getTitle());
        super.onBeforeClusterItemRendered(item, markerOptions);
    }
}
