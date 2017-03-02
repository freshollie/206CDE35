package uk.ac.coventry.m206cde.tutorial3.group5.disasterzone;

import android.os.AsyncTask;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;

/**
 * Created by Freshollie on 02/03/2017.
 */

public class DatabaseDownloaderService extends AsyncTask<DatabaseDownloaderService.DownloadCallback, Void, String> {
    private static String DATABASE_URL = "https://raw.githubusercontent.com/freshollie/206CDE35/master/project/disaster_database.json?token=AK1i3JXr0E5fsOa1t7HI5clTQ-pfGkckks5YwUZpwA%3D%3D";

    public interface DownloadCallback {
        void onStarted();
        void onComplete(String stringJsonDatabase);
    }

    private DownloadCallback callback;

    protected String doInBackground(DownloadCallback... callbacks) {
        String newStringJsonDatabase = "";

        callback = callbacks[0];

        try {
            URL url = new URL(DATABASE_URL);
            callback.onStarted();
            URLConnection connection = url.openConnection();

            InputStream inStream = connection.getInputStream();
            ByteArrayOutputStream byteOutStream = new ByteArrayOutputStream();
            int readByte;

            //read the bytes one-by-one from the inputstream to the buffer.

            while (true) {
                readByte = inStream.read();
                if (readByte == -1) { // End of the stream
                    break;
                }
                byteOutStream.write(readByte);
            }
            byteOutStream.flush();
            inStream.close();
            byteOutStream.close();

            newStringJsonDatabase = byteOutStream.toString();

        } catch (IOException e) {
            e.printStackTrace();
        }

        return newStringJsonDatabase;
    }

    protected void onPostExecute(String result) {
        callback.onComplete(result);
    }
}