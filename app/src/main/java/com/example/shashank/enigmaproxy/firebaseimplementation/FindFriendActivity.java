package com.example.shashank.enigmaproxy.firebaseimplementation;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.arlib.floatingsearchview.FloatingSearchView;
import com.example.shashank.enigmaproxy.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class FindFriendActivity extends AppCompatActivity {

    private FloatingSearchView mSearchView;
    private DatabaseReference userDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find_friend);

        userDatabase= FirebaseDatabase.getInstance().getReference().child("users");
        userDatabase.keepSynced(true);
        mSearchView=(FloatingSearchView)findViewById(R.id.floating_search_view);
        mSearchView.setOnHomeActionClickListener(new FloatingSearchView.OnHomeActionClickListener() {

            @Override
            public void onHomeClicked() {
                finish();
            }
        });
        mSearchView.setOnQueryChangeListener(new FloatingSearchView.OnQueryChangeListener() {
            @Override
            public void onSearchTextChanged(String oldQuery, final String newQuery) {


                userDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                        for (DataSnapshot snapshot: dataSnapshot.getChildren()) {
                            mSearchView.swapSuggestions(null);
                          //  imagesDir.add(imageSnapshot.child("address").getValue(String.class));
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });


                 //pass an arraylist
            }
        });




//        FragmentManager fragmentManager = getSupportFragmentManager();
//        FragmentTransaction fragmentTransaction=fragmentManager.beginTransaction();
//        fragmentTransaction.replace(R.id.entire_view,new FindFriendFragment());
//        fragmentTransaction.commit();
    }
}
