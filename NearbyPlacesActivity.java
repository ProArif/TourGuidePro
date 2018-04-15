package com.arifulislam.tourguidepro.locations;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.net.Uri;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.Spinner;
import android.widget.Toast;

import com.arifulislam.tourguidepro.R;
import com.arifulislam.tourguidepro.adapters.PlaceAdapter;
import com.arifulislam.tourguidepro.responses.PlaceResponse;
import com.arifulislam.tourguidepro.services.PlaceService;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class NearbyPlacesActivity extends AppCompatActivity{

    private Button btnSubmit;
    private Spinner types, distance;
    private String typesSelected = "restaurant", api_key = "AIzaSyCAArNkJGFxcOUDL7Ai-O3z-ENWjRQXTTI";
    private int distanceSelected = 500;
    double latitude, longitude;
    private FusedLocationProviderClient locationProviderClient;
    Context context = this;
    private Double lat, lng;
    private FirebaseAuth mAuth;
    private FirebaseUser mUser;

    public static String BASE_URL = "https://maps.googleapis.com/maps/api/place/";
    GridView gridView;

    PlaceResponse responses;
    PlaceService service;
    PlaceAdapter placeAdapter;
    List<PlaceResponse.Result> places = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nearby_places);

        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();

        locationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        btnSubmit = findViewById(R.id.btnSubmit);
        gridView = findViewById(R.id.grid);
        types = findViewById(R.id.spinner1);
        distance = findViewById(R.id.spinner2);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.types, android.R.layout.simple_spinner_item);
// Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
// Apply the adapter to the spinner
        types.setAdapter(adapter);
        ArrayAdapter<CharSequence> adapter2 = ArrayAdapter.createFromResource(this,
                R.array.distance, android.R.layout.simple_spinner_item);
// Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
// Apply the adapter to the spinner
        distance.setAdapter(adapter2);

        distance.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                switch (i) {
                    case 0:
                        distanceSelected = 500;
                        Log.e("selected:", String.valueOf(distanceSelected));
                        break;
                    case 1:
                        distanceSelected = 1000;
                        Log.e("selected:", String.valueOf(distanceSelected));
                        break;
                    case 2:
                        distanceSelected = 1500;
                        Log.e("selected:", String.valueOf(distanceSelected));
                        break;

                    case 3:
                        distanceSelected = 2000;
                        Log.e("selected:", String.valueOf(distanceSelected));
                        break;
                    case 4:
                        distanceSelected = 3000;
                        Log.e("selected:", String.valueOf(distanceSelected));
                        break;
                    case 5:
                        distanceSelected = 5000;
                        Log.e("selected:", String.valueOf(distanceSelected));
                        break;
                    case 6:
                        distanceSelected = 10000;
                        Log.e("selected:", String.valueOf(distanceSelected));
                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                distanceSelected = 500;
            }
        });

        types.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                switch (i) {
                    case 0:
                        typesSelected = "restaurant";
                        Log.e("selected:", String.valueOf(typesSelected));
                        break;
                    case 1:

                        typesSelected = "hospital";
                        Log.e("selected:", String.valueOf(typesSelected));
                        break;
                    case 2:

                        typesSelected = "atm";
                        Log.e("selected:", String.valueOf(typesSelected));
                        break;
                    case 3:

                        typesSelected = "bank";
                        Log.e("selected:", String.valueOf(typesSelected));
                        break;                    case 4:

                        typesSelected = "bus_station";
                        Log.e("selected:", String.valueOf(typesSelected));
                        break;

                    case 5:

                        typesSelected = "airport";
                        Log.e("selected:", String.valueOf(typesSelected));
                        break;

                    case 6:

                        typesSelected = "train_station";
                        Log.e("selected:", String.valueOf(typesSelected));
                        break;

                    case 7:

                        typesSelected = "mosque";
                        Log.e("selected:", String.valueOf(typesSelected));
                        break;

                    case 8:

                        typesSelected = "police";
                        Log.e("selected:", String.valueOf(typesSelected));
                        break;

                    case 9:

                        typesSelected = "shopping_mall";
                        Log.e("selected:", String.valueOf(typesSelected));
                        break;
                    case 10:

                        typesSelected = "movie_theater";
                        Log.e("selected:", String.valueOf(typesSelected));
                        break;
                    case 11:

                        typesSelected = "library";
                        Log.e("selected:", String.valueOf(typesSelected));
                        break;

                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                //typesSelected = "restaurant";
            }
        });


        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        service = retrofit.create(PlaceService.class);


        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getPlaces(typesSelected, distanceSelected, api_key);
                typesSelected = null;
                //Toast.makeText(NearbyPlacesActivity.this,"clicked",Toast.LENGTH_LONG).show();

            }
        });
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]
                    {Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission
                            .ACCESS_FINE_LOCATION}, 1);
            return;
        }
        locationProviderClient.getLastLocation().addOnSuccessListener(new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                if (location != null) {
                    latitude = location.getLatitude();
                    longitude = location.getLongitude();
                }
            }
        });

    }

    private void getPlaces(final String placeType, final int placeArea, String api) {

        try {
            String urlString = String.format("nearbysearch/json?location=%f,%f&radius=%d&types=%s&key=%s", latitude, longitude, placeArea, placeType, api);
            Call<PlaceResponse> responseCall = service.getPlacesResponse(urlString);

            responseCall.enqueue(new Callback<PlaceResponse>() {
                @Override
                public void onResponse(Call<PlaceResponse> call, final Response<PlaceResponse> response) {
                    if (response.code() == 200) {
                        if (places != null) {
                            places.clear();
                        }
                        responses = response.body();
                        for (int i = 0; i < responses.getResults().size(); i++) {
                            places.add(responses.getResults().get(i));
                        }

                        placeAdapter = new PlaceAdapter(context, places);
                        if (placeAdapter != null) {
                            gridView.setAdapter(placeAdapter);

                            gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                @Override
                                public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                                    PlaceResponse.Result result = places.get(position);

                                    String name = places.get(position).getName();
                                    Double rating = places.get(position).getRating();
                                    lat = places.get(position).getGeometry().getLocation().getLat();
                                    lng = places.get(position).getGeometry().getLocation().getLng();
                                    final LatLng latlng = new LatLng(lat, lng);
                                    //Toast.makeText(NearbyPlacesActivity.this, "Size: "+position+" "+name, Toast.LENGTH_LONG).show();
                                    final AlertDialog.Builder dialog = new AlertDialog.Builder(NearbyPlacesActivity.this);
                                    dialog.setCancelable(false);
                                    dialog.setTitle("Place Details");
                                    dialog.setMessage("Place Name: " + name + "\n" + "User Ratings: " + rating + "\n")
                                            .setPositiveButton("Show Direction", new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialogInterface, int i) {
                                                    Uri.Builder directionsBuilder = new Uri.Builder()
                                                            .scheme("https")
                                                            .authority("www.google.com")
                                                            .appendPath("maps")
                                                            .appendPath("dir")
                                                            .appendPath("")
                                                            .appendQueryParameter("api", "1")
                                                            .appendQueryParameter("destination", lat + "," + lng);

                                                    startActivity(new Intent(Intent.ACTION_VIEW, directionsBuilder.build()));

                                                }
                                            }).setNegativeButton("OK", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i) {
                                            dialogInterface.dismiss();
                                        }
                                    });
                                    dialog.show();


                                }
                            });
                            //Toast.makeText(NearbyPlacesActivity.this, "Size: " + responses.getResults().size(), Toast.LENGTH_LONG).show();


                        }
                    }
                }

                @Override
                public void onFailure(Call<PlaceResponse> call, Throwable t) {
                    Toast.makeText(NearbyPlacesActivity.this, t.toString(), Toast.LENGTH_LONG).show();

                }
            });
        } catch (Exception e) {
            Toast.makeText(this, "" + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }




}

