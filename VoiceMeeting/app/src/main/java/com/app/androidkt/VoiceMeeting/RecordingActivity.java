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
import android.widget.Toast;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import static com.app.androidkt.VoiceMeeting.VoiceRecorder.filePath;


// Recording activity provide an control panel to start and stop the recording as well as check the
// result of the diarization from the server.
public class RecordingActivity extends AppCompatActivity {

    Button historyButton,resultButton,recordButton,stopButton;
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
    private boolean isReceived = false;
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
                                    // if the speech recognition is final then record the content of
                                    // the speech for diarization result display.
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

    // bind the view and the button, and set the button listener.
    private void binder(){
        historyButton = findViewById(R.id.recording_btn_history);
        resultButton = findViewById(R.id.recording_btn_result);
        recordButton = findViewById(R.id.recording_btn_start);
        stopButton = findViewById(R.id.recording_btn_pause);
        MyBtnClicker myBtnClicker = new MyBtnClicker();
        historyButton.setOnClickListener(myBtnClicker);
        resultButton.setOnClickListener(myBtnClicker);
        recordButton.setOnClickListener(myBtnClicker);
        stopButton.setOnClickListener(myBtnClicker);
    }

    // Get the time elapsed from the start of the recording.
    private float getTimeElapsed(){
        long currentTime = System.currentTimeMillis();
        return (float) (currentTime - start)/1000;
    }

