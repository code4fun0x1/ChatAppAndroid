package com.example.shashank.enigmaproxy;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

/**
 * Created by Shashank on 06-01-2017.
 */

public class ProgressFragment extends android.app.DialogFragment {

    private ProgressBar pBar;


    void updateProgressbar(int progress){
        pBar.setProgress(progress);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View v=inflater.inflate(R.layout.progress,null);
        pBar= (ProgressBar) v.findViewById(R.id.progressBar2);
        setCancelable(false);
        return v;
    }
}
