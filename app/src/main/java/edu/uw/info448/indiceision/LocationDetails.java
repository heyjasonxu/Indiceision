package edu.uw.info448.indiceision;

import android.Manifest;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.auth.AuthUI;
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
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

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

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;


public class LocationDetails extends AppCompatActivity implements OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener {

    private static final int LOCATION_REQUEST_CODE = 1;
    public static String TAG = "Detail";
    public static List<Review> reviews;
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
    private DatabaseReference mDatabase;
    private FirebaseAuth auth;


    private String rId;
    private int numberSuggested;
    private int numberLiked;
    private int numberVisited;

    private boolean currentlyOpen;
    private String budget;
    private String distance;
    private String restaurant;
    private String name;

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

        restaurant = getIntent().getExtras().getString(DrawingSurfaceView.BUNDLE_KEY);


        title = (TextView) findViewById(R.id.title);
        price = (TextView) findViewById(R.id.price);
        rating = (TextView) findViewById(R.id.rating);
        phone = (TextView) findViewById(R.id.phone);

        mDatabase = FirebaseDatabase.getInstance().getReference();
        auth = FirebaseAuth.getInstance();

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        createGoButton();
        createShareButton();
        createCallButton();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_settings:
                startActivity(new Intent(this, SettingsActivity.class));
                return true;
            case R.id.profile:
                startActivity(new Intent(this, Profile.class));
                return true;
            case R.id.sign_out:
                AuthUI.getInstance()
                        .signOut(this)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull com.google.android.gms.tasks.Task<Void> task) {
                                Toast.makeText(LocationDetails.this, "Signed out", Toast.LENGTH_SHORT).show();
                            }
                        });
                startActivity(new Intent(this, Introduction.class));
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
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


    private void getRestaurant(JSONObject results) throws Exception {


        FirebaseUser user = auth.getCurrentUser();

        rest = results;
        rId = rest.get("id").toString();
//        rId = "din-tai-fung-seattle";


        coor = rest.getJSONObject("coordinates");
        lat = Double.parseDouble(coor.get("latitude").toString());
        lng = Double.parseDouble(coor.get("longitude").toString());
        final String pn = rest.get("phone").toString();
        final TextView reviews = (TextView) findViewById(R.id.reviews);

//        Log.v(TAG, rest.toString());
        Log.v(TAG, lat + "");
        Log.v(TAG, lng + "");
        Log.v(TAG, rest.get("rating").toString());
        Log.v(TAG, rest.get("phone").toString());
        Log.v(TAG, "This is the " + coor.get("latitude").toString());
        Log.v(TAG, coor.get("longitude").toString());


//        runOnUiThread(new Runnable() {
//            @Override
//            public void run() {

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

//        });


        //Set our rating.
        mDatabase.child("restaurants").child(rId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                DataSnapshot suggested = dataSnapshot.child("numberSuggested");
                numberSuggested = (int) suggested.getChildrenCount();
                DataSnapshot visited = dataSnapshot.child("numberVisited");
                numberVisited = (int) visited.getChildrenCount();
                DataSnapshot liked = dataSnapshot.child("numberLiked");
                numberLiked = (int) liked.getChildrenCount();
                String ourRating = "";

                if (numberSuggested == 1) {
                    ourRating = "You are the first person to be suggested this restaurant!";
                } else {

                    ourRating = "Ratings from the Indiceisive: Out of " + numberSuggested +
                            " users that were suggested this restaurant, " + numberVisited +
                            " users visited the restaurant and " + numberLiked + " liked it";
                }

                TextView ourRatingText = (TextView) findViewById(R.id.indice_rating);
                ourRatingText.setText(ourRating);
                Log.v(TAG, "Here is numberSuggested " + numberSuggested);
                Log.v(TAG, "Here is numberVisited " + numberVisited);
                Log.v(TAG, "Here is numberLiked " + numberLiked);

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
        for (int i = 0; i < list.length(); i++) {
            JSONObject rev = list.getJSONObject(i);
            JSONObject user = rev.getJSONObject("user");
            this.reviews.add(new Review(user.get("name").toString(), rev.get("time_created").toString(),
                    rev.get("rating").toString(), rev.get("text").toString()));
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        gMap = googleMap;
        try {
            Log.v(TAG, "getRestaurant");

            //Log.v("restaurant here: ", restaurant);

            JSONObject results = new JSONObject(restaurant);

            getRestaurant(results);
        } catch (Exception e) {

            e.printStackTrace();

        }
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


        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(10000);
        mLocationRequest.setFastestInterval(5000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);


        int permissionCheck = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION);
        if (permissionCheck == PackageManager.PERMISSION_GRANTED) {
            //have permission, can go ahead and do stuff

            //assumes location settings enabled

            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
            current = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
            //START API CALL

//            Location l = new Location("Mock");
//            l.setLatitude(47.6550);
//            l.setLongitude(-122.3080);
//            LocationServices.FusedLocationApi.setMockLocation(mGoogleApiClient, l);
        } else {
            //request permission
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_REQUEST_CODE);
        }


    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case LOCATION_REQUEST_CODE: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    //START API CALL
                    onConnected(null);
                }
                break;
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
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
        Log.v(TAG, "Latitude: " + location.getLatitude());
        Log.v(TAG, "Longitude: " + location.getLongitude());
    }

    private String formatPhoneNumber(String phone) {
        Log.v(TAG, phone);
        if (phone != null) {
            return "(" + phone.substring(2, 5) + ") "
                    + phone.substring(5, 8) + "-" + phone.substring(8, 12);
        }
        return "No phone number provided";
    }

    private void createGoButton(){
        Button goButton = findViewById(R.id.go_button);
        goButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                //Show the location on Google Maps
                String location = "Guanchos Tacos, Seattle, Washington"; //TODO: Get this from Yelp API
                Uri geoUri = Uri.parse("google.navigation:q=" + Uri.encode(location)
                        + "&mode=w");
                Intent mapIntent = new Intent(Intent.ACTION_VIEW, geoUri);
                mapIntent.setPackage("com.google.android.apps.maps");
                if (mapIntent.resolveActivity(getPackageManager()) != null) {
                    startActivity(mapIntent);
                }
                // Starts the making of the notification
                NotificationManager notifyMgr =
                        (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
                //Only needed if on a phone running Oreo and up
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
                    String id = "visit_channel";
                    CharSequence name = getString(R.string.channel_name);
                    String description = getString(R.string.channel_description);
                    int importance = NotificationManager.IMPORTANCE_LOW;
                    NotificationChannel notifyChannel = new NotificationChannel(id, name, importance);
                    notifyChannel.setDescription(description);
                    notifyChannel.setVibrationPattern(new long[]{100, 200, 300, 400, 500, 400, 300, 200, 400});
                    notifyMgr.createNotificationChannel(notifyChannel);
                }
                //Creates the intents for the buttons
                Intent yesGoodButton = new Intent(getApplicationContext(),Profile.class);
                Intent yesBadButton = new Intent(getApplicationContext(),Profile.class);
                Intent noButton = new Intent(getApplicationContext(),Profile.class);

                yesGoodButton.setAction("Yes:Good");
                yesBadButton.setAction("Yes:Bad");
                noButton.setAction("No:None");

                yesGoodButton.putExtra(Intent.EXTRA_TEXT, rId);
                yesGoodButton.putExtra(Intent.EXTRA_TEXT, name);
                yesGoodButton.setType("text/plain");

                yesBadButton.putExtra(Intent.EXTRA_TEXT, rId);
                yesBadButton.putExtra(Intent.EXTRA_TEXT, name);
                yesBadButton.setType("text/plain");

                //Wraps the intents in PendingIntents
                PendingIntent piYesGood = PendingIntent.getService(getApplicationContext(), 0, yesGoodButton, 0);
                PendingIntent piYesBad = PendingIntent.getService(getApplicationContext(), 0, yesBadButton, 0);
                PendingIntent piNo = PendingIntent.getService(getApplicationContext(), 0, noButton, 0);

                String restaurantName = null;
                try {
                    restaurantName = rest.get("name").toString();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                //Builds the notification using the previously made components
                Notification.Builder notifyBuilder =
                        new Notification.Builder(LocationDetails.this)
                                .setSmallIcon(R.drawable.ic_dice)
                                .setContentTitle("Indiceision")
                                .setContentText("Did you visit "+restaurantName+"?")
                                .addAction(R.drawable.ic_thumbs_up,
                                        getString(R.string.yes_good), piYesGood)
                                .addAction(R.drawable.ic_thumbs_down,
                                        getString(R.string.yes_bad), piYesBad)
                                .addAction(R.drawable.ic_cancel_icon,
                                        getString(R.string.no), piNo);

                int mNotificationId = 001;

                notifyMgr.notify(mNotificationId, notifyBuilder.build());
            }
        });
    }
    private void createShareButton(){
        Button shareButton = findViewById(R.id.share_button);
        shareButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                String storeURL = "https://www.yelp.com/biz/guanacos-tacos-pupuseria-seattle"; //TODO:Get this from Yelp API
                Intent shareIntent = new Intent(Intent.ACTION_SEND);
                shareIntent.putExtra(Intent.EXTRA_TEXT, storeURL);
                shareIntent.setType("text/plain");
                if (shareIntent.resolveActivity(getPackageManager()) != null) {
                    startActivity(shareIntent);
                }
            }
        });
    }
    private void createCallButton(){
        Button callButton = findViewById(R.id.call_button);
        callButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                String teliNumber = "2065472369"; //TODO:Get this from Yelp API
                Uri phoneNumber = Uri.parse("tel:" + teliNumber);
                Intent callIntent = new Intent(Intent.ACTION_DIAL, phoneNumber);
                startActivity(callIntent);
            }
        });
    }


}
