package com.arifulislam.tourguidepro.locations;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.arifulislam.tourguidepro.R;
import com.arifulislam.tourguidepro.events.EventActivity;
import com.arifulislam.tourguidepro.responses.DirectionResponse;
import com.arifulislam.tourguidepro.services.DirectionService;
import com.arifulislam.tourguidepro.services.PendingIntentService;
import com.arifulislam.tourguidepro.weather.WeatherActivity;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingClient;
import com.google.android.gms.location.GeofencingRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.AutocompleteFilter;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocomplete;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMapOptions;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.maps.android.clustering.ClusterManager;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback,View.OnClickListener{
    private GoogleMap mMap;
    private GoogleMapOptions options;
    public static final String DIRECTION_BASE_URL = "https://maps.googleapis.com/maps/api/directions/";

    private DirectionService mDirectionService;

    private ImageButton searchButton;
    private ImageButton nextRouteButton;
    private ImageButton directionButton;
    private ImageButton walkingButton;
    private ImageButton drivingButton;
    private Button mapViewButton;
    private List<MarkerItems> clusterItems;
    private ClusterManager<MarkerItems> mClusterManager;
    private MarkerOptions ExtraMarker;
    private ProgressDialog mProgressDialog;
    private LatLng mLatLng;
    private final int PLACE_PICKER_REQUEST = 1;
    private final int AUTOCOMPLTE_REQUEST = 2;
    private LatLng origin;
    private LatLng destination;
    private int markerCount = 0;
    private int CAMERA_ZOOM = 18;
    private int route = 1;
    private int index = 0;
    String mode = "driving";
    private DirectionResponse mDirectionResponse;
    Context mContext;
    private static final String TAG = "mapfrag";

    private GeofencingClient mGeofencingClient;
    private PendingIntent mPendingIntent;
    private ArrayList<Geofence> mGeofence = null;
    private final int PLACE_PICKER_REUEST=1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        options = new GoogleMapOptions();
        options.zoomControlsEnabled(true).compassEnabled(true)
                .mapType(GoogleMap.MAP_TYPE_TERRAIN);
        SupportMapFragment mapFragment = SupportMapFragment.newInstance(options);
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction()
                .replace(R.id.mapContainer, mapFragment);
        ft.commit();
        //asynchronously load
        mapFragment.getMapAsync(this);
        mContext = this;

        searchButton = findViewById(R.id.search_button_map);
        nextRouteButton = findViewById(R.id.next_route_ib);
        directionButton = findViewById(R.id.direction_show_ib);
        walkingButton = findViewById(R.id.walking_ib);
        drivingButton = findViewById(R.id.driving_ib);
        mapViewButton = findViewById(R.id.map_view_btn);

        searchButton.setOnClickListener(this);
        nextRouteButton.setEnabled(false);
        directionButton.setOnClickListener(this);
        nextRouteButton.setOnClickListener(this);
        walkingButton.setOnClickListener(this);
        drivingButton.setOnClickListener(this);
        mapViewButton.setOnClickListener(this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {

        mMap = googleMap;
        mMap.setMapType(GoogleMap.MAP_TYPE_TERRAIN);
        mMap.getUiSettings().setZoomControlsEnabled(true);
        mMap.getUiSettings().setCompassEnabled(true);
        if (mLatLng != null) {
            origin = mLatLng;
            Log.d("maps", "onMapReady: null");
        } else {
            origin = new LatLng(23.7503816, 90.3930701);
        }

        //mMap.addMarker(new MarkerOptions().position(origin).title("Marker in Dhaka"));

        if (ExtraMarker != null) {
            mMap.addMarker(ExtraMarker);
            destination = ExtraMarker.getPosition();
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(destination, CAMERA_ZOOM));
        } else {
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(origin, CAMERA_ZOOM));
        }


        mMap.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener() {
            @Override
            public void onMapLongClick(LatLng latLng) {
                //clusterItems.add(new MarkerItems(latLng.latitude, latLng.longitude));
                //mClusterManager.addItems(clusterItems);
                // mClusterManager.cluster();
                mMap.addMarker(new MarkerOptions().position(latLng).title("Marker in Destination"));
                destination = latLng;
            }
        });
        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                showGeofenceDialog(latLng);
            }
        });
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]
                    {Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission
                            .ACCESS_FINE_LOCATION}, 1);
            return;
        }
        mMap.setOnMyLocationButtonClickListener(new GoogleMap.OnMyLocationButtonClickListener() {
            @Override
            public boolean onMyLocationButtonClick() {
                if (mMap.getMyLocation() != null) {
                    mLatLng = new LatLng(mMap.getMyLocation().getLatitude(), mMap.getMyLocation().getLongitude());
                }
                if (mLatLng != null) {
                    origin = mLatLng;
                    //mMap.clear();
                    mMap.addMarker(new MarkerOptions().title("Your location").position(origin));
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(origin, CAMERA_ZOOM));
                }
                Log.d("map", "onMyLocationButtonClick: clicked");
                return false;
            }
        });

        try {
            mMap.setMyLocationEnabled(true);

        } catch (SecurityException e) {
            e.printStackTrace();
        }


    }



    public void setMarkerOnMap(double lat, double lng, String title, String snippet) {
        LatLng latLng = new LatLng(lat, lng);
        ExtraMarker = new MarkerOptions().position(latLng).title(title).snippet(snippet);
    }

    private void showGeofenceDialog(final LatLng latLng) {
        final Dialog dialog = new Dialog(mContext);
        dialog.setContentView(R.layout.geofence_layout);
        final EditText areaEdit = dialog.findViewById(R.id.area_range_edit);
        final EditText timeEdit = dialog.findViewById(R.id.expire_time_edit);
        final EditText nameEdit = dialog.findViewById(R.id.name_geo_edit);
        Button savebtn = dialog.findViewById(R.id.geofence_save_btn);

        savebtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String areaString = areaEdit.getText().toString();
                String timeString = timeEdit.getText().toString();
                String nameString = nameEdit.getText().toString();

                if (!TextUtils.isEmpty(areaString) && !TextUtils.isEmpty(timeString) && !TextUtils.isEmpty(nameString)) {
                    int area = 0;
                    int time = 0;
                    try {
                        area = Integer.parseInt(areaString);
                        time = Integer.parseInt(timeString);
                    } catch (NumberFormatException e) {

                        e.printStackTrace();
                    }
                    mMap.addMarker(new MarkerOptions().position(latLng).title(nameString).snippet("latitude: "+latLng.latitude+" , longitude: "+latLng.longitude));
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, CAMERA_ZOOM));

                    saveAsGeofence(area, time, nameString, latLng);
                    dialog.dismiss();
                }else{
                    Toast.makeText(mContext, "Please give all required information",Toast.LENGTH_SHORT).show();
                }

            }
        });
        dialog.show();

    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.maps_menu, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.nearbyPlaces) {
            Intent n = new Intent(MapsActivity.this,NearbyPlacesActivity.class);
            startActivity(n);
        }
        if (id == R.id.event){
            Intent n = new Intent(MapsActivity.this,EventActivity.class);
            startActivity(n);
        }
        if (id == R.id.weather){
            Intent n = new Intent(MapsActivity.this,WeatherActivity.class);
            startActivity(n);
        }
        if (id == R.id.placePicker){
            try {
                Intent intent = new PlacePicker.IntentBuilder().build(this);
                startActivityForResult(intent,PLACE_PICKER_REUEST);
            } catch (GooglePlayServicesRepairableException e) {
                e.printStackTrace();
            } catch (GooglePlayServicesNotAvailableException e) {
                e.printStackTrace();
            }
        }
        return super.onOptionsItemSelected(item);
    }

    private void saveAsGeofence(int area, int time, String name, LatLng latLng) {
        //TODO: geofence

        mGeofence = new ArrayList<>();
        mGeofencingClient = LocationServices.getGeofencingClient(mContext);
        mPendingIntent = null;

        Geofence geofence = new Geofence.Builder()
                .setRequestId(name)
                .setCircularRegion(latLng.latitude, latLng.longitude, area)
                .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER | Geofence.GEOFENCE_TRANSITION_EXIT)
                .setExpirationDuration(time * 60 * 60 * 1000)
                .build();

        mGeofence.add(geofence);
        registerGeofence();

    }

    private void registerGeofence() {
        try {
            mGeofencingClient.addGeofences(getGeofencingRequest(), getPendingIntent()).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()) {
                        Toast.makeText(mContext, "Geofence Added", Toast.LENGTH_SHORT).show();
                    }
                }
            });
            Log.d(TAG, "Geofence: registerd");
        }catch (SecurityException e) {
            Log.d(TAG, "Geofence: not register");
        }


    }

    private GeofencingRequest getGeofencingRequest() {
        GeofencingRequest.Builder builder = new GeofencingRequest.Builder();
        builder.setInitialTrigger(GeofencingRequest.INITIAL_TRIGGER_ENTER);
        builder.addGeofences(mGeofence);
        Log.d(TAG, "Geofence: requested");
        return builder.build();
    }
    private PendingIntent getPendingIntent() {
        if (mPendingIntent != null) {
            return mPendingIntent;
        }
        Intent intent = new Intent(mContext, PendingIntentService.class);
        mPendingIntent = PendingIntent.getService(mContext, 999, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        return mPendingIntent;
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        switch (id) {
            case R.id.search_button_map:
                showAutocompleteWidget();
                break;
            case R.id.direction_show_ib:
                getDirections();
                break;
            case R.id.next_route_ib:
                showDirection();
                break;
            case R.id.walking_ib:
                mode = "walking";
                index=0;
                getDirections();
                break;
            case R.id.driving_ib:
                mode = "driving";
                index=0;
                getDirections();
                break;
            case R.id.map_view_btn:
                showMapViewDialog();
                break;
        }
    }

    private void showMapViewDialog() {
        final Dialog dialog = new Dialog(mContext);
        dialog.setContentView(R.layout.map_view_layout);


        // set the custom dialog components - text, image and button
        ImageView defaultMap = dialog.findViewById(R.id.default_view_iv);

        ImageView satelliteMap =  dialog.findViewById(R.id.satellite_iv);
        ImageView normalMap =  dialog.findViewById(R.id.normal_iv);

        satelliteMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        defaultMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mMap.setMapType(GoogleMap.MAP_TYPE_TERRAIN);
                dialog.dismiss();
            }
        });
        satelliteMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
                dialog.dismiss();
            }
        });
        normalMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
                dialog.dismiss();
            }
        });
        dialog.show();
        return;
    }

    private void showAutocompleteWidget() {
        try {
            AutocompleteFilter filter = new AutocompleteFilter.Builder().build();
            Intent intent = new PlaceAutocomplete.IntentBuilder(PlaceAutocomplete.MODE_FULLSCREEN).setFilter(filter).build((Activity) mContext);
            startActivityForResult(intent, AUTOCOMPLTE_REQUEST);
        } catch (GooglePlayServicesRepairableException e) {
            e.printStackTrace();
        } catch (GooglePlayServicesNotAvailableException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {

            case AUTOCOMPLTE_REQUEST:
                if (resultCode == RESULT_OK) {
                    Place place = PlaceAutocomplete.getPlace(mContext, data);
                    LatLng latLng = place.getLatLng();
                    mMap.addMarker(new MarkerOptions().title(place.getName().toString()).position(latLng));
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, CAMERA_ZOOM));
                    destination = latLng;
                    Toast.makeText(mContext, ""+place.getName().toString(), Toast.LENGTH_SHORT).show();
                }
            case PLACE_PICKER_REQUEST:
                if (resultCode == RESULT_OK){
                    Place place = PlacePicker.getPlace(this,data);
                    LatLng latLng = place.getLatLng();
                    String name = (String) place.getName();
                    String address = (String) place.getAddress();
                    float rating = place.getRating();
                    mMap.clear();
                    mMap.addMarker(new MarkerOptions().position(latLng).title(name).snippet("Rating :"+String.valueOf(rating)).snippet(address));
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng,18));

                }
                break;
        }
    }
    public void setMyLocation(LatLng latLng) {
        mLatLng = latLng;
        Log.d(TAG, "setMyLocation: "+(mLatLng == null));
    }

    //---------------------Direction-------------------//
    private void getDirections() {

        if (destination == null) {
            return;
        }
        if (mProgressDialog != null) {
            mProgressDialog.show();
        }

        String key = getString(R.string.direction_api);
        String org = origin.latitude + "," + origin.longitude;
        String dest = destination.latitude + "," + destination.longitude;
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(DIRECTION_BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        mDirectionService = retrofit.create(DirectionService.class);


        String urlString
                = String.format("json?origin=%s&destination=%s&alternatives=true&mode=%s&key=%s",
                org,dest,mode,key);
        Log.d("direction", "getDirections: url :"+DIRECTION_BASE_URL + urlString);

        Call<DirectionResponse> directionResponseCall = mDirectionService.getDirections(urlString);
        directionResponseCall.enqueue(new Callback<DirectionResponse>() {
            @Override
            public void onResponse(Call<DirectionResponse> call, Response<DirectionResponse> response) {
                if(response.isSuccessful()){
                    if (mProgressDialog != null) {
                        mProgressDialog.dismiss();
                    }
                    DirectionResponse directionResponse = response.body();
                    route = directionResponse.getRoutes().size();
                    Log.d("distance", "onResponse: Route "+route);
                    if (route > 0) {
                        mDirectionResponse = directionResponse;
                        showDirection();
                    }
                }
            }

            @Override
            public void onFailure(Call<DirectionResponse> call, Throwable t) {
                mProgressDialog.dismiss();
            }
        });
    }

    private void showDirection() {

        if (mDirectionResponse == null) {
            directionButton.setEnabled(false);
            nextRouteButton.setEnabled(false);
            return;
        }

        index = index % route;
        List<DirectionResponse.Step> steps =
                mDirectionResponse.getRoutes().get(index)
                        .getLegs().get(0).getSteps();
        getDestinationDistanceAndDuration(mDirectionResponse.getRoutes().get(index).getLegs());


        for(int i = 0; i < steps.size(); i++){
            double startLat = steps.get(i).getStartLocation().getLat();
            double startLng = steps.get(i).getStartLocation().getLng();
            LatLng startPoint = new LatLng(startLat,startLng);

            double endLat = steps.get(i).getEndLocation().getLat();
            double endLng = steps.get(i).getEndLocation().getLng();
            LatLng endPoint = new LatLng(endLat,endLng);

            Polyline polyline = mMap.addPolyline(new PolylineOptions()
                    .add(startPoint)
                    .add(endPoint));
            polyline.setColor(Color.GREEN);

        }
        directionButton.setEnabled(true);
        if (route > 1) {
            nextRouteButton.setEnabled(true);
        }
        index++;

    }

    private void getDestinationDistanceAndDuration(List<DirectionResponse.Leg> legs) {
        Log.d("distance", "getDestinationDistanceAndDuration: "+ legs.get(0).getDistance().getText() +" "+ legs.get(0).getDuration().getText() +" Travel_mode: "+legs.get(0).getSteps().get(0).getTravelMode());
        String destAddr = legs.get(0).getEndAddress();
        mMap.clear();
        mMap.addMarker(new MarkerOptions().title(destAddr).snippet("Distance: "+ legs.get(0).getDistance().getText() +" Duration: "+ legs.get(0).getDuration().getText()).position(destination));
        mMap.addMarker(new MarkerOptions().title("Your Location").position(origin).draggable(true));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(origin, CAMERA_ZOOM));


    }
}
