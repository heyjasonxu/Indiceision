package edu.uw.info448.indiceision;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
    private static final String TAG = "Profile";
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

        Query query = mDatabase
                .child("users")
                .child(currentUser.getUid())
                .limitToFirst(50); //Think about limit.

        FirebaseRecyclerOptions<userRestaurant> options = new FirebaseRecyclerOptions.Builder<userRestaurant>()
                .setQuery(query, userRestaurant.class)
                .build();

        adapter = new FirebaseRecyclerAdapter<userRestaurant, userRestaurantViewHolder>(options){

            @Override
            public userRestaurantViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

                View view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.restaurant_list_item, parent, false);
                return new userRestaurantViewHolder(view);
            }

            @Override
            protected void onBindViewHolder(userRestaurantViewHolder holder, int position, userRestaurant model) {
                Log.v(TAG, "This is the restaurant " + model.getRestaurantName());
                holder.restaurantName.setText(model.getRestaurantName());
                holder.restaurantLiked.setText(model.getLiked());
            }
        };

        //if visted and liked: mDatabase.child("users").child(currentUser.getUid()).push.setValue(userRestaurant)
        // with liked as yes. Add user to numberVisited and numberLiked for that restaurant (look at numberSuggested)
        //if visited and not liked: same but like as no and add only to numberVisited for that restaurant
        //else do nothing


        RecyclerView restaurantList = (RecyclerView) findViewById(R.id.restaurant_list);
        restaurantList.setAdapter(adapter);
        restaurantList.setLayoutManager(new LinearLayoutManager(this));
        adapter.notifyDataSetChanged();


    }

    @Override
    protected void onStart() {
        super.onStart();
        adapter.startListening();
    }

    @Override
    protected void onStop() {
        super.onStop();
        adapter.stopListening();
    }
}
