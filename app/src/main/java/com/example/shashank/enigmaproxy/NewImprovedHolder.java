package com.example.shashank.enigmaproxy;

import android.content.Context;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.view.ContextMenu;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.mikhaellopez.circularimageview.CircularImageView;

/**
 * Created by Shashank on 04-09-2016.
 */
public class NewImprovedHolder extends RecyclerView.ViewHolder implements View.OnCreateContextMenuListener {

    View v;

    CircularImageView circularimage;
   // TextView title;
    TextView content;
    ImageView imagecontent;
    ImageButton loadImageButton,overflowButton;
    ProgressBar progressbar;
    FrameLayout secondryImageLayout;
    Context context;
    PopupMenu menu=null;


    public NewImprovedHolder(View view, final Context context) {
        super(view);
        this.context=context;
        this.v=view;
//        circularimage=(CircularImageView) v.findViewById(R.id.networkImageView);
//        imagecontent=(ImageView)v.findViewById(R.id.ivContent);
//       // title=(TextView)v.findViewById(R.id.tvName);
//        secondryImageLayout=(FrameLayout)v.findViewById(R.id.secondry_imagelayout);
//
//        content=(TextView)v.findViewById(R.id.tvContent);
//       // loadImageButton=(ImageButton)v.findViewById(R.id.imageloadButton);
//        progressbar=(ProgressBar)v.findViewById(R.id.progressBar);
//       // v.setOnCreateContextMenuListener(this);

    }



    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        menu.setHeaderTitle("OPTIONS");
        menu.add(0,v.getId(),1,"Delete");
    }







}
