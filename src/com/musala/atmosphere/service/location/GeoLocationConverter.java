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
