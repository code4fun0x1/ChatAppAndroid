package com.example.shashank.enigmaproxy.firebaseimplementation;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.shashank.enigmaproxy.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class SingleFriendChatFragment extends Fragment {


    TextView tt;
    View mainView;

    public SingleFriendChatFragment() {
        // Required empty public constructor
    }




    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

//
//        cameraAction=(ImageButton)findViewById(R.id.camera_action);
//        cameraAction.setOnClickListener(this);
//        // setting.setOnClickListener(this);
//        prosetting.setOnClickListener(this);
//
//
//        etmessage = (EditText) findViewById(R.id.etMessage);
//        bsend = (FloatingActionButton) findViewById(R.id.bsend);
        mainView=inflater.inflate(R.layout.fragment_single_friend_chat, container, false);
        tt=(TextView)mainView.findViewById(R.id.tt);
        return mainView;
    }


    public void receiveUID(String uid){
        tt.setText(uid);
    }

}
