package com.example.voicemeeting;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import org.achartengine.GraphicalView;
import org.achartengine.model.XYMultipleSeriesDataset;
import org.achartengine.model.XYSeries;
import org.achartengine.renderer.XYMultipleSeriesRenderer;
import org.achartengine.renderer.XYSeriesRenderer;

public class ResultActivity extends AppCompatActivity {

    private GraphicalView chart;
    private XYMultipleSeriesDataset dateset = new XYMultipleSeriesDataset();
    private XYMultipleSeriesRenderer renderer = new XYMultipleSeriesRenderer();
    private XYSeries currentSeries;
    private XYSeriesRenderer currentrenderer;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);
    }

    private initChart(){
        currentSeries = new XYSeries("sample data");
        
    }
}
