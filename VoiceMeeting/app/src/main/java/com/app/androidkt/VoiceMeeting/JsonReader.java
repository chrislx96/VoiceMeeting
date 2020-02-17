package com.app.androidkt.VoiceMeeting;

import java.util.Hashtable;

public class JsonReader {

    public static Hashtable<long[],Integer>  readJson(String result){
        String[] sentences = result.split(",");
        String[] temp = new String[sentences.length-1];
        Hashtable<long[],Integer> finalTimeline = new Hashtable<>();
        int numberOfSpeaker = temp.length;
        int i =-1;
        for (String sentence:sentences
        ) {
            sentence=sentence.replaceAll("\"","").replaceAll("\\{","").replaceAll("\\}","");
            sentence = sentence.replaceAll("\\\\","").replaceAll("result:","").replaceAll("uuid:","");
            sentence = sentence.replaceAll("\\s","");
            sentence = sentence.substring(2);
            if (i>=0) temp[i] = sentence;
            i++;
        }
        int speakerNo = 1;
        for (String sentence: temp
        ) {
            String[] timeline = sentence.split(";");
            for (String times:
                    timeline) {
                String[] startEnd = times.split("==>");
                long start = timeConverter(startEnd[0]);
                long end = timeConverter(startEnd[1]);
                long[] startFinish = new long[2];
                startFinish[0] = start;
                startFinish[1] = end;
                finalTimeline.put(startFinish,speakerNo);
//                System.out.println("Speaker "+speakerNo + " start from " + start + " and ends at " + end);
            }
            speakerNo++;
        }
//        System.out.print("Total number of speakers: " + numberOfSpeaker);
        return finalTimeline;
    }


    // String result = "{\"uuid\":\"3511qf-c682-4198-aef8-3449f7e89630\",\"result\":\"{\\\"0\\\": \\\"0:01.24==>0:05.100;0:09.616==>0:10.180\\\", \\\"1\\\": \\\"0:05.100==>0:09.616;0:10.180==>0:12.0\\\"}\"}";

    private static long timeConverter(String time){
        long min;
        long sec;
        long msec;
        String [] minSec = time.split(":" );
        min = Long.parseLong(minSec[0]);
        String[] secMsec = minSec[1].split("\\.");
        sec = Long.parseLong(secMsec[0]);
        msec = Long.parseLong(secMsec[1]);
        return msec + 1000*sec + 60000*min;
    }


}

