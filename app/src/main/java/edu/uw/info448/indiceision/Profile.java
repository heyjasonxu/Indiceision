package edu.uw.info448.indiceision;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
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

        if(getIntent().getStringExtra("yesGoodId") != null){
            String id = getIntent().getStringExtra("yesGoodId");
            String name = getIntent().getStringExtra("yesGoodName");
            Log.v(TAG, "This is the intent id and intent name " + id + " " + name);
            userRestaurant newRestaurant = new userRestaurant(name, "Yes");
            mDatabase.child("users").child(currentUser.getUid()).push().setValue(newRestaurant);
            mDatabase.child("restaurants").child(id).child("numberVisited").child(currentUser.getUid()).setValue(currentUser.getUid());
            mDatabase.child("restaurants").child(id).child("numberLiked").child(currentUser.getUid()).setValue(currentUser.getUid());

        }else if (getIntent().getStringExtra("yesBadId") != null){
            String id = getIntent().getStringExtra("yesBadId");
            String name = getIntent().getStringExtra("yesBadName");
            userRestaurant newRestaurant = new userRestaurant(name, "No");
            mDatabase.child("users").child(currentUser.getUid()).push().setValue(newRestaurant);
            mDatabase.child("restaurants").child(id).child("numberVisited").child(currentUser.getUid()).setValue(currentUser.getUid());
            Log.v(TAG, "This is the intent id and intent name " + id + " " + name);

        }


//            }else if(bundle.getString("yesBadId") != null){
//                String id = bundle.getString("yesBadId");
//                String restaurantName = bundle.getString("yesBadName");
//            }
//        }



        //if visted and liked: mDatabase.child("users").child(currentUser.getUid()).push.setValue(userRestaurant)
        // with liked as yes. Add user to numberVisited and numberLiked for that restaurant (look at numberSuggested)
        //if visited and not liked: same but like as no and add only to numberVisited for that restaurant
        //else do nothing


        RecyclerView restaurantList = (RecyclerView) findViewById(R.id.restaurant_list);
        restaurantList.setAdapter(adapter);
        restaurantList.setLayoutManager(new LinearLayoutManager(this));
        adapter.notifyDataSetChanged();


    }

    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()){
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
                                Toast.makeText(Profile.this, "Signed out", Toast.LENGTH_SHORT).show();
                            }
                        });
                startActivity(new Intent(this, Introduction.class));
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
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
