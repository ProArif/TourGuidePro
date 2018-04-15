package com.arifulislam.tourguidepro.adapters;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;


import com.arifulislam.tourguidepro.R;
import com.arifulislam.tourguidepro.locations.NearbyPlacesActivity;
import com.arifulislam.tourguidepro.responses.PlaceResponse;
import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by Ariful Islam on 07-Feb-18.
 */

public class PlaceAdapter extends BaseAdapter {

    private Context context;
    private List<PlaceResponse.Result> placeResponses;

    public PlaceAdapter(Context context, List<PlaceResponse.Result> placeResponses) {
        this.context = context;
        this.placeResponses = placeResponses;
    }
    @Override
    public int getCount() {
        return placeResponses.size();
    }

    @Override
    public Object getItem(int i) {
        return placeResponses.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            if (inflater!=null) {
                view = inflater.inflate(R.layout.nearby_places_custom, viewGroup, false);
                ImageView imageView = view.findViewById(R.id.place_image);
                TextView textView = view.findViewById(R.id.tvName);
                    textView.setText(placeResponses.get(i).getName());

                    try{
                        String ref = placeResponses.get(i).getPhotos().get(0).getPhotoReference();
                        String imageReference = NearbyPlacesActivity.BASE_URL + "photo?maxwidth=400&maxheight=400&photoreference=" + ref + "&key=AIzaSyCAArNkJGFxcOUDL7Ai-O3z-ENWjRQXTTI";
                        Uri uri = Uri.parse(imageReference);
                        Picasso.with(context).load(uri).into(imageView);

                    }catch(Exception e){


                }


            }

        return view;
    }
}
