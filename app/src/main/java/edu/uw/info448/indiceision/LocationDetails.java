package edu.uw.info448.indiceision;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


public class LocationDetails extends AppCompatActivity implements OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener {

    public static String TAG = "Detail";

    private static final int LOCATION_REQUEST_CODE = 1;

    private MapFragment map;

    private GoogleMap gMap;

    private LocationRequest mLocationRequest;

    private GoogleApiClient mGoogleApiClient;

    private JSONObject rest;

    private JSONObject coor;

    private double lat;

    private double lng;

    private TextView title, price, rating, phone;

    private Location current;

    public static List<Review> reviews;

    private DatabaseReference mDatabase;
    private FirebaseAuth auth;


    private String rId;
    private int numberSuggested;

    private boolean currentlyOpen;
    private String budget;
    private String distance;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location_details);
        reviews = new ArrayList<>();
        if (mGoogleApiClient == null) {
            mGoogleApiClient = new GoogleApiClient.Builder(this)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();
        }

        title = (TextView) findViewById(R.id.title);
        price = (TextView) findViewById(R.id.price);
        rating = (TextView) findViewById(R.id.rating);
        phone = (TextView) findViewById(R.id.phone);

        mDatabase = FirebaseDatabase.getInstance().getReference();
        auth = FirebaseAuth.getInstance();


        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);


    }

    private String getToken() throws Exception {

        String url = "https://api.yelp.com/oauth2/token";
        URL org = new URL("https://api.yelp.com/oauth2/token");

        HttpClient client = new DefaultHttpClient();
        HttpPost post = new HttpPost(url);
//        post.setHeader("User-Agent", USER_AGENT);

        List<NameValuePair> urlParameters = new ArrayList<NameValuePair>();
        urlParameters.add(new BasicNameValuePair("grant_type", getString(R.string.yelp_grant_type)));
        urlParameters.add(new BasicNameValuePair("client_id", getString(R.string.yelp_client_id)));
        urlParameters.add(new BasicNameValuePair("client_secret", getString(R.string.yelp_client_secret)));

        post.setEntity(new UrlEncodedFormEntity(urlParameters));

        HttpResponse response = client.execute(post);
        Log.v(TAG, "\nSending 'POST' request to URL : " + url);
        Log.v(TAG, "Post parameters : " + post.getEntity());
        Log.v(TAG, "Response Code : " +
                response.getStatusLine().getStatusCode());

        BufferedReader rd = new BufferedReader(
                new InputStreamReader(response.getEntity().getContent()));

        StringBuffer result = new StringBuffer();
        String line = "";
        while ((line = rd.readLine()) != null) {
            result.append(line);
        }

        JSONObject jsonObj = new JSONObject(result.toString());

        Log.v(TAG, jsonObj.get("access_token").toString());
        return jsonObj.get("access_token").toString();
    }

    private String search() throws Exception {
        String token = getToken();
        String url = "https://api.yelp.com/v3/businesses/search?term=restaurants";
//        String at = "ACCESS_TOKEN=" + token + "&";
        url += "&latitude=" + current.getLatitude() + "&longitude=" + current.getLongitude();
        url += "&radius=" + 3200;
        url += "&limit" + 50;
        Log.v(TAG, url);

        HttpClient client = new DefaultHttpClient();
        HttpGet request = new HttpGet(url);

        // add request header
        request.setHeader("Authorization", "Bearer " + token);

        HttpResponse response = client.execute(request);

        Log.v(TAG, "\nSending 'GET' request to URL : " + url);
        Log.v(TAG, "Response Code : " +
                response.getStatusLine().getStatusCode());

        BufferedReader rd = new BufferedReader(
                new InputStreamReader(response.getEntity().getContent()));

        StringBuffer result = new StringBuffer();
        String line = "";
        while ((line = rd.readLine()) != null) {
            result.append(line);
        }

//        Log.v(TAG, result.toString());
        return result.toString();
    }



    private void getRestaurant() throws Exception {
        JSONObject results = new JSONObject(search());
        JSONArray list = results.getJSONArray("businesses");
        Random rand = new Random();
        int r = rand.nextInt(list.length());

        FirebaseUser user = auth.getCurrentUser();

        rest = list.getJSONObject(r);
        rId = rest.get("id").toString();



        coor = rest.getJSONObject("coordinates");
        lat = Double.parseDouble(coor.get("latitude").toString());
        lng = Double.parseDouble(coor.get("longitude").toString());
        final String pn = rest.get("phone").toString();
        final TextView reviews = (TextView)findViewById(R.id.reviews);

//        Log.v(TAG, rest.toString());
        Log.v(TAG, lat + "");
        Log.v(TAG, lng + "");
        Log.v(TAG, rest.get("rating").toString());
        Log.v(TAG, rest.get("phone").toString());
        Log.v(TAG,coor.get("latitude").toString());
        Log.v(TAG,coor.get("longitude").toString());



        runOnUiThread(new Runnable() {
            @Override
            public void run() {

                try {
                    title.setText(rest.get("name").toString());
                    price.setText("Price: " + rest.get("price").toString());
                    rating.setText("Rating: " + rest.get("rating").toString());
                    phone.setText(formatPhoneNumber(rest.get("phone").toString()));
                    reviews.setText("  (reviews)");

                    phone.setOnClickListener(new View.OnClickListener() {

                        @Override
                        public void onClick(View arg0) {

                            // TODO Auto-generated method stub
                            Intent intent = new Intent(Intent.ACTION_DIAL);
                            intent.setData(Uri.parse("tel:" + pn.substring(2)));
                            startActivity(intent);
                        }
                    });

                    reviews.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Log.v(TAG, "review");
                            Intent callIntent = new Intent(LocationDetails.this, Reviews.class);
                            startActivity(callIntent);
                        }
                    });

                } catch (JSONException e) {
                    e.printStackTrace();
                }
                LatLng l = new LatLng(lat, lng);
                gMap.addMarker(new MarkerOptions()
                        .position(l)
                        .title("Marker"));
                gMap.moveCamera(CameraUpdateFactory.newLatLng(l));
            }
        });

        mDatabase.child("restaurants").child(rId).child("numberSuggested").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                numberSuggested = (int) dataSnapshot.getChildrenCount();
                Log.v(TAG, "Here is numberSuggested " + numberSuggested);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });



        mDatabase.child("restaurants").child(rId).child("numberSuggested").child(auth.getUid()).setValue(auth.getUid());
        mDatabase.child("restaurants").child(rId).child("id").setValue(rId);
        mDatabase.child("restaurants").child(rId).child("restaurantName").setValue(rest.get("name"));




        getReviews(rest.get("id").toString());



    }

    private void getReviews(String id) throws Exception {
        String token = getToken();
        String url = "https://api.yelp.com/v3/businesses/";
//        String at = "ACCESS_TOKEN=" + token + "&";
        url += id;
        url += "/reviews";
        Log.v(TAG, url);

        HttpClient client = new DefaultHttpClient();
        HttpGet request = new HttpGet(url);

        // add request header
        request.setHeader("Authorization", "Bearer " + token);

        HttpResponse response = client.execute(request);

        Log.v(TAG, "\nSending 'GET' request to URL : " + url);
        Log.v(TAG, "Response Code : " +
                response.getStatusLine().getStatusCode());

        BufferedReader rd = new BufferedReader(
                new InputStreamReader(response.getEntity().getContent()));

        StringBuffer result = new StringBuffer();
        String line = "";
        while ((line = rd.readLine()) != null) {
            result.append(line);
        }

        Log.v(TAG, result.toString());
        saveReviews(result.toString());
        Log.v(TAG, "complete");
    }

    public void saveReviews(String reviews) throws JSONException {
        JSONObject obj = new JSONObject(reviews);
        JSONArray list = obj.getJSONArray("reviews");
        for(int i = 0; i < list.length(); i++) {
            JSONObject rev = list.getJSONObject(i);
            JSONObject user = rev.getJSONObject("user");
            this.reviews.add(new Review(user.get("name").toString(), rev.get("time_created").toString(),
                    rev.get("rating").toString(), rev.get("text").toString()));
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        gMap = googleMap;
    }

    @Override
    protected void onStart() {
        mGoogleApiClient.connect();
        super.onStart();
    }

    @Override
    protected void onStop() {
        mGoogleApiClient.disconnect();
        super.onStop();
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        Log.v(TAG, "GoogleApiClient connected");

        new Task().execute();

        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(10000);
        mLocationRequest.setFastestInterval(5000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        current = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);

        int permissionCheck = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION);
        if(permissionCheck == PackageManager.PERMISSION_GRANTED) {
            //have permission, can go ahead and do stuff

            //assumes location settings enabled

            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
//            Location l = new Location("Mock");
//            l.setLatitude(47.6550);
//            l.setLongitude(-122.3080);
//            LocationServices.FusedLocationApi.setMockLocation(mGoogleApiClient, l);
        }
        else {
            //request permission
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_REQUEST_CODE);
        }
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onLocationChanged(Location location) {
        current = location;
        Log.v(TAG,  "Latitude: " + location.getLatitude());
        Log.v(TAG,  "Longitude: " + location.getLongitude());
    }

    private String formatPhoneNumber(String phone) {
        Log.v(TAG, phone);
        return "(" + phone.substring(2, 5) + ") "
                + phone.substring(5, 8) + "-" + phone.substring(8, 12);
    }


    private class Task extends AsyncTask<String, String, String> {

        @Override
        protected String doInBackground(String... params) {
            try {
                getRestaurant();
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }
    }
}
