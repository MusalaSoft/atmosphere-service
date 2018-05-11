// This file is part of the ATMOSPHERE mobile testing framework.
// Copyright (C) 2016 MusalaSoft
//
// ATMOSPHERE is free software: you can redistribute it and/or modify
// it under the terms of the GNU General Public License as published by
// the Free Software Foundation, either version 3 of the License, or
// (at your option) any later version.
//
// ATMOSPHERE is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
// GNU General Public License for more details.
//
// You should have received a copy of the GNU General Public License
// along with ATMOSPHERE.  If not, see <http://www.gnu.org/licenses/>.

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
