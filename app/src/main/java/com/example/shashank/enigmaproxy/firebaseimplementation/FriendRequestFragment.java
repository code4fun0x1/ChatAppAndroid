package com.example.shashank.enigmaproxy.firebaseimplementation;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.shashank.enigmaproxy.R;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

/**
 * Created by Shashank on 05-03-2017.
 */

public class FriendRequestFragment extends DialogFragment {

    View mainView;
    DatabaseReference tUser;
    FirebaseAuth mAuth;
    FirebaseAuth.AuthStateListener mAuthListener;
    StorageReference mStorage;
    DatabaseReference mDatabase,requestStore,usersStore;
    LayoutInflater inflater;
    RecyclerView recyclerView=null;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        mainView=inflater.inflate(R.layout.fragment_friend_request,container,false);
        setCancelable(true);
        this.inflater=inflater;
        initialize();
        return mainView;
    }

    private void initialize() {

        recyclerView=(RecyclerView)mainView.findViewById(R.id.recycler_requests);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        mAuth=FirebaseAuth.getInstance();
        mAuthListener=new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                if(firebaseAuth.getCurrentUser()==null){
                    dismiss();
                }
            }
        };
        mDatabase= FirebaseDatabase.getInstance().getReference();
        usersStore=mDatabase.child("users");
        requestStore=usersStore.child(mAuth.getCurrentUser().getUid()).child("requests");
        requestStore.keepSynced(true);
        recyclerView.setAdapter(new FireAdapter(null,0,null,requestStore));
        RecyclerView.ItemAnimator itemanimator = new DefaultItemAnimator();
        itemanimator.setAddDuration(400);
        itemanimator.setRemoveDuration(400);
        recyclerView.setItemAnimator(itemanimator);
        recyclerView.setSelected(true);


    }



    public class FireAdapter extends FirebaseRecyclerAdapter<FriendRequestModel,RecyclerView.ViewHolder> {


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
        public FireAdapter(Class<FriendRequestModel> modelClass, int modelLayout, Class<FireHolder> viewHolderClass, Query ref) {
            super(FriendRequestModel.class, R.layout.card_request, RecyclerView.ViewHolder.class, requestStore);
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View v=null;

                v=inflater.inflate(R.layout.card_request,parent,false);
                return new FireHolder(v);

        }

        @Override
        protected void populateViewHolder(final RecyclerView.ViewHolder viewHolder, final FriendRequestModel model, int position) {

           // DatabaseReference ref=requestStore.child(model.getUid());
            final FireHolder holder=(FireHolder)viewHolder;
            holder.nameView.setText(model.getName());
            if(model.getMessage()!=null) {
                if (!model.getMessage().trim().equals(""))
                    holder.messageView.setText(model.getMessage());
            }
            if(mAuth.getCurrentUser()!=null) {
                tUser = usersStore.child(mAuth.getCurrentUser().getUid());
                tUser.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        String profilePicPath;
                        profilePicPath = String.valueOf(dataSnapshot.child("propic").getValue());
                        if (!profilePicPath.equals("default"))
                            Picasso.with(getContext()).load(profilePicPath).fit().centerCrop().into(holder.profileIcon);

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
            }
        }

    }



    public class FireHolder extends RecyclerView.ViewHolder{

        View v;
        ImageView profileIcon;
        TextView nameView;
        TextView messageView;

        public FireHolder(View itemView) {
            super(itemView);
            v=itemView;
            profileIcon= (ImageView) v.findViewById(R.id.request_icon);
            nameView= (TextView) v.findViewById(R.id.request_name);
            messageView= (TextView) v.findViewById(R.id.request_message);
        }



    }





    @Override
    public void onResume() {

        //getDialog().getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        super.onResume();
    }

    @Override
    public void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
        if(mAuth.getCurrentUser()==null){
            dismiss();
        }
    }

    @Override
    public void onStop() {
        mAuth.removeAuthStateListener(mAuthListener);
        super.onStop();
    }
}