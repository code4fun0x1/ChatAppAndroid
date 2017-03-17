package com.example.shashank.enigmaproxy.firebaseimplementation;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.arlib.floatingsearchview.FloatingSearchView;
import com.example.shashank.enigmaproxy.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class FindFriendActivity extends AppCompatActivity {

    private static final String TAG = "FindFriendActivity" ;
    private FloatingSearchView mSearchView;
    private DatabaseReference userDatabase;
    private FirebaseAuth mAuth;
    private ArrayList<UserModel> friends=new ArrayList<>();
    FindRecycler adapter;
    LinearLayoutManager layoutManager;
    RecyclerView listFriend;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find_friend);
        listFriend=(RecyclerView)findViewById(R.id.friend_search_recycler);
        adapter=new FindRecycler();
        layoutManager=new LinearLayoutManager(this);
        listFriend.setLayoutManager(layoutManager);
        listFriend.setAdapter(adapter);
        mAuth=FirebaseAuth.getInstance();
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

                friends.clear();
              //  Log.d(TAG, "\n onSearchTextChanged: "+newQuery);
                userDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                        for (DataSnapshot snapshot: dataSnapshot.getChildren()) {
                            Log.d(TAG, "onDataChange: "+snapshot.toString()+"\n");
                            UserModel user=new UserModel();
                            user.setUid(snapshot.getKey());
                            if((String)snapshot.child("email").getValue()!=null)
                            user.setEmail((String)snapshot.child("email").getValue());
                            if((String)snapshot.child("name").getValue()!=null)
                            user.setName((String)snapshot.child("name").getValue());
                            user.setUid(snapshot.getKey());
                            if((String)snapshot.child("propic").getValue()!=null)
                            user.setPropic((String)snapshot.child("propic").getValue());
                            try{
                                if(user.getEmail().toLowerCase().contains(newQuery)){
                                    friends.add(user);
                                    continue;
                                }

                            }catch (Exception e){

                            }
                            if(user.getName().toLowerCase().contains(newQuery)){
                                friends.add(user);
                            }



                            //mSearchView.swapSuggestions(null);
                          //  imagesDir.add(imageSnapshot.child("address").getValue(String.class));
                        }
                        adapter.notifyDataSetChanged();
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


    private class FindHolder extends RecyclerView.ViewHolder{

        TextView name,email;
        View v;
        ImageView propic;
        ImageButton requestButton;

        public FindHolder(View itemView) {
            super(itemView);
            v=itemView;
            name=(TextView)v.findViewById(R.id.ff_name);
            email=(TextView)v.findViewById(R.id.ff_email);
            propic=(ImageView) v.findViewById(R.id.ff_propic);
            requestButton=(ImageButton) v.findViewById(R.id.ff_request);

        }
    }

    private class FindRecycler extends RecyclerView.Adapter<FindHolder> {

        @Override
        public FindHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View v=getLayoutInflater().inflate(R.layout.find__friend_card,parent,false);
            FindHolder holder=new FindHolder(v);
            return holder;
        }

        @Override
        public void onBindViewHolder(FindHolder holder, final int position) {

            holder.name.setText(friends.get(position).getName());
            holder.email.setText(friends.get(position).getEmail());
            Picasso.with(getApplicationContext()).load(friends.get(position).getPropic()).fit().centerCrop().into(holder.propic);
            holder.requestButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    userDatabase.child(mAuth.getCurrentUser().getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            //Toast.makeText(FindFriendActivity.this,friends.get(position).getUid(),Toast.LENGTH_LONG).show();
                            UserModel newRequest=new UserModel(
                                    (String)dataSnapshot.child("name").getValue()
                                    , mAuth.getCurrentUser().getUid()
                                    ,(String)dataSnapshot.child("propic").getValue()
                                    ,mAuth.getCurrentUser().getEmail());

                            userDatabase.child(friends.get(position).getUid()).child("requests").child(mAuth.getCurrentUser().getUid()).setValue(newRequest);
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });




                }
            });

        }

        @Override
        public int getItemCount() {
            return friends.size();
        }
    }


}
