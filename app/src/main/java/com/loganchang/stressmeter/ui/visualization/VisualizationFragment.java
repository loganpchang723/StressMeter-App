package com.loganchang.stressmeter.ui.visualization;

import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;

import com.loganchang.stressmeter.R;
import com.loganchang.stressmeter.SoundAlert;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import lecho.lib.hellocharts.model.Axis;
import lecho.lib.hellocharts.model.Line;
import lecho.lib.hellocharts.model.LineChartData;
import lecho.lib.hellocharts.model.PointValue;
import lecho.lib.hellocharts.view.LineChartView;

/**
 * Fragment for "Results" page
 */
public class VisualizationFragment extends Fragment {
    //instance widgets
    private LineChartView mGraph;
    private TableLayout mTable;

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState){
        //inflate view
        View view =  inflater.inflate(R.layout.fragment_visualization, container, false);

        //stop media alarm
        SoundAlert.endAlarm();

        //instantiate widgets
        mGraph = (LineChartView) view.findViewById(R.id.graph);
        mTable = (TableLayout) view.findViewById(R.id.table);

        //get the URI for the csv data table
        Uri csvUri = FileProvider.getUriForFile(getContext(),
                "com.loganchang.stressmeter",
                new File(getActivity().getExternalFilesDir(null), "stress_timestamp.csv"));

        //start worker thread to make graph
        GraphThread graphThread = new GraphThread(csvUri);
        graphThread.start();

        //start worker thread to make table
        TableThread tableThread = new TableThread(csvUri);
        tableThread.start();

        return view;
    }


    /**
     * Worker thread for making graph
     */
    private class GraphThread extends Thread{
        Uri csvUri;
        LineChartData data;

        /**
         * Constructor takes in path to csv URI
         * @param csvUri    csv file's URI
         */
        public GraphThread(Uri csvUri){
            this.csvUri = csvUri;
        }

        //runnable to show the graph
        private final Runnable runnable = new Runnable() {
            @Override
            public void run() {
                mGraph.setLineChartData(data);
            }
        };

        //handler to post runnable in main thread queue
        private final Handler handler = new Handler(Looper.getMainLooper());

        public void run(){
            BufferedReader in = null;
            try {
                //read the csv and stores its data
                in = new BufferedReader(new FileReader(new File(csvUri.getPath())
                        .getAbsoluteFile()));
                List<PointValue> values = new ArrayList<>();
                int i = 0;
                String inLine;
                while((inLine = in.readLine()) != null){
                    values.add(new PointValue(i, Integer.parseInt(inLine.split(",")[1])));
                    i++;
                }

                //create a Line object for the data
                Line line = new Line(values).setColor(Color.BLUE).setFilled(true);
                ArrayList<Line> lines = new ArrayList<>();
                lines.add(line);
                data = new LineChartData();
                data.setLines(lines);

                //create graph axis
                Axis xAxis = new Axis().setName("Instances");
                Axis yAxis = new Axis().setName("Stress Level").setHasLines(true);
                data.setAxisXBottom(xAxis);
                data.setAxisYLeft(yAxis);

                //post compelted graph data to main thread
                handler.post(runnable);
            } catch (IOException e){
                e.printStackTrace();
            } finally {
                //close the file reader
                try{
                    in.close();
                } catch (IOException | NullPointerException e){
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * Worker thread to make the table
     */
    private class TableThread extends Thread{
        Uri csvUri;
        List<TableRow> rowList;

        /**
         * Constructor takes the csv file's URI
         * @param csvUri    csv file's URI
         */
        public TableThread(Uri csvUri){
            this.csvUri = csvUri;
            rowList = new ArrayList<>();
        }

        //runnable to put table in main thread
        private final Runnable runnable = new Runnable() {
            @Override
            public void run() {
                Log.d("MY_TAG", "in runnable");
                for(TableRow row: rowList) {
                    mTable.addView(row);
                    mTable.requestLayout();
                }
            }
        };

        //handler to put runnable in main thread queue
        private final Handler handler = new Handler(Looper.getMainLooper());

        public void run(){
            BufferedReader in = null;
            try{
                //read in csv file content
                in = new BufferedReader(new FileReader(new File(csvUri.getPath())
                        .getAbsoluteFile()));
                String inLine;
                while ((inLine = in.readLine()) != null) {
                    //create a new row in the table for each line in the csv file
                    //row will also have certain formatting applied
                    TableRow tableRow = new TableRow(getContext());

                    TableRow.LayoutParams lp = new TableRow.LayoutParams(0,
                            TableRow.LayoutParams.WRAP_CONTENT);
                    lp.weight = 1;
                    TableLayout.LayoutParams tableRowParams= new TableLayout.LayoutParams
                                    (TableLayout.LayoutParams.MATCH_PARENT,
                                            TableLayout.LayoutParams.WRAP_CONTENT);
                    int leftMargin=10;
                    int topMargin=10;
                    int rightMargin=10;
                    int bottomMargin=10;
                    tableRowParams.setMargins(leftMargin, topMargin, rightMargin, bottomMargin);
                    tableRow.setLayoutParams(tableRowParams);

                    String[] row = inLine.split(",");
                    //input and format first column: time
                    TextView t1v = new TextView(getContext());
                    t1v.setText(""+row[0]);
                    t1v.setTextColor(Color.BLACK);
                    t1v.setLayoutParams(lp);
                    t1v.setGravity(Gravity.CENTER);
                    tableRow.addView(t1v);

                    //input and format second column: stress level
                    TextView t2v = new TextView(getContext());
                    t2v.setText(""+row[1]);
                    t2v.setLayoutParams(lp);
                    t2v.setTextColor(Color.BLACK);
                    t2v.setGravity(Gravity.CENTER);
                    tableRow.addView(t2v);

                    //add this row
                    rowList.add(tableRow);
                }
                handler.post(runnable);
                Log.d("MY_TAG", "read all the rows");
            } catch (IOException e){
                e.printStackTrace();
            } finally {
                //close the input reader
                try{
                    in.close();
                } catch (IOException | NullPointerException e){
                    e.printStackTrace();
                }
            }
        }
    }
}
