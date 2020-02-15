package com.app.androidkt.VoiceMeeting;

import okhttp3.*;

import java.io.*;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class AndroidHTTPUtils {
    public HttpResponse doPost(String url, String uuid, String filename, String filePath) throws IOException {


        OkHttpClient client = new OkHttpClient.Builder().connectTimeout(30, TimeUnit.SECONDS).
                writeTimeout(30, TimeUnit.SECONDS).readTimeout(30, TimeUnit.SECONDS).build();

        RequestBody requestBody = new MultipartBody.Builder().setType(MultipartBody.FORM)
                .addFormDataPart("uuid", uuid)
                .addFormDataPart("file", filename,
                        RequestBody.create(new File(filePath), MediaType.parse("audio/wav")))
                .build();

        Request request = new Request.Builder()
                .url(url)
                .post(requestBody)
                .build();

        try (Response response = client.newCall(request).execute()) {
            return new HttpResponse(response);
        }
    }

    public HttpResponse doGet(String url) throws IOException {
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder().url(url).get().build();

        try (Response response = client.newCall(request).execute()) {
            return new HttpResponse(response);
        }
    }

    public void debug2(){
        OkHttpClient client = new OkHttpClient();
        String url = "http://45.113.235.106/wave_factory/?uuid=3511qf-c682-4198-aef8-3449f7e89630";
//        String url = "https://reqres.in/api/users";

        //RequestBody body = RequestBody.create(JSON, json.toString());
        Request request = new Request.Builder().url(url).build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if(response.isSuccessful()){
                    String myResponse = response.body().string();
                    System.out.println(myResponse);


                }
            }
        });
    }

    public HttpResponse doDelete(String url) throws IOException {
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder().url(url).delete().build();

        try (Response response = client.newCall(request).execute()) {
            return new HttpResponse(response);
        }

    }

    public class HttpResponse {
        private int responseCode;
        private String responseBody;

        public HttpResponse(Response response) throws IOException {
            responseCode = response.code();
            responseBody = response.body().string();
        }

        public int getResponseCode() {
            return responseCode;
        }

        public String getResponseBody() {
            return responseBody;
        }
    }

    public static void main(String[] args) throws IOException {
        String url = "http://45.113.235.106/wave_factory/";
        String header = "";
        String fileUUid = "3511qf-c682-4198-aef8-3449f7e89630";
        String filePath = "output.wav";

        AndroidHTTPUtils httpUtils = new AndroidHTTPUtils();
        HttpResponse response = httpUtils.doPost(url, fileUUid, filePath, filePath);

//        url = url + "?uuid=" + fileUUid;
//        HttpResponse response = httpUtils.doGet(url);
//        HttpResponse response = httpUtils.doDelete(url);

        System.out.println(response.getResponseCode());
        System.out.println(response.getResponseBody());

    }
}
