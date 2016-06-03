package com.musala.atmosphere.httprequest;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Class responsible for handling most commonly used HTTP request types.
 * 
 * @author filareta.yordanova
 *
 */
public class HttpRequest {
    private static final String ENCODING_FORMAT = "UTF-8";

    private static final String CONTENT_TYPE = "application/json";

    private static final int RESPONSE_BUFFER_SIZE = 512;

    private HttpRequestMethod requestMethod;

    private int successCode;

    private HttpURLConnection urlConnection;

    private String endpoint;

    private InputStream inputStream;

    private OutputStream outputStream;

    /**
     * Creates an HTTP request for the given request method, response code, which is considered to be successful and the
     * endpoint of the request.
     * 
     * @param requestMethod
     *        - HTTP request method type
     * @param successCode
     *        - successful response code for the request
     * @param urlString
     *        - request endpoint
     */
    public HttpRequest(HttpRequestMethod requestMethod, int successCode, String urlString) {
        this.requestMethod = requestMethod;
        this.successCode = successCode;
        this.endpoint = urlString;
    }

    /**
     * Opens an HTTP connection to the endpoint given when constructing the request.
     * 
     * @throws IOException
     *         if opening fails
     */
    public void openConnection() throws IOException {
        URL url = new URL(endpoint);
        urlConnection = (HttpURLConnection) url.openConnection();
        urlConnection.setRequestMethod(requestMethod.toString());
    }

    /**
     * Sets request data. Used when sending POST requests.
     * 
     * @param json
     *        - request data to be set in JSON format
     * @throws IOException
     *         if sending request data fails
     */
    public void setData(String json) throws IOException {
        urlConnection.setDoOutput(true);
        urlConnection.setRequestProperty("Content-Type", CONTENT_TYPE);

        outputStream = urlConnection.getOutputStream();
        outputStream.write(json.getBytes());
        outputStream.flush();
    }

    /**
     * Returns the response content from the request.
     * 
     * @return response content
     * @throws IOException
     *         if retrieving response fails
     */
    public String getResponseContent() throws IOException {
        urlConnection.setDoInput(true);

        if (successCode != urlConnection.getResponseCode()) {
            return null;
        }

        inputStream = urlConnection.getInputStream();
        char[] buffer = new char[RESPONSE_BUFFER_SIZE];
        InputStreamReader reader = new InputStreamReader(inputStream, ENCODING_FORMAT);
        reader.read(buffer);

        return new String(buffer);
    }

    /**
     * Returns the response code from the request.
     * 
     * @return request response code
     * @throws IOException
     *         if retrieving response code fails
     */
    public int getResponseCode() throws IOException {
        return urlConnection.getResponseCode();
    }

    /**
     * Closes all the resources used for the connection and transferring data in the request.
     */
    public void closeConnection() {
        if (urlConnection != null) {
            urlConnection.disconnect();
        }

        try {
            if (inputStream != null) {
                inputStream.close();
            }

            if (outputStream != null) {
                outputStream.close();
            }
        } catch (IOException e) {
        }
    }

    /**
     * Determines whether the request is successful.
     * 
     * @return <code>true</code> if request is successful and <code>false</code> otherwise
     */
    public boolean isSuccessful() {
        try {
            return urlConnection.getResponseCode() == successCode;
        } catch (IOException e) {
            return false;
        }
    }
}
