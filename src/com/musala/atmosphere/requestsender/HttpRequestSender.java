package com.musala.atmosphere.requestsender;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import android.os.AsyncTask;
import android.util.Log;

/**
 * Class responsible for sending http get requests to a given endpoint.
 * 
 * @author filareta.yordanova
 *
 */
public class HttpRequestSender extends AsyncTask<String, String, String> {
    private final static String LOG_TAG = HttpRequestSender.class.getSimpleName();

    @Override
    protected String doInBackground(String... uri) {
        HttpClient httpClient = new DefaultHttpClient();
        String responseContent = null;
        String errorMessage = String.format("Get request to the given endpoint %s failed.", uri[0]);

        try {
            HttpResponse response = httpClient.execute(new HttpGet(uri[0]));
            StatusLine statusLine = response.getStatusLine();

            if (statusLine.getStatusCode() == HttpStatus.SC_OK) {
                ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                response.getEntity().writeTo(outputStream);
                responseContent = outputStream.toString();
                outputStream.close();
            } else {
                // Closes the connection.
                response.getEntity().getContent().close();
                errorMessage = String.format("Request finished with %s, the reason for the failure: %s",
                                             statusLine.getStatusCode(),
                                             statusLine.getReasonPhrase());
                Log.e(LOG_TAG, errorMessage);
            }
        } catch (ClientProtocolException e) {
            Log.e(LOG_TAG, errorMessage, e);
        } catch (IOException e) {
            Log.e(LOG_TAG, errorMessage, e);
        }

        return responseContent;
    }
}
