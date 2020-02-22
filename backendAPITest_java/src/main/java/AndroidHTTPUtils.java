import okhttp3.*;

import java.io.*;
import java.util.Map;
import java.util.UUID;

public class AndroidHTTPUtils {
    /**
     * Http Post Method
     *
     * @param url      String
     * @param uuid     String
     * @param filename String
     * @param filePath String
     * @return HttpResponse
     * @throws IOException
     */
    public HttpResponse doPost(String url, String uuid, String filename, String filePath) throws IOException {
        // create a new client
        OkHttpClient client = new OkHttpClient();

        // create request body
        RequestBody requestBody = new MultipartBody.Builder().setType(MultipartBody.FORM)
                .addFormDataPart("uuid", uuid)
                .addFormDataPart("file", filename,
                        RequestBody.create(new File(filePath), MediaType.parse("audio/wav")))
                .build();

        // create post request
        Request request = new Request.Builder()
                .url(url)
                .post(requestBody)
                .build();

        try (Response response = client.newCall(request).execute()) {
            return new HttpResponse(response);
        }
    }

    /**
     * Http Get Method
     *
     * @param url String
     * @return HttpResponse
     * @throws IOException
     */
    public HttpResponse doGet(String url) throws IOException {
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder().url(url).get().build();

        try (Response response = client.newCall(request).execute()) {
            return new HttpResponse(response);
        }
    }


    /**
     * Http Delete Method
     *
     * @param url String
     * @return HttpResponse
     * @throws IOException
     */
    public HttpResponse doDelete(String url) throws IOException {
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder().url(url).delete().build();

        try (Response response = client.newCall(request).execute()) {
            return new HttpResponse(response);
        }

    }

    /**
     * Nested Class
     * Store response code, body
     */
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

    //==================================================================
    // test functions
    //==================================================================
//    public static void main(String[] args) throws IOException {
//        String uuid = UUID.randomUUID().toString();
//        System.out.println(uuid);
//        System.out.println(uuid.length());

//        String url = "http://43.112.235.106/wave_factory/";
//        String header = "";
//        String fileUUid = "59d619c5-f314-4204-bb13-adb81f7d7ef7";
//        String filePath = "output.wav";

//        AndroidHTTPUtils httpUtils = new AndroidHTTPUtils();
//        HttpResponse response = httpUtils.doPost(url, fleUUid, filePath, filePath);

//        url = url + "?uuid=" + fileUUid;
//        HttpResponse response = httpUtils.doGet(url);
//        HttpResponse response = httpUtils.doDelete(url);
//
//        System.out.println(response.getResponseCode());
//        System.out.println(response.getResponseBody());

//    }
}