    private class MyBtnClicker implements View.OnClickListener{
        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.recording_btn_start:
                    // record the start time of the recording to calculate the duration of each speech.
                    final long startT = System.currentTimeMillis();
                    start = startT;
                    // clear the content and start time of each speech every time the recording start.
                    utterences.clear();
                    startTime.clear();
                    stringList.clear();
                    // start the Google API
                    speechAPI = new SpeechAPI(RecordingActivity.this);
                    if (isGrantedPermission(Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED) {
                        startVoiceRecorder();
                    } else {
                        makeRequest(Manifest.permission.RECORD_AUDIO);
                    }
                    speechAPI.addListener(mSpeechServiceListener);
                    DataPasser myDP = (DataPasser) getApplication();
                    myDP.setCurrentResult("");
                    break;
                case R.id.recording_btn_pause:
                    stopVoiceRecorder();
                    // Stop Cloud Speech API
                    speechAPI.removeListener(mSpeechServiceListener);
                    speechAPI.destroy();
                    speechAPI = null;

                    Thread ts = new Thread(new SendFile());
                    ts.start();
                    Toast.makeText(RecordingActivity.this, "Audio uploading", Toast.LENGTH_LONG).show();
                    historyButton.setEnabled(false);
                    resultButton.setEnabled(false);
                    // sleep for 2s wait for upload
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                Thread.sleep(1000); // sleep 2s
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                            RecordingActivity.this.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    historyButton.setEnabled(true);
                                    resultButton.setEnabled(true);
                                }
                            });
                        }
                    }).start();
                    break;
                case R.id.recording_btn_result:
                    // start receive new file thread
                    if(!isReceived){
                        ReceiveFile myReceiveFile = new ReceiveFile();
                        Thread thread = new Thread(myReceiveFile);
                        thread.start();
                        isReceived =true;
                    }

                    myDP = (DataPasser) getApplication();
                    // If the diarization result is not ready from the server, the application will
                    // tell the user to wait util the client get a not null response from the server.
                    if ("null".equals(myDP.getCurrentResult())||
                            "".equals(myDP.getCurrentResult())||
                            ":\"null\"}".equals(myDP.getCurrentResult().split("\"result\"")[1])) {
                        Toast.makeText(RecordingActivity.this, "Audio processing, please try again later", Toast.LENGTH_LONG).show();
                    }else{
                        // let the application wait for another 5 seconds to ensure the result is ready.
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    Thread.sleep(500);
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
                                // start the result page showing the diarization result in the form of a pie chart
                                Intent intent1 = new Intent(RecordingActivity.this, ResultActivity.class);
                                startActivity(intent1);
                            }
                        }).start();
                        // delete file on backend
                        DeleteFile myDeleteFile = new DeleteFile();
                        Thread thread_delete = new Thread(myDeleteFile);
                        thread_delete.start();
                        isReceived = false;
                    }
                    break;
                case R.id.recording_btn_history:
                    // Once the history button is clicked, pass the data to the history activity
                    // and present the diarization in the form of timeline which shows the start time
                    // and content of each speech.
                    Intent intent = new Intent(RecordingActivity.this, HistoryActivity.class);
                    myDP = (DataPasser) getApplication();
                    // Set the start time and content of each speech and pass it to the history activities.
                    myDP.setStartTime(startTime);
                    myDP.setUtterences(utterences);
                    startActivity(intent);
            }
        }
    }


    private final VoiceRecorder.Callback mVoiceCallback = new VoiceRecorder.Callback() {
        // Trigger the Google speech to text API once the voice is detected.
        @Override
        public void onVoiceStart() {
            if (speechAPI != null) {
                startTime.add(getTimeElapsed());
                speechAPI.startRecognizing(mVoiceRecorder.getSampleRate());
            }
        }
        // send the voice data to the google for the speech recognition result.
        @Override
        public void onVoice(byte[] data, int size) {
            if (speechAPI != null) {
                speechAPI.recognize(data, size);
            }
        }
        // once the voice is no longer detected, turn of the speech AIP recognition.
        @Override
        public void onVoiceEnd() {
            if (speechAPI != null) {
                speechAPI.finishRecognizing();
            }
        }
    };

    // Check permission
    private int isGrantedPermission(String permission) {
        return ContextCompat.checkSelfPermission(this, permission);
    }

    // Make request
    private void makeRequest(String permission) {
        ActivityCompat.requestPermissions(this, new String[]{permission}, RECORD_REQUEST_CODE);
    }

    // Method to start the recording of voice.
    private void startVoiceRecorder() {
        if (mVoiceRecorder != null) {
            mVoiceRecorder.stop();
        }
        mVoiceRecorder = new VoiceRecorder(mVoiceCallback);
        mVoiceRecorder.start();
    }

    // Method to stop recording.
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

    // A new thread to send the wav file to the server.
    class SendFile implements Runnable {
        @Override
        public void run() {
            String serverUrl = "http://45.113.235.106/wave_factory/";
            final String fileUUid = UUID.randomUUID().toString();
            // set the uuid of the current file and pass the uuid to VoiceRecorder Class.
            DataPasser myDP = (DataPasser) getApplication();
            myDP.setUuid(fileUUid);
            File audioFile = new File(filePath);
            AndroidHTTPUtils httpUtils = new AndroidHTTPUtils();
            try {
                AndroidHTTPUtils.HttpResponse response = httpUtils.doPost(serverUrl, fileUUid, audioFile.getName(), filePath);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    // A new thread to receive the diarization result in the format of json from the server.
    class ReceiveFile implements Runnable {
        private volatile String result = "";
        @Override
        public void run() {
            String serverUrl = "http://45.113.235.106/wave_factory/?uuid=";
            DataPasser myDP = (DataPasser) getApplication();
            String fileUUid = myDP.getUuid();
            serverUrl +=fileUUid;
            AndroidHTTPUtils httpUtils = new AndroidHTTPUtils();
            while("".equals(result)
                    || "fail".equals(result)
                    || "null".equals(result)
                    || ":\"null\"}".equals(result.split("\"result\"")[1])){
                result = httpUtils.doGet(serverUrl);
            }
            myDP.setCurrentResult(result);
        }
        public String getResult(){
                return result;
        }
    }

    // A new thread to send the delete file request, specify the file by using the uuid.
    class DeleteFile implements Runnable {
        private volatile String result = "";
        @Override
        public void run() {
            try {
                Thread.sleep(50000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            String serverUrl = "http://45.113.235.106/wave_factory/?uuid=";
            DataPasser myDP = (DataPasser) getApplication();
            String fileUUid = myDP.getUuid();
            serverUrl +=fileUUid;
            AndroidHTTPUtils httpUtils = new AndroidHTTPUtils();
            try {
                httpUtils.doDelete(serverUrl);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        public String getResult(){
            return result;
        }
    }
}

