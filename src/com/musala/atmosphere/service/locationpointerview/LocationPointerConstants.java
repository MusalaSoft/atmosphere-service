package com.musala.atmosphere.service.locationpointerview;

/**
 * Enum containing constants for the location pointer functionality.
 * 
 * @author yavor.stankov
 *
 */
public enum LocationPointerConstants {
    CENTER_POINT_INTENT_NAME("com.musala.atmosphere.service.locationpointerview.EXTRA_POINT_LOCATION");

    private String value;

    private LocationPointerConstants(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    @Override
    public String toString() {
        return value;
    }
}
