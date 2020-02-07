package com.example.voicemeeting;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Environment;
import android.os.Message;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static android.Manifest.permission.RECORD_AUDIO;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;

public class RecordingActivity extends Activity {

    @SuppressLint("StaticFieldLeak")
    public static Context currentContext ;
    private ImageButton stopButton,recordButton;

    private TextView textMessage;
    private ListView listView;
    private EditText editText, editPort,editIp;
    ExecutorService exec = Executors.newCachedThreadPool();
    public static final int REQUEST_AUDIO_PERMISSION_CODE = 1;
    private String pathSave = null;
    private static AudioRecorder recorder;
    private List<String> stringList;
//    private SpeechAPI speechAPI;
//    private VoiceRecorder mVoiceRecorder;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recording);
        binder();

    }

    private void binder(){
        recordButton = findViewById(R.id.recording_btn_start);
        stopButton = findViewById(R.id.recording_btn_pause);
        MyBtnClicker myBtnClicker = new MyBtnClicker();
        recordButton.setOnClickListener(myBtnClicker);
        stopButton.setOnClickListener(myBtnClicker);

    }

    private class MyBtnClicker implements View.OnClickListener{
        @Override
        public void onClick(View view) {
            switch (view.getId()){
                case R.id.recording_btn_start:
                    startRecord();
                    break;

                case R.id.recording_btn_pause:
                    stopRecord();
//                    uploadAudio(pathSave);
                    break;
            }
        }
    }


    private void startRecord(){
        System.out.println("1");
//        if(CheckPermissions()) {
            pathSave = getPath(System.currentTimeMillis()+ "audio.wav");
            recorder = new AudioRecorder(pathSave);
            recorder.startRecording();
            System.out.println("2");
            Toast.makeText(getApplicationContext(), "Recording Started", Toast.LENGTH_LONG).show();
//        }
//        else {
//            RequestPermissions();
//        }
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

    private void stopRecord(){
        recorder.stopRecording();
        Toast.makeText(getApplicationContext(), "Recording Stopped", Toast.LENGTH_LONG).show();
    }

    private void uploadAudio(String path){
        try {
//            TcpUploadClient client = new TcpUploadClient(editIp.getText().toString(),getPort(editPort.getText().toString()));
            TcpUploadClient client = new TcpUploadClient("10.13.120.182",7788);
            client.sendFile(path);
        }catch (Exception e) {
            e.printStackTrace();
        }
    }

    public boolean CheckPermissions() {
        int result = ContextCompat.checkSelfPermission(getApplicationContext(), WRITE_EXTERNAL_STORAGE);
        int result1 = ContextCompat.checkSelfPermission(getApplicationContext(), RECORD_AUDIO);
        return result == PackageManager.PERMISSION_GRANTED && result1 == PackageManager.PERMISSION_GRANTED;
    }

    private void RequestPermissions() {
        ActivityCompat.requestPermissions(RecordingActivity.this, new String[]{RECORD_AUDIO, WRITE_EXTERNAL_STORAGE}, REQUEST_AUDIO_PERMISSION_CODE);
    }

    private int getPort(String msg){
        if (msg.equals("")){
            msg = "1234";
        }
        return Integer.parseInt(msg);
    }
}
