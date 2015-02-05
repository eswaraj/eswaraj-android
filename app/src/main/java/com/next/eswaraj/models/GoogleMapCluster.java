package com.next.eswaraj.models;

import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.clustering.ClusterItem;


public class GoogleMapCluster implements ClusterItem {

        private final LatLng mPosition;

        public GoogleMapCluster(double lat, double lng) {
            mPosition = new LatLng(lat, lng);
        }

        @Override
        public LatLng getPosition() {
            return mPosition;
        }
}
