package com.musala.atmosphere.httprequest;

import java.io.IOException;
import java.net.HttpURLConnection;

import android.os.AsyncTask;
import android.util.Log;

/**
 * Class responsible for sending HTTP POST requests in background to a given endpoint.
 * 
 * @author filareta.yordanova
 *
 */
public class HttpPostRequest extends AsyncTask<String, String, Void> {
    private final static String LOG_TAG = HttpPostRequest.class.getSimpleName();

    // params[0] - uri, params[1] - json string for post data
    @Override
    protected Void doInBackground(String... params) {
        HttpRequest request = new HttpRequest(HttpRequestMethod.POST, HttpURLConnection.HTTP_CREATED, params[0]);

        try {
            request.openConnection();
            request.setData(params[1]);
            if (!request.isSuccessful()) {
                Log.e(LOG_TAG, String.format("Request to %s with post data %s failed with response code %d.",
                                             params[0],
                                             params[1],
                                             request.getResponseCode()));
            }
        } catch (IOException e) {
            Log.e(LOG_TAG, String.format("Request to endpoint %s with post data %s failed.", params[0], params[1]));
        } finally {
            request.closeConnection();
        }

        return null;
    }
}
