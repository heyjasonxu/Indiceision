package edu.uw.info448.indiceision;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import org.w3c.dom.Text;

public class Profile extends AppCompatActivity {

    private DatabaseReference mDatabase;
    private FirebaseAuth auth;
    private FirebaseUser currentUser;
    private FirebaseRecyclerAdapter adapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        // add back button

        mDatabase = FirebaseDatabase.getInstance().getReference();

        auth = FirebaseAuth.getInstance();
        currentUser = auth.getCurrentUser();
        TextView displayName = (TextView) findViewById(R.id.user_name);
        displayName.setText(currentUser.getDisplayName());

        Query query = FirebaseDatabase.getInstance().
                getReference()
                .child("users")
                .child(currentUser.getUid())
                .limitToFirst(50); //Think about limit.

        FirebaseRecyclerOptions<userRestaurant> options = new FirebaseRecyclerOptions.Builder<userRestaurant>()
                .setQuery(query, userRestaurant.class)
                .build();

        adapter = new FirebaseRecyclerAdapter<userRestaurant, userRestaurantHolder>(options){

        }

//        ValueEventListener myValueListener = new ValueEventListener() {
//            @Override
//            public void onDataChange(DataSnapshot dataSnapshot) {
//                for(DataSnapshot r: dataSnapshot.getChildren()){
//                    TextView restaurant = (TextView) findViewById(R.id.restaurant);
//                    String userRestuarant = (String) r.getValue();
//                    restaurant.setText(userRestuarant);
//                }
//
//            }
//
//            @Override
//            public void onCancelled(DatabaseError databaseError) {
//
//            }
//        };
//
//
//        mDatabase.child("users").child(currentUser.getUid()).addValueEventListener(myValueListener);

    }
}
