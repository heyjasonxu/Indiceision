package edu.uw.info448.indiceision;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.util.Pair;
import android.util.AttributeSet;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;

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
import java.util.Random;

/**
 * Created by m on 12/4/17.
 */

public class DrawingSurfaceView extends SurfaceView implements SurfaceHolder.Callback, LocationListener, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    public static final int LOCATION_REQUEST_CODE = 1;
    public static final String BUNDLE_KEY = "final";
    private static final String TAG = "SurfaceView";
    public static List<Review> reviews;
    public Dice dice; //public for easy access
    List<Pair<String, JSONObject>> restaurants;
    TextView randomRestaurantTxt;
    private float width = 100;
    private float height = width;
    private float x = 20;
    private float y = x;
    private int viewWidth, viewHeight; //size of the view
    private Bitmap bmp; //image to draw on
    private SurfaceHolder mHolder; //the holder we're going to post updates to
    private DrawingRunnable mRunnable; //the code that we'll want to run on a background thread
    private Thread mThread; //the background thread
    private Paint whitePaint; //drawing variables (pre-defined for speed)
    private Paint goldPaint; //drawing variables (pre-defined for speed)
    private MapFragment map;
    private GoogleMap gMap;
    private LocationRequest mLocationRequest;
    private GoogleApiClient mGoogleApiClient;
    private JSONObject rest;
    private JSONObject coor;
    private double lat;
    private double lng;
    int randomInt = -1;
    private boolean started = false;
    private TextView title, price, rating, phone;
    private Location current;
    private boolean currentlyOpen;
    private String budget;
    private String distance;
    private JSONObject results;

    /**
     * We need to override all the constructors, since we don't know which will be called
     */
    public DrawingSurfaceView(Context context) {
        this(context, null);
    }

    public DrawingSurfaceView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public DrawingSurfaceView(Context context, AttributeSet attrs, int defaultStyle) {
        super(context, attrs, defaultStyle);

        viewWidth = 1;
        viewHeight = 1; //positive defaults; will be replaced when #surfaceChanged() is called

        // register our interest in hearing about changes to our surface
        mHolder = getHolder();
        mHolder.addCallback(this);

        mRunnable = new DrawingRunnable();

        //set up drawing variables ahead of time
        whitePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        whitePaint.setColor(Color.WHITE);
        goldPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        goldPaint.setColor(Color.rgb(145, 123, 76));

        init();
    }

    /**
     * Initialize graphical drawing state
     */
    public void init() {

        dice = new Dice(viewHeight / 2, viewWidth / 2);
    }

    /**
     * Helper method for the "game loop"
     */
    public void update() {

        //Log.v(TAG, "update" + "dx: " + ball.dx + "dy : " + ball.dy);

        dice.updatePosition(dice.cx + dice.dx, dice.cy + dice.dy);

        //slow down
        dice.dx *= 0.99;
        dice.dy *= 0.99;


        String restaurant = "";
        /* hit detection */

        Random r = new Random();

        if (dice.cx + dice.offset > viewWidth) { //left bound
            dice.cx = viewWidth - dice.offset;
            dice.dx *= -1;
            Log.v(TAG, "hit left");
            if (restaurants != null) {

                randomInt = new Random().nextInt(restaurants.size());
                Pair<String, JSONObject> singlePair = restaurants.get(randomInt);
                restaurant = singlePair.first;
            }
        } else if (dice.cx - dice.offset < 0) { //right bound
            dice.cx = dice.offset;
            dice.dx *= -1;
            Log.v(TAG, "hit right");
            if (restaurants != null) {

                randomInt = new Random().nextInt(restaurants.size());
                Pair<String, JSONObject> singlePair = restaurants.get(randomInt);
                restaurant = singlePair.first;
            }
        } else if (dice.cy + dice.offset > viewHeight) { //bottom bound
            dice.cy = viewHeight - dice.offset;
            dice.dy *= -1;
            Log.v(TAG, "hit bottom");
            if (restaurants != null) {

                randomInt = new Random().nextInt(restaurants.size());
                Pair<String, JSONObject> singlePair = restaurants.get(randomInt);
                restaurant = singlePair.first;
            }
        } else if (dice.cy - dice.offset < 0) { //top bound
            dice.cy = dice.offset;
            dice.dy *= -1;
            Log.v(TAG, "hit top");
            if (restaurants != null) {

                randomInt = new Random().nextInt(restaurants.size());
                Pair<String, JSONObject> singlePair = restaurants.get(randomInt);
                restaurant = singlePair.first;
            }
        }

        final String finalRestaurant = restaurant;


        if (randomRestaurantTxt != null) {

            Log.v(TAG, restaurant);

            Activity activity = (Activity) this.getContext();

            activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (!finalRestaurant.equals("")) {
                        randomRestaurantTxt.setText(finalRestaurant);
                    }

                }
            });


        }


        double velocity = Math.sqrt(Math.pow(dice.dx, 2) + Math.pow(dice.dy, 2));

        //Log.v(TAG, dice.moved + " " + velocity);


        if (dice.moved && velocity < 5) {


            Log.v(TAG, "startActivity");

            if (!started) {
                started = true;


                Intent intent = new Intent(getContext(), LocationDetails.class);
                intent.putExtra(BUNDLE_KEY, restaurants.get(randomInt).second.toString());


                Activity activity = (Activity) getContext();

                activity.startActivity(intent);

            }


        }
    }


    /**
     * Helper method for the "render loop"
     *
     * @param canvas The canvas to draw on
     */
    public synchronized void render(Canvas canvas) {
        if (canvas == null) return;

        canvas.drawColor(Color.rgb(51, 10, 111));

        canvas.drawRect(dice.left, dice.top, dice.right, dice.bottom, whitePaint);
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        Log.d(TAG, "Creating new drawing thread");
        mThread = new Thread(mRunnable);
        mRunnable.setRunning(true); //turn on the runner
        mThread.start(); //start up the thread when surface is created

        if (mGoogleApiClient == null) {
            Log.v(TAG, "mgoogle api client null");
            mGoogleApiClient = new GoogleApiClient.Builder(getContext())
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();

            mGoogleApiClient.connect();
        }


        Activity activity = (Activity) this.getContext();

        randomRestaurantTxt = (TextView) activity.findViewById(R.id.txt_random_restaurant);

        if (randomRestaurantTxt == null) {

            Log.v(TAG, "text is null");
        } else {
            Log.v(TAG, "text is not null");
        }

//        Activity activity = (Activity) getContext();


        // activity.startActivity(new Intent(getContext(), Reviews.class));


    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        synchronized (mHolder) { //synchronized to keep this stuff atomic
            viewWidth = width;
            viewHeight = height;
            bmp = Bitmap.createBitmap(viewWidth, viewHeight, Bitmap.Config.ARGB_8888); //new buffer to draw on

            init();
        }
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        // we have to tell thread to shut down & wait for it to finish, or else
        // it might touch the Surface after we return and explode
        mRunnable.setRunning(false);
        boolean retry = true;
        while (retry) {
            try {
                mThread.join();
                retry = false;
            } catch (InterruptedException e) {
                //will try again...
            }
        }
        //Log.d(TAG, "Drawing thread shut down");
    }

    private String getToken() throws Exception {

        String url = "https://api.yelp.com/oauth2/token";
        URL org = new URL("https://api.yelp.com/oauth2/token");

        HttpClient client = new DefaultHttpClient();
        HttpPost post = new HttpPost(url);
//        post.setHeader("User-Agent", USER_AGENT);

        List<NameValuePair> urlParameters = new ArrayList<NameValuePair>();
        urlParameters.add(new BasicNameValuePair("grant_type", getContext().getString(R.string.yelp_grant_type)));
        urlParameters.add(new BasicNameValuePair("client_id", getContext().getString(R.string.yelp_client_id)));
        urlParameters.add(new BasicNameValuePair("client_secret", getContext().getString(R.string.yelp_client_secret)));

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
        results = new JSONObject(search());

        JSONArray list = results.getJSONArray("businesses");

        restaurants = new ArrayList<Pair<String, JSONObject>>();

        for (int i = 0; i < list.length(); i++) {

            JSONObject restaurant = list.getJSONObject(i);

            String restaurantName = restaurant.get("name").toString();

            Pair<String, JSONObject> pair = new Pair<String, JSONObject>(restaurantName, restaurant);

            restaurants.add(pair);
        }

        Random rand = new Random();
        int r = rand.nextInt(list.length());
        rest = list.getJSONObject(r);
        coor = rest.getJSONObject("coordinates");
        lat = Double.parseDouble(coor.get("latitude").toString());
        lng = Double.parseDouble(coor.get("longitude").toString());
        final String pn = rest.get("phone").toString();
        final TextView reviews = (TextView) findViewById(R.id.reviews);

//        Log.v(TAG, rest.toString());
        Log.v(TAG, lat + "");
        Log.v(TAG, lng + "");
        Log.v("Restaurant Name", rest.get("name").toString());


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
//            this.reviews.add(new Review(user.get("name").toString(), rev.get("time_created").toString(),
//                    rev.get("rating").toString(), rev.get("text").toString()));
        }
    }

    @Override
    public void onLocationChanged(Location location) {
        current = location;
        Log.v(TAG, "Latitude: " + location.getLatitude());
        Log.v(TAG, "Longitude: " + location.getLongitude());
    }

    private String formatPhoneNumber(String phone) {
        Log.v(TAG, phone);
        return "(" + phone.substring(2, 5) + ") "
                + phone.substring(5, 8) + "-" + phone.substring(8, 12);
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {

        Log.v(TAG, "GoogleApiClient connected");


        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(10000);
        mLocationRequest.setFastestInterval(5000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);


        int permissionCheck = ContextCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION);
        if (permissionCheck == PackageManager.PERMISSION_GRANTED) {

            current = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);


            Log.v(TAG, current + "");

            //START API CALL
            new Task().execute();
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);

        } else {
            Activity activity = (Activity) this.getContext();
            ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_REQUEST_CODE);
        }


    }


    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    /**
     * An inner class representing a runnable that does the drawing. Animation timing could go in here.
     * http://obviam.net/index.php/the-android-game-loop/ has some nice details about using timers to specify animation
     */
    public class DrawingRunnable implements Runnable {

        private boolean isRunning; //whether we're running or not (so we can "stop" the thread)

        public void setRunning(boolean running) {
            this.isRunning = running;
        }

        public void run() {
            Canvas canvas;
            while (isRunning) {
                canvas = null;
                try {
                    canvas = mHolder.lockCanvas(); //grab the current canvas
                    synchronized (mHolder) {
                        update(); //update the game
                        render(canvas); //redraw the screen
                    }
                } finally { //no matter what (even if something goes wrong), make sure to push the drawing so isn't inconsistent
                    if (canvas != null) {
                        mHolder.unlockCanvasAndPost(canvas);
                    }
                }
            }
        }
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
