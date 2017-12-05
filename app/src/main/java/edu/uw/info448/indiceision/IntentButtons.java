package edu.uw.info448.indiceision;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class IntentButtons extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_intent_buttons);

        Button goButton = findViewById(R.id.go_button);
        goButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                //Show the location on Google Maps
                String location = "Guanchos Tacos, Seattle, Washington"; //Get this from Yelp API
                Uri geoUri = Uri.parse("google.navigation:q=" + Uri.encode(location)
                                + "&mode=w");
                Intent mapIntent = new Intent(Intent.ACTION_VIEW, geoUri);
                mapIntent.setPackage("com.google.android.apps.maps");
                if (mapIntent.resolveActivity(getPackageManager()) != null) {
                    startActivity(mapIntent);
                }
                // Build
                NotificationManager notifyMgr =
                        (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
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
                Intent yesGoodButton = new Intent(getApplicationContext(),HomeActivity.class);
                Intent yesBadButton = new Intent(getApplicationContext(),HomeActivity.class);
                Intent noButton = new Intent(getApplicationContext(),HomeActivity.class);

                yesGoodButton.setAction("Yes:Good");
                yesBadButton.setAction("Yes:Bad");
                noButton.setAction("No:None");

                PendingIntent piYesGood = PendingIntent.getService(getApplicationContext(), 0, yesGoodButton, 0);
                PendingIntent piYesBad = PendingIntent.getService(getApplicationContext(), 0, yesBadButton, 0);
                PendingIntent piNo = PendingIntent.getService(getApplicationContext(), 0, noButton, 0);

                String restaurantName = "Guanchos Tacos";//Get this from Yelp API

                Notification.Builder notifyBuilder =
                        new Notification.Builder(IntentButtons.this)
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
        Button shareButton = findViewById(R.id.share_button);
        shareButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                //Intent to a Messaging app?
            }
        });
        Button callButton = findViewById(R.id.order_button);
        callButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                String teliNumber = "2065472369"; //Get this from Yelp API
                Uri phoneNumber = Uri.parse("tel:" + teliNumber);
                Intent callIntent = new Intent(Intent.ACTION_DIAL, phoneNumber);
                startActivity(callIntent);
            }
        });
    }
}
