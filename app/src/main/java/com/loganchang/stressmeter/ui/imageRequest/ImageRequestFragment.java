package com.loganchang.stressmeter.ui.imageRequest;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;

import androidx.fragment.app.Fragment;

import com.loganchang.stressmeter.GridAdapter;
import com.loganchang.stressmeter.ImageResponseActivity;
import com.loganchang.stressmeter.PSM;
import com.loganchang.stressmeter.R;
import com.loganchang.stressmeter.SoundAlert;

/**
 * Fragment for "image choosing" page with the grid
 */
public class ImageRequestFragment extends Fragment {
    //static keys
    public static final String GRID_ID_KEY = "grid id";
    public static final String PICTURE_ID_KEY = "pic id";
    public static final String SHARED_PREFERENCE_NAME = "shared prefs";

    //instance variables
    private int mGridID;
    private SharedPreferences mSharedPreferences;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        //retrieve last known grid id
        mSharedPreferences = getActivity().getSharedPreferences(SHARED_PREFERENCE_NAME, getContext().MODE_PRIVATE);
        mGridID = mSharedPreferences.getInt(GRID_ID_KEY, 0);
        Log.d("MY_TAG", "onCreateView: mGridID = " + mGridID);

        //inflate view and instantiate associated widgets
        View view = inflater.inflate(R.layout.fragment_image_request, container, false);
        GridView gridView = (GridView) view.findViewById(R.id.image_grid_view);
        Button moreImagesButton = (Button) view.findViewById(R.id.more_image_button);

        //bind and set grid adapter
        GridAdapter adapter = new GridAdapter(getContext(), PSM.getGridById(mGridID));
        gridView.setAdapter(adapter);

        //create click listener for grid images
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //intent to image response activity for the selected image
                Log.d("MY_TAG", "onItemClick: grid id = "+mGridID);
                Log.d("MY_TAG", "onItemClick: position = "+position);
                Intent intent = new Intent(getActivity(), ImageResponseActivity.class);
                intent.putExtra(GRID_ID_KEY, mGridID);
                intent.putExtra(PICTURE_ID_KEY, position);

                startActivity(intent);
            }
        });

        //click listener for 'more images' button
        moreImagesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //stop the media alarm
                SoundAlert.endAlarm();
                //increment grid id and set grid adapter to new images
                mGridID = (mGridID + 1) % 3;
                GridAdapter adapter = new GridAdapter(getContext(), PSM.getGridById(mGridID));
                gridView.setAdapter(adapter);
            }
        });
        return view;
    }


    /**
     * Save the last seen grid by its ID in shared preferences
     */
    public void onPause() {
        mSharedPreferences = getActivity().getSharedPreferences(SHARED_PREFERENCE_NAME, getContext().MODE_PRIVATE);
        SharedPreferences.Editor editor = mSharedPreferences.edit();
        editor.clear();
        editor.putInt(GRID_ID_KEY, mGridID);
        Log.d("MY_TAG", "onPause: saving mGridID: " + mGridID);
        editor.apply();
        super.onPause();
    }

}