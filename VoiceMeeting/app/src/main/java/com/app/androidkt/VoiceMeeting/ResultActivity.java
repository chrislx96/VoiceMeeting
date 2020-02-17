package com.app.androidkt.VoiceMeeting;

import android.app.Activity;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import android.os.Bundle;

import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.formatter.PercentFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;

import org.achartengine.ChartFactory;
import org.achartengine.GraphicalView;
import org.achartengine.model.CategorySeries;
import org.achartengine.model.SeriesSelection;
import org.achartengine.renderer.DefaultRenderer;
import org.achartengine.renderer.SimpleSeriesRenderer;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Hashtable;

import static com.app.androidkt.VoiceMeeting.JsonReader.readJson;

public class ResultActivity extends Activity {

    String result;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);
        PieChart pieChart = findViewById(R.id.piechart);


        // get input data
        DataPasser myDP = (DataPasser) getApplication();
        result = myDP.getCurrentResult();
        System.out.println("result in result activity class: "+result);
        Hashtable<long[],Integer> finalTimeline = readJson(result);
        Hashtable<Integer,Long> totalDuration = new Hashtable<>();


        for (long[] startEnd:
             finalTimeline.keySet()) {
            long duration = startEnd[1]-startEnd[0];
            int speaker = finalTimeline.get(startEnd);
            if (totalDuration.keySet().contains(speaker)){
                totalDuration.put(speaker,totalDuration.get(speaker)+duration);
            }else {
                totalDuration.put(speaker,duration);
            }
        }


        ArrayList NoOfEmp = new ArrayList();
        ArrayList year = new ArrayList();
        for (int no:
                totalDuration.keySet()) {
            System.out.println(totalDuration.get(no));
            NoOfEmp.add(new Entry(totalDuration.get(no)/1000, no));
            year.add("\""+no+"\"");
        }

        PieDataSet dataSet = new PieDataSet(NoOfEmp, "Duration(s) each speaker");

        PieData data = new PieData(year, dataSet);
        pieChart.setData(data);
        dataSet.setColors(ColorTemplate.COLORFUL_COLORS);
        pieChart.animateXY(3000, 3000);
    }
}
