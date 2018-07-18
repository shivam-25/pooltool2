package com.example.android.pooltool2.Helper;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.example.android.pooltool2.R;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Marker;

import org.w3c.dom.Text;

public class CustomInfoWindow implements GoogleMap.InfoWindowAdapter {

    View myView;

    public CustomInfoWindow(Context context) {
        myView = LayoutInflater.from(context)
                 .inflate(R.layout.custom_rider_info_window,null);
    }

    @Override
    public View getInfoWindow(Marker marker) {
        TextView textPickupTitle = ((TextView)myView.findViewById(R.id.txtPickupInfo));
        textPickupTitle.setText(marker.getTitle());

        TextView textPickupSnippet = ((TextView)myView.findViewById(R.id.txtPickupInfo));
        textPickupSnippet.setText(marker.getTitle());

        return myView;
    }

    @Override
    public View getInfoContents(Marker marker) {
        return null;
    }
}
