package com.musala.atmosphere.httprequest;

import java.io.IOException;
import java.net.HttpURLConnection;

import android.os.AsyncTask;
import android.util.Log;

/**
 * Class responsible for sending HTTP GET requests in background to a given endpoint.
 * 
 * @author filareta.yordanova
 *
 */
public class HttpGetRequest extends AsyncTask<String, String, String> {
    private final static String LOG_TAG = HttpGetRequest.class.getSimpleName();

    @Override
    protected String doInBackground(String... params) {
        HttpRequest request = new HttpRequest(HttpRequestMethod.GET, HttpURLConnection.HTTP_OK, params[0]);

        try {
            request.openConnection();

            if (request.isSuccessful()) {
                return request.getResponseContent();
            }

            Log.e(LOG_TAG,
                  String.format("GET request to ednpoint %s failed with status code %d.",
                                params[0],
                                request.getResponseCode()));
        } catch (IOException e) {
            Log.e(LOG_TAG, String.format("GET request to ednpoint %s failed.", params[0]));
        }

        return null;
    }
}
