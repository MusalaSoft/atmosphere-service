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

package com.musala.atmosphere.service.location;

import android.location.Location;

import com.musala.atmosphere.commons.util.GeoLocation;

/**
 * A class that handles converting {@link GeoLocation} to {@link Location}.
 * 
 * @author delyan.dimitrov
 * 
 */
public class GeoLocationConverter {

    /**
     * Converts a {@link GeoLocation} object to a {@link Location}.
     * 
     * @param location
     *        - the location to be converted
     * @return a {@link Location} object converted from the passed location
     */
    public static Location getLocation(GeoLocation location) {
        Location convertedLocation = new Location(location.getProvider());
        convertedLocation.setLatitude(location.getLatitude());
        convertedLocation.setLongitude(location.getLongitude());
        convertedLocation.setAccuracy(location.getAccuracy());

        Double altitude = location.getAltitude();
        if (altitude != null) {
            convertedLocation.setAltitude(altitude);
        }

        Float bearing = location.getBearing();
        if (bearing != null) {
            convertedLocation.setBearing(bearing);
        }

        Float speed = location.getSpeed();
        if (speed != null) {
            convertedLocation.setSpeed(speed);
        }

        return convertedLocation;
    }
}
