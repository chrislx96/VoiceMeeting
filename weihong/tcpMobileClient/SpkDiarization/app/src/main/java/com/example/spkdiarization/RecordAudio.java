package com.example.spkdiarization;

import android.os.Environment;
import android.util.Log;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class RecordAudio {
    public boolean uploadFileNew(String sourceFileUri) throws IOException {
        HttpURLConnection connection = null;
        DataOutputStream outputStream = null;
        DataInputStream inputStream = null;

        String lineEnd = "\r\n";
        String twoHyphens = "--";
        String boundary = "*****";

        int bytesRead, bytesAvailable, bufferSize;
        byte[] buffer;
        int maxBufferSize = 1 * 1024 * 1024;
        File upFile = null;

        try {
            final File root = new File((Environment.getExternalStorageDirectory() + File.separator + "DIR_NAME"));


            root.mkdirs();
            final String tmp[] = sourceFileUri.split(File.separator);
            final String fname = tmp[tmp.length - 1];
            upFile = new File(root, fname);


            FileInputStream fileInputStream = new FileInputStream(upFile);


            URL url = new URL("YOUR_URL");
            connection = (HttpURLConnection) url.openConnection();

            // Allow Inputs & Outputs.
            connection.setDoInput(true);
            connection.setDoOutput(true);
            connection.setUseCaches(false);

            // Set HTTP method to POST.
            connection.setReadTimeout(10000);
            connection.setConnectTimeout(15000);
            connection.setRequestMethod("POST");

            connection.setRequestProperty("Connection", "Keep-Alive");

            connection.setRequestProperty("Content-Type", "multipart/form-data;boundary=" + boundary);

            outputStream = new DataOutputStream(connection.getOutputStream());
            outputStream.writeBytes(twoHyphens + boundary + lineEnd);

            outputStream.writeBytes("Content-Disposition: form-data; typ=\"" + 1 + "\"; name=\"" + fname + "\";filename=\"" + fname + "\"" + lineEnd);
            outputStream.writeBytes(lineEnd);

            bytesAvailable = fileInputStream.available();
            bufferSize = Math.min(bytesAvailable, maxBufferSize);
            buffer = new byte[bufferSize];

            // Read file
            bytesRead = fileInputStream.read(buffer, 0, bufferSize);

            while (bytesRead > 0) {
                outputStream.write(buffer, 0, bufferSize);
                bytesAvailable = fileInputStream.available();
                bufferSize = Math.min(bytesAvailable, maxBufferSize);
                bytesRead = fileInputStream.read(buffer, 0, bufferSize);
            }

            outputStream.writeBytes(lineEnd);
            outputStream.writeBytes(twoHyphens + boundary + twoHyphens + lineEnd);

            // Responses from the server (code and message)
            int serverResponseCode = connection.getResponseCode();
            String serverResponseMessage = connection.getResponseMessage();
            InputStream in = connection.getInputStream();

            byte data[] = new byte[1024];
            int counter = -1;

            in.close();


            if (serverResponseCode == 200) {
                Log.d("uploadFile", "File Upload Complete.");
            }

            fileInputStream.close();
            outputStream.flush();
            outputStream.close();


        } catch (Exception ex) {
            Log.d("uploadFile", "File Upload Error:" + ex.toString());

            return false;

        }
        return true;
    }


    
}
