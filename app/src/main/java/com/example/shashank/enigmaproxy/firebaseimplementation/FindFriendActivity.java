package com.example.shashank.enigmaproxy.firebaseimplementation;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.arlib.floatingsearchview.FloatingSearchView;
import com.example.shashank.enigmaproxy.R;

public class FindFriendActivity extends AppCompatActivity {

    private FloatingSearchView mSearchView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find_friend);

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

                //get suggestions based on newQuery

                //pass them on to the search view



                mSearchView.swapSuggestions(null); //pass an arraylist
            }
        });




//        FragmentManager fragmentManager = getSupportFragmentManager();
//        FragmentTransaction fragmentTransaction=fragmentManager.beginTransaction();
//        fragmentTransaction.replace(R.id.entire_view,new FindFriendFragment());
//        fragmentTransaction.commit();
    }
}
