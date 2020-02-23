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
import java.util.Hashtable;
import static com.app.androidkt.VoiceMeeting.JsonReader.readJson;

public class HistoryActivity extends AppCompatActivity {

    ArrayList<Float> startTime;
    ArrayList<String> utterences;
    String result;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);
        DataPasser myDP = (DataPasser) getApplication();
        startTime = myDP.getStartTime();
        utterences = myDP.getUtterences();
        result = myDP.getCurrentResult();
        Hashtable<long[],Integer> finalTimeline = readJson(result);


        // Create Timeline rows List
        ArrayList<TimelineRow> timelineRowsList = new ArrayList<>();
        int speakerName = 1;

        Hashtable<Integer,Integer> picture = new Hashtable<>();
        picture.put(1,R.drawable.butters);
        picture.put(2,R.drawable.biggles);
        picture.put(3,R.drawable.cop);
        picture.put(4,R.drawable.marjorine);
        picture.put(5,R.drawable.squirrel);
        picture.put(6,R.drawable.hitler);
        picture.put(7,R.drawable.cartman);
        
        for (int i =0; i<utterences.size();i++) {
            for (long [] startEnd:
                 finalTimeline.keySet()) {
                long start = Math.round(startTime.get(i)*1000);

                if(isThisUtterence(startEnd,start)){
                    speakerName = finalTimeline.get(startEnd);
                }
            }

            int pictureId = picture.get(speakerName);
            addTimelibeRows(timelineRowsList, i, "Time: "+String.valueOf(startTime.get(i)) + " s", "Speaker "+speakerName+ ": "+utterences.get(i), pictureId);
        }

        // Create the Timeline Adapter
        ArrayAdapter<TimelineRow> myAdapter = new TimelineViewAdapter(this, 0, timelineRowsList,
                //if true, list will be sorted by date
                false);

        // Get the ListView and Bind it with the Timeline Adapter
        ListView myListView = (ListView) findViewById(R.id.timeline_listView);
        myListView.setAdapter(myAdapter);
    }

    // add timeline rows function
    private void addTimelibeRows(ArrayList<TimelineRow> timelineRowsList, int id, String title, String content, int picId){
        // Create new timeline row (Row Id)
        TimelineRow myRow = new TimelineRow(id);
        // To set the row Date (optional)
//        myRow.setDate(date);
        // To set the row Title (optional)
        myRow.setTitle(title);
        // To set the row Description (optional)
        myRow.setDescription(content);
        // To set the row bitmap image (optional)
        myRow.setImage(BitmapFactory.decodeResource(getResources(), picId));
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
    private static boolean isThisUtterence(long[] startEnd, long startTime){
        if (startTime>= startEnd[0]&&startTime<=startEnd[1]){
            return true;
        }else {
            return false;
        }

    }
}
