package edu.uw.info448.indiceision;

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
                String location = "Guanchos Tacos, Seattle, Washington";
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

                NotificationCompat.Builder mBuilder =
                        new NotificationCompat.Builder(IntentButtons.this)
                                .setSmallIcon(R.drawable.ic_dice)
                                .setContentTitle("Indiceision")
                                .setContentText("Did you visit Guanchos Tacos?");

                int mNotificationId = 001;

                notifyMgr.notify(mNotificationId, mBuilder.build());
            }
        });
        Button shareButton = (Button) findViewById(R.id.share_button);
        shareButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                //Intent to a Messaging app?
            }
        });
        Button callButton = (Button) findViewById(R.id.order_button);
        callButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Uri phoneNumber = Uri.parse("tel:2065472369");
                Intent callIntent = new Intent(Intent.ACTION_DIAL, phoneNumber);
                startActivity(callIntent);
            }
        });
    }
}
