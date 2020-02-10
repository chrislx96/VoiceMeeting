package com.app.androidkt.VoiceMeeting;

import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import org.qap.ctimelineview.TimelineRow;
import org.qap.ctimelineview.TimelineViewAdapter;

import java.util.ArrayList;
import java.util.Date;

public class HistoryActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);


        // Create Timeline rows List
        ArrayList<TimelineRow> timelineRowsList = new ArrayList<>();

        addTimelibeRows(timelineRowsList, 0, new Date(), "lixiang", "hey how are you");
        addTimelibeRows(timelineRowsList, 1, new Date(), "liuweihong", "fuck you");


//        // Create new timeline row (Row Id)
//        TimelineRow myRow = new TimelineRow(0);
//
//        // To set the row Date (optional)
//        myRow.setDate(new Date());
//        // To set the row Title (optional)
//        myRow.setTitle("Title");
//        // To set the row Description (optional)
//        myRow.setDescription("“1 plus 5 ram”的图片搜索结果" +
//                "The device comes with a Qualcomm Snapdragon 835 chipset clocked at 2.45 GHz, with up to 8 GB RAM and 128 GB storage. It has a 3300 mAh battery with OnePlus' proprietary Dash Charge technology and a 1080p AMOLED display with DCI-P3 wide colour gamut.");
//        // To set the row bitmap image (optional)
//        myRow.setImage(BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher));
//        // To set row Below Line Color (optional)
//        myRow.setBellowLineColor(Color.argb(255, 0, 0, 0));
//        // To set row Below Line Size in dp (optional)
//        myRow.setBellowLineSize(6);
//        // To set row Image Size in dp (optional)
//        myRow.setImageSize(40);
//        // To set background color of the row image (optional)
//        myRow.setBackgroundColor(Color.argb(255, 0, 0, 0));
//        // To set the Background Size of the row image in dp (optional)
//        myRow.setBackgroundSize(60);
//        // To set row Date text color (optional)
//        myRow.setDateColor(Color.argb(255, 0, 0, 0));
//        // To set row Title text color (optional)
//        myRow.setTitleColor(Color.argb(255, 0, 0, 0));
//        // To set row Description text color (optional)
//        myRow.setDescriptionColor(Color.argb(255, 0, 0, 0));
//
//
//
//        // Add the new row to the list
//        timelineRowsList.add(myRow);

        // Create the Timeline Adapter
        ArrayAdapter<TimelineRow> myAdapter = new TimelineViewAdapter(this, 0, timelineRowsList,
                //if true, list will be sorted by date
                false);

        // Get the ListView and Bind it with the Timeline Adapter
        ListView myListView = (ListView) findViewById(R.id.timeline_listView);
        myListView.setAdapter(myAdapter);
    }

    // add timeline rows function
    private void addTimelibeRows(ArrayList<TimelineRow> timelineRowsList, int id, Date date, String title, String content){
        // Create new timeline row (Row Id)
        TimelineRow myRow = new TimelineRow(id);

        // To set the row Date (optional)
        myRow.setDate(date);
        // To set the row Title (optional)
        myRow.setTitle(title);
        // To set the row Description (optional)
        myRow.setDescription(content);
        // To set the row bitmap image (optional)
        myRow.setImage(BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher));
        // To set row Below Line Color (optional)
        myRow.setBellowLineColor(Color.argb(255, 0, 0, 0));
        // To set row Below Line Size in dp (optional)
        myRow.setBellowLineSize(6);
        // To set row Image Size in dp (optional)
        myRow.setImageSize(40);
        // To set background color of the row image (optional)
        myRow.setBackgroundColor(Color.argb(255, 0, 0, 0));
        // To set the Background Size of the row image in dp (optional)
        myRow.setBackgroundSize(60);
        // To set row Date text color (optional)
        myRow.setDateColor(Color.argb(255, 0, 0, 0));
        // To set row Title text color (optional)
        myRow.setTitleColor(Color.argb(255, 0, 0, 0));
        // To set row Description text color (optional)
        myRow.setDescriptionColor(Color.argb(255, 0, 0, 0));

        timelineRowsList.add(myRow);
    }
}
