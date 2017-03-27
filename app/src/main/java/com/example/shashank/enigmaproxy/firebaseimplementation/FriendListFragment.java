package com.example.shashank.enigmaproxy.firebaseimplementation;


import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.shashank.enigmaproxy.R;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.mikhaellopez.circularimageview.CircularImageView;
import com.squareup.picasso.Picasso;

import java.util.Date;

/**
 * A simple {@link Fragment} subclass.
 */
public class FriendListFragment extends Fragment {


    public static final String TAG="FriendListFragment";

    View mainView;
    RecyclerView recyclerView;
    CircularImageView profileThumbnail;
    LinearLayoutManager layoutManager;
     DatabaseReference friendRef,mRef,usersRef;
     StorageReference profileStorage,mediaStorage;
     FirebaseAuth mAuth;
    LayoutInflater inflater;
    FirebaseAuth.AuthStateListener mAuthListener;


    public FriendListFragment() {
        // Required empty public constructor
    }

    private FriendClick mListener=null;

    public interface FriendClick{
        void onFriendClicked(UserModel uid);
    }

    public void setOnFriendClickListener(FriendClick listener){
        this.mListener=listener;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        this.inflater=inflater;
        mainView=inflater.inflate(R.layout.fragment_friend_list, container, false);
        recyclerView=(RecyclerView)mainView.findViewById(R.id.mainbody);
        mAuth=FirebaseAuth.getInstance();
        mAuthListener=new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                if(firebaseAuth.getCurrentUser()==null){

                }
            }
        };
        try {
            mRef = FirebaseDatabase.getInstance().getReference();
            usersRef = mRef.child("users");
            friendRef = usersRef.child(mAuth.getCurrentUser().getUid()).child("friends");
            usersRef.keepSynced(true);
            friendRef.keepSynced(true);
        }catch (Exception e){
            Log.d(TAG, "onCreateView: "+e.toString());
        }
        FireAdapter adapter=new FireAdapter(UserModel.class,0,null,null);
        layoutManager=new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);
        profileStorage= FirebaseStorage.getInstance().getReference("profilepic");
        return mainView;
    }



    public class FireAdapter extends FirebaseRecyclerAdapter<UserModel,RecyclerView.ViewHolder> {


        /**
         * @param modelClass      Firebase will marshall the data at a location into
         *                        an instance of a class that you provide
         * @param modelLayout     This is the layout used to represent a single item in the list.
         *                        You will be responsible for populating an instance of the corresponding
         *                        view with the data from an instance of modelClass.
         * @param viewHolderClass The class that hold references to all sub-views in an instance modelLayout.
         * @param ref             The Firebase location to watch for data changes. Can also be a slice of a location,
         *                        using some combination of {@code limit()}, {@code startAt()}, and {@code endAt()}.
         */
        public FireAdapter(Class<UserModel> modelClass, int modelLayout, Class<FireHolderImage> viewHolderClass, Query ref) {
            super(UserModel.class, R.layout.card_friend_list, RecyclerView.ViewHolder.class, friendRef);
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View v=null;

            v=inflater.inflate(R.layout.card_friend_list,parent,false);

            return new FireHolderImage(v);
        }

        @Override
        protected void populateViewHolder(final RecyclerView.ViewHolder viewHolder, final UserModel model, int position) {

            final FireHolderImage holder=(FireHolderImage) viewHolder;
            holder.name.setText(model.getName());
            if(position==getItemCount()-1){

            }

            updatePresenceStatus(model,holder);

            if(mAuth.getCurrentUser()!=null) {

                usersRef.child(model.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        String profilePicPath;
                        profilePicPath = String.valueOf(dataSnapshot.child("propic").getValue());
                        if (!profilePicPath.equals("default"))
                            Picasso.with(getActivity().getApplicationContext()).load(profilePicPath).fit().centerCrop().into(holder.profileTumbnail);

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

                holder.v.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(model==null){
                            Toast.makeText(getActivity().getApplicationContext(),"Null in FriendList",Toast.LENGTH_SHORT).show();
                        }else {
                            mListener.onFriendClicked(model);

                        }
                    }
                });

            }


        }




    }


    public class FireHolderImage extends RecyclerView.ViewHolder{

        View v,seperator;
        CircularImageView profileTumbnail;
        TextView name,status;


        public FireHolderImage(View itemView) {
            super(itemView);
            v=itemView;
            profileTumbnail=(CircularImageView)v.findViewById(R.id.profileThumbnail);
            name=(TextView) v.findViewById(R.id.et_name);
            status=(TextView) v.findViewById(R.id.et_status);

        }



    }


    void updatePresenceStatus(UserModel model , final FireHolderImage holder){

        // since I can connect from multiple devices, we store each connection instance separately
// any time that connectionsRef's value is null (i.e. has no children) I am offline
        final FirebaseDatabase database = FirebaseDatabase.getInstance();
        final DatabaseReference myConnectionsRef = database.getReference().child("users").child(model.getUid()).child("connections");

// stores the timestamp of my last disconnect (the last time I was seen online)
        final DatabaseReference lastOnlineRef = database.getReference().child("users").child(model.getUid()).child("lastonline");

        myConnectionsRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                if(dataSnapshot.getValue(Boolean.class)){
                    holder.status.setTextColor(Color.GREEN);
                    holder.status.setText("Online");
                }else {
                    holder.status.setTextColor(Color.RED);

                    holder.status.setText(("Offline"));
                }

            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                holder.status.setText("Offline");
                holder.status.setTextColor(Color.RED);

                lastOnlineRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        Date d=new Date(dataSnapshot.getValue(Long.class));
                        holder.status.setTextColor(Color.LTGRAY);
                        holder.status.setText(d.toString());
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }


    @Override
    public void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
        if(mAuth.getCurrentUser()==null){
        }
    }

    @Override
    public void onStop() {
        mAuth.removeAuthStateListener(mAuthListener);
        super.onStop();
    }

}
