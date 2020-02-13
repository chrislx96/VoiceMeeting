package com.app.androidkt.VoiceMeeting;
import android.app.Application;

import java.util.ArrayList;

public class DataPasser extends Application {
    ArrayList<Float> startTime;
    ArrayList<String> utterences;
    public ArrayList<Float> getStartTime() {
        return startTime;
    }

    public ArrayList<String> getUtterences() {
        return utterences;
    }

    public void setStartTime(ArrayList<Float> startTime) {

        this.startTime = startTime;

    }

    public  void setUtterences(ArrayList<String> utterences){
        this.utterences =utterences;
    }



    @Override

    public void onCreate() {

        // TODO Auto-generated method stub

        super.onCreate();

        setStartTime(null);
        setUtterences(null);

    }

}
