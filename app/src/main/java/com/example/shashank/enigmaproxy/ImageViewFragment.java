package com.example.shashank.enigmaproxy;

import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

/**
 * Created by Shashank on 11-11-2016.
 */

public class ImageViewFragment extends Fragment {

    ImageView holder;


    public static ImageViewFragment newInstance(String path) {

        Bundle args = new Bundle();
        args.putString("path","error");
        ImageViewFragment fragment = new ImageViewFragment();
        fragment.setArguments(args);
        return fragment;
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v=inflater.inflate(R.layout.fragment_imageview,container);
        holder= (ImageView) v.findViewById(R.id.img_holder);
        try {
            holder.setImageBitmap(BitmapFactory.decodeFile(getArguments().getString("path")));
        }catch (Exception e){

        }
        v.setTag("holder");
        return v;
    }


    void dataChange(String path){
        holder.setImageBitmap(BitmapFactory.decodeFile(path));
    }


}
