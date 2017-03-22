package com.example.shashank.enigmaproxy.firebaseimplementation;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.example.shashank.enigmaproxy.R;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
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

/**
 * A simple {@link Fragment} subclass.
 */
public class FriendListFragment extends Fragment {


    View mainView;
    RecyclerView recyclerView;
    CircularImageView profileThumbnail;
    EditText name;
    LinearLayoutManager layoutManager;
    private DatabaseReference friendRef,mRef,usersRef;
    private StorageReference profileStorage,mediaStorage;
    private FirebaseAuth mAuth;
    LayoutInflater inflater;


    public FriendListFragment() {
        // Required empty public constructor
    }

    private FriendClick mListener=null;

    public interface FriendClick{
        void onFriendClicked(String uid);
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
        mRef= FirebaseDatabase.getInstance().getReference();
        usersRef=mRef.child("users");
        friendRef=mRef.child(mAuth.getCurrentUser().getUid()).child("friends");

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
            if(mAuth.getCurrentUser()!=null) {

                usersRef.child(model.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        String profilePicPath;
                        profilePicPath = String.valueOf(dataSnapshot.child("propic").getValue());
                        if (!profilePicPath.equals("default"))
                            Picasso.with(getContext()).load(profilePicPath).fit().centerCrop().into(holder.profileTumbnail);

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
            }


        }




    }


    public class FireHolderImage extends RecyclerView.ViewHolder{

        View v;
        CircularImageView profileTumbnail;
        EditText name,status;


        public FireHolderImage(View itemView) {
            super(itemView);
            v=itemView;
            profileTumbnail=(CircularImageView)v.findViewById(R.id.profileThumbnail);
            name=(EditText)v.findViewById(R.id.et_name);
            status=(EditText)v.findViewById(R.id.et_status);

        }



    }


}
