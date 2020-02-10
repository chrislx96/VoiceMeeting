package com.app.androidkt.VoiceMeeting;


import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class RecordingActivity extends AppCompatActivity {


    @SuppressLint("StaticFieldLeak")
    public static Context currentContext ;
    private Button stopButton,recordButton;
    private Button historyButton, resultButton;
    private EditText editText, editPort,editIp;
    ExecutorService exec = Executors.newCachedThreadPool();
    public static final int REQUEST_AUDIO_PERMISSION_CODE = 1;
    private String pathSave = null;
    public static final String TAG = "MainActivity";

    private static final int RECORD_REQUEST_CODE = 101;
    TextView status;
    TextView textMessage;
    ListView listView;

    private List<String> stringList;
    private SpeechAPI speechAPI;
    private VoiceRecorder mVoiceRecorder;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recording);
        binder();
        stringList = new ArrayList<>();
        adapter = new ArrayAdapter(RecordingActivity.this,
                android.R.layout.simple_list_item_1, stringList);
        listView = (ListView) findViewById(R.id.listview);
        textMessage = (TextView) findViewById(R.id.textMessage);
        status = (TextView) findViewById(R.id.status);
        listView.setAdapter(adapter);

    }

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

    private class MyBtnClicker implements View.OnClickListener{
        @Override
        public void onClick(View view) {
            switch (view.getId()){
                case R.id.recording_btn_start:
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
                    break;
                case R.id.recording_btn_result:
                    break;
                case R.id.recording_btn_history:
                    Intent intent = new Intent(RecordingActivity.this, HistoryActivity.class);
                    startActivity(intent);
            }
        }
    }


    private String getPath(String nameOfFile) {
        FileOutputStream b = null;
        File file = new File(getFileDirectory());
        file.mkdirs();
        String pathName = getFileDirectory() + nameOfFile;
        return pathName;
    }

    private String getFileDirectory(){
        return (Environment.getExternalStorageDirectory().getAbsolutePath() + "/SpkDiarization/");
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
                                    System.out.println("1");
                                    adapter.notifyDataSetChanged();
                                } else {
                                    textMessage.setText(text);
                                }
                            }
                        });
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
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == RECORD_REQUEST_CODE) {
            if (grantResults.length == 0 && grantResults[0] == PackageManager.PERMISSION_DENIED
                    && grantResults[0] == PackageManager.PERMISSION_DENIED) {
                finish();
            } else {
                startVoiceRecorder();
            }
        }
    }

}