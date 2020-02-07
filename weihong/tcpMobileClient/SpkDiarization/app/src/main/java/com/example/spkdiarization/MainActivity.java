package com.example.spkdiarization;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Message;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.io.File;
import java.io.FileOutputStream;
import java.lang.ref.WeakReference;
import java.util.Date;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import android.content.pm.PackageManager;
import android.media.MediaRecorder;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.io.IOException;
import static android.Manifest.permission.RECORD_AUDIO;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;

public class MainActivity extends Activity {
    @SuppressLint("StaticFieldLeak")
    public static Context currentContext ;
    private Button startButton,closeButton, cleanSendButton, stopButton,sendButton,recordButton;
    private TextView receivedText,textSent;
    private EditText editText, editPort,editIp;
    private static TcpClient tcpClient = null;
    private final MyHandler myHandler = new MyHandler(this);
    private MyBroadcastReceiver myBroadcastReceiver = new MyBroadcastReceiver();
    ExecutorService exec = Executors.newCachedThreadPool();
    MediaRecorder mediaRecorder = null;
    private MediaRecorder mRecorder;
    public static final int REQUEST_AUDIO_PERMISSION_CODE = 1;
    private String pathSave = null;


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        currentContext = this;
        bindID();
        bindListener();
        bindReceiver();
        Ini();
    }

    private void bindID(){
        startButton = (Button) findViewById(R.id.btn_tcpClientCnn);
        closeButton = (Button) findViewById(R.id.btn_tcpClientClose);
        stopButton = (Button) findViewById(R.id.stopButton);
        cleanSendButton = (Button) findViewById(R.id.btn_tcpCleanClientSend);
        recordButton = (Button) findViewById(R.id.recordButton);
        sendButton = (Button) findViewById(R.id.btn_tcpClientSend);
        editPort = (EditText) findViewById(R.id.edit_tcpClientPort);
        editIp = (EditText) findViewById(R.id.edit_tcpClientIp);
        editText = (EditText) findViewById(R.id.edit_tcpClientSend);
        receivedText = (TextView) findViewById(R.id.txt_ClientRcv);
        textSent = (TextView) findViewById(R.id.txt_ClientSend);
    }

    private void bindListener(){
        MyBtnClicker myBtnClicker = new MyBtnClicker();
        startButton.setOnClickListener(myBtnClicker);
        closeButton.setOnClickListener(myBtnClicker);
        stopButton.setOnClickListener(myBtnClicker);
        cleanSendButton.setOnClickListener(myBtnClicker);
        recordButton.setOnClickListener(myBtnClicker);
        sendButton.setOnClickListener(myBtnClicker);
    }

    private void bindReceiver(){
        IntentFilter intentFilter = new IntentFilter("tcpClientReceiver");
        registerReceiver(myBroadcastReceiver,intentFilter);
    }

    private void Ini(){
        closeButton.setEnabled(false);
        sendButton.setEnabled(false);
    }

    private class MyBtnClicker implements View.OnClickListener{
        @Override
        public void onClick(View view) {
            switch (view.getId()){
                case R.id.btn_tcpClientCnn:
                    startButton.setEnabled(false);
                    closeButton.setEnabled(true);
                    sendButton.setEnabled(true);
                    tcpClient = new TcpClient(editIp.getText().toString(),getPort(editPort.getText().toString()));
                    exec.execute(tcpClient);
                    break;
                case R.id.btn_tcpClientClose:
                    tcpClient.closeSelf();
                    startButton.setEnabled(true);
                    closeButton.setEnabled(false);
                    sendButton.setEnabled(false);
                    break;
                case R.id.stopButton:
//                    receivedText.setText("");
                    System.out.println("2");
                    stopRecord();
                    uploadAudio(pathSave);
                    break;
                case R.id.btn_tcpCleanClientSend:
                    textSent.setText("");
                    break;
                case R.id.recordButton:
                    startRecord();
                    break;
                case R.id.btn_tcpClientSend:
                    Message message = Message.obtain();
                    message.what = 2;
                    message.obj = editText.getText().toString();
                    myHandler.sendMessage(message);
                    exec.execute(new Runnable() {
                        @Override
                        public void run() {
                            tcpClient.send(editText.getText().toString());
                        }
                    });
                    break;
            }
        }
    }

    private class MyHandler extends android.os.Handler{
        private WeakReference<MainActivity> mActivity;
        MyHandler(MainActivity activity){
            mActivity = new WeakReference<MainActivity>(activity);
        }
        @Override
        public void handleMessage(Message msg) {
            if (mActivity != null){
                switch (msg.what){
                    case 1:
                        receivedText.append(msg.obj.toString());
                        break;
                    case 2:
                        textSent.append(msg.obj.toString());
                        break;
                }
            }
        }
    }

    private class MyBroadcastReceiver extends BroadcastReceiver{
        @Override
        public void onReceive(Context context, Intent intent) {
            String mAction = intent.getAction();
            switch (mAction){
                case "tcpClientReceiver":
                    String msg = intent.getStringExtra("tcpClientReceiver");
                    Message message = Message.obtain();
                    message.what = 1;
                    message.obj = msg;
                    myHandler.sendMessage(message);
                    break;
            }
        }
    }

    private void startRecord(){
        if(CheckPermissions()) {
            pathSave = getPath(System.currentTimeMillis()+ "audio.3gp");
            mediaRecorder = new MediaRecorder();
            mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
            mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
            mediaRecorder.setAudioEncoder(MediaRecorder.OutputFormat.AMR_NB);
            mediaRecorder.setOutputFile(pathSave);
            try {
                mediaRecorder.prepare();
            } catch (IOException e) {
                e.printStackTrace();
            }
            mediaRecorder.start();
            Toast.makeText(getApplicationContext(), "Recording Started", Toast.LENGTH_LONG).show();
        }
        else
        {
            RequestPermissions();
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

    private void stopRecord(){
        mediaRecorder.stop();
        mediaRecorder.release();
        mediaRecorder = null;
        Toast.makeText(getApplicationContext(), "Recording Stopped", Toast.LENGTH_LONG).show();
    }

    private void uploadAudio(String path){
        try {
//            TcpUploadClient client = new TcpUploadClient(editIp.getText().toString(),getPort(editPort.getText().toString()));
            TcpUploadClient client = new TcpUploadClient("10.13.246.153",7788); // 启动客户端连接

            client.sendFile(path);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public boolean CheckPermissions() {
        int result = ContextCompat.checkSelfPermission(getApplicationContext(), WRITE_EXTERNAL_STORAGE);
        int result1 = ContextCompat.checkSelfPermission(getApplicationContext(), RECORD_AUDIO);
        return result == PackageManager.PERMISSION_GRANTED && result1 == PackageManager.PERMISSION_GRANTED;
    }
    private void RequestPermissions() {
        ActivityCompat.requestPermissions(MainActivity.this, new String[]{RECORD_AUDIO, WRITE_EXTERNAL_STORAGE}, REQUEST_AUDIO_PERMISSION_CODE);
    }

    private int getPort(String msg){
        if (msg.equals("")){
            msg = "1234";
        }
        return Integer.parseInt(msg);
    }

}
