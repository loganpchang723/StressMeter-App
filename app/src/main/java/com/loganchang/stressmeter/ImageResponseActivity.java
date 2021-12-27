package com.loganchang.stressmeter;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.loganchang.stressmeter.ui.imageRequest.ImageRequestFragment;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

public class ImageResponseActivity extends AppCompatActivity {
    public static final HashMap<Integer, Integer> STRESS_SCORE = new HashMap<>();

    //instance widgets
    private ImageView mImageView;
    private int mGridID;
    private int mPicID;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_response);

        //set up stress levels per position in the grid
        STRESS_SCORE.put(0, 6);
        STRESS_SCORE.put(1, 8);
        STRESS_SCORE.put(2, 14);
        STRESS_SCORE.put(3, 16);
        STRESS_SCORE.put(4, 5);
        STRESS_SCORE.put(5, 7);
        STRESS_SCORE.put(6, 13);
        STRESS_SCORE.put(7, 15);
        STRESS_SCORE.put(8, 2);
        STRESS_SCORE.put(9, 4);
        STRESS_SCORE.put(10, 10);
        STRESS_SCORE.put(11, 12);
        STRESS_SCORE.put(12, 1);
        STRESS_SCORE.put(13, 3);
        STRESS_SCORE.put(14, 9);
        STRESS_SCORE.put(15, 11);

        //stop the media alarm
        SoundAlert.endAlarm();

        //instantiate widgets
        mGridID = getIntent().getIntExtra(ImageRequestFragment.GRID_ID_KEY, -1);
        mPicID = getIntent().getIntExtra(ImageRequestFragment.PICTURE_ID_KEY, -1);
        mImageView = (ImageView) findViewById(R.id.image_request_view);
        mImageView.setImageResource(PSM.getGridById(mGridID)[mPicID]);

    }

    /**
     * Kill activity when cancel button clicked
     *
     * @param view View
     */
    public void onCancelClick(View view) {
        this.finish();
    }

    /**
     * Save the stress level of the picture chosen and kill the app when submit is clicked
     *
     * @param view View
     */
    public void onSubmitClick(View view) {
        //get the csv file's uri and both read and write to it
        Uri csvUri = FileProvider.getUriForFile(this, "com.loganchang.stressmeter",
                new File(getExternalFilesDir(null), "stress_timestamp.csv"));
        File csvFile = new File(csvUri.getPath());
        BufferedWriter out = null;
        BufferedReader in = null;
        try {
            //create file reader and writer
            out = new BufferedWriter(new FileWriter(csvFile.getAbsoluteFile(), true));
            in = new BufferedReader(new FileReader(csvFile.getAbsoluteFile()));
            //format the unix timestamp into human readable form
            SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy hh:mm aa");
            String newEntry = "";
            //if the file isn't empty, append a new line with newly chosen data
            if (in.readLine() != null) newEntry += "\n";
//                out.append("\n" + sdf.format(new Date(System.currentTimeMillis())) + "," +
//                        (STRESS_SCORE.get(mPicID)));
//            //
//            else {
//                Log.d("MY_TAG", "onSubmitClick: new file writing");
//                out.write(sdf.format(new Date(System.currentTimeMillis())) + "," +
//                        (STRESS_SCORE.get(mPicID)));
//            }
            newEntry += sdf.format(new Date(System.currentTimeMillis())) + "," +
                    (STRESS_SCORE.get(mPicID));
            out.append(newEntry);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            //close the reader and writer; kill the app
            try {
                if (in != null) in.close();
                if (out != null) out.close();
                this.finishAffinity();
                System.exit(0);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}