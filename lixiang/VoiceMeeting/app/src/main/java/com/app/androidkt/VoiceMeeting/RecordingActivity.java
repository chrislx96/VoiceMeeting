package com.app.androidkt.VoiceMeeting;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


import static com.app.androidkt.VoiceMeeting.VoiceRecorder.filePath;

public class RecordingActivity extends AppCompatActivity {

    private static final int RECORD_REQUEST_CODE = 101;
    ArrayList<Float> startTime = new ArrayList<Float>();
    ArrayList<String> utterences = new ArrayList<String>();
    private VoiceRecorder mVoiceRecorder;
    private List<String> stringList;
    private SpeechAPI speechAPI;
    TextView status;
    TextView textMessage;
    ListView listView;
    long start;
    private ArrayAdapter adapter;
    private final SpeechAPI.Listener mSpeechServiceListener =
            new SpeechAPI.Listener() {
                @Override
                public void onSpeechRecognized(final String text, final boolean isFinal) {
                    if (isFinal) {
                        mVoiceRecorder.dismiss();
                    }
                    if (textMessage != null && !TextUtils.isEmpty(text)) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if (isFinal) {
                                    textMessage.setText(null);
                                    stringList.add(0,text);
                                    utterences.add(text);
                                    adapter.notifyDataSetChanged();
                                } else {
                                    textMessage.setText(text);
                                }
                            }
                        });
                    }
                }
            };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recording);
        binder();
        stringList = new ArrayList<>();
        adapter = new ArrayAdapter(RecordingActivity.this, android.R.layout.simple_list_item_1, stringList);
        listView = findViewById(R.id.listview);
        textMessage = findViewById(R.id.textMessage);
        status = findViewById(R.id.status);
        listView.setAdapter(adapter);
    }

    private void binder(){
        Button historyButton = findViewById(R.id.recording_btn_history);
        Button resultButton = findViewById(R.id.recording_btn_result);
        Button recordButton = findViewById(R.id.recording_btn_start);
        Button stopButton = findViewById(R.id.recording_btn_pause);
        MyBtnClicker myBtnClicker = new MyBtnClicker();
        historyButton.setOnClickListener(myBtnClicker);
        resultButton.setOnClickListener(myBtnClicker);
        recordButton.setOnClickListener(myBtnClicker);
        stopButton.setOnClickListener(myBtnClicker);
    }

    private float getTimeElapsed(){
        long currentTime = System.currentTimeMillis();
        return (float) (currentTime - start)/1000;
    }

    private class MyBtnClicker implements View.OnClickListener{
        @Override
        public void onClick(View view) {
            switch (view.getId()){
                case R.id.recording_btn_start:

                    final long startT = System.currentTimeMillis();
                    start = startT;
                    utterences.clear();
                    startTime.clear();
                    stringList.clear();
                    speechAPI = new SpeechAPI(RecordingActivity.this);
                    if (isGrantedPermission(Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED) {
                        startVoiceRecorder();
                    } else {
                        makeRequest(Manifest.permission.RECORD_AUDIO);
                    }
                    speechAPI.addListener(mSpeechServiceListener);
                    break;

                case R.id.recording_btn_pause:
                    stopVoiceRecorder();
                    // Stop Cloud Speech API
                    speechAPI.removeListener(mSpeechServiceListener);
                    speechAPI.destroy();
                    speechAPI = null;

                    Thread ts = new Thread(new SendFile());
                    ts.start();

                    break;
                case R.id.recording_btn_result:
                    Thread rs = new Thread(new ReceiveFile());
                    rs.start();
                    break;
                case R.id.recording_btn_history:
                    Intent intent = new Intent(RecordingActivity.this, HistoryActivity.class);
//                    System.out.println(Arrays.toString(getStartTime().toArray()));
//                    System.out.println(Arrays.toString(getUtterences().toArray()));

                      DataPasser myDP = (DataPasser) getApplication();
                      myDP.setStartTime(startTime);
                      myDP.setUtterences(utterences);
//                    intent.putExtra("time",getStartTime().toArray());
//                    intent.putExtra("speech",getUtterences().toArray());
                    startActivity(intent);
            }
        }
    }

    private void uploadAudio(String path){
        try {
            TcpUploadClient client = new TcpUploadClient("10.13.120.182",7788);
            client.sendFile(path);
        }catch (Exception e) {
            e.printStackTrace();
        }
    }

    private int getPort(String msg){
        if (msg.equals("")){
            msg = "1234";
        }
        return Integer.parseInt(msg);
    }

    private final VoiceRecorder.Callback mVoiceCallback = new VoiceRecorder.Callback() {
        @Override
        public void onVoiceStart() {
            if (speechAPI != null) {
                startTime.add(getTimeElapsed());
                speechAPI.startRecognizing(mVoiceRecorder.getSampleRate());
            }
        }
        @Override
        public void onVoice(byte[] data, int size) {
            if (speechAPI != null) {
                speechAPI.recognize(data, size);
            }
        }
        @Override
        public void onVoiceEnd() {
            if (speechAPI != null) {
                speechAPI.finishRecognizing();
            }
        }
    };

    private int isGrantedPermission(String permission) {
        return ContextCompat.checkSelfPermission(this, permission);
    }

    private void makeRequest(String permission) {
        ActivityCompat.requestPermissions(this, new String[]{permission}, RECORD_REQUEST_CODE);
    }

    private void startVoiceRecorder() {
        if (mVoiceRecorder != null) {
            mVoiceRecorder.stop();
        }
        mVoiceRecorder = new VoiceRecorder(mVoiceCallback);
        mVoiceRecorder.start();
    }

    private void stopVoiceRecorder() {
        if (mVoiceRecorder != null) {
            mVoiceRecorder.stop();
            mVoiceRecorder = null;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == RECORD_REQUEST_CODE) {
            if (grantResults.length == 0 && grantResults[0] == PackageManager.PERMISSION_DENIED) {
                finish();
            } else {
                startVoiceRecorder();
            }
        }
    }

    public ArrayList<Float> getStartTime(){
        return startTime;
    }
    public ArrayList<String> getUtterences(){
        return utterences;
    }


    class SendFile implements Runnable {

        @Override
        public void run() {
            String serverUrl = "https://45.113.235.106/wave_factory/";
            String fileUUid = "3511qf-c682-4198-aef8-3449f7e89630";
            File audioFile = new File(filePath);
            System.out.println(filePath);
            AndroidHTTPUtils httpUtils = new AndroidHTTPUtils();
            try {
                AndroidHTTPUtils.HttpResponse response = httpUtils.doPost(serverUrl, fileUUid, audioFile.getName(), filePath);
                System.out.println(response.getResponseBody());
            } catch (IOException e) {
                e.printStackTrace();
            }


        }



    }

    class ReceiveFile implements Runnable {

        @Override
        public void run() {
            while(true){
                String serverUrl = "https://45.113.235.106/wave_factory/?uuid=3511qf-c682-4198-aef8-3449f7e89630";
//                String serverUrl = "https://reqres.in/api/users";

                AndroidHTTPUtils httpUtils = new AndroidHTTPUtils();
//                try {
//                    AndroidHTTPUtils.HttpResponse response = httpUtils.doGet(serverUrl);
//                    System.out.println(response.getResponseBody());
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
                httpUtils.debug2();



            }
        }
    }
}

