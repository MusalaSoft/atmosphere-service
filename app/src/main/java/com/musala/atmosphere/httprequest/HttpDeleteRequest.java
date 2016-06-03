package com.musala.atmosphere.httprequest;

import java.io.IOException;
import java.net.HttpURLConnection;

import android.os.AsyncTask;
import android.util.Log;

/**
 * Class responsible for sending HTTP DELETE requests in background to a given endpoint.
 *
 * @author filareta.yordanova
 *
 */
public class HttpDeleteRequest extends AsyncTask<String, String, Boolean> {
    private final static String LOG_TAG = HttpDeleteRequest.class.getSimpleName();

    @Override
    protected Boolean doInBackground(String... params) {
        HttpRequest request = new HttpRequest(HttpRequestMethod.DELETE, HttpURLConnection.HTTP_NO_CONTENT, params[0]);
        Boolean isSuccessful = false;
        try {
            request.openConnection();
            isSuccessful = request.isSuccessful();
            if (!isSuccessful) {
                Log.e(LOG_TAG,
                      String.format("Delete request to endpoint %s failed with response code %d.",
                                    params[0],
                                    request.getResponseCode()));
            }
        } catch (IOException e) {
            Log.e(LOG_TAG, String.format("Request to endpoint %s failed.", params[0]), e);
        } finally {
            request.closeConnection();
        }

        return isSuccessful;
    }

}
