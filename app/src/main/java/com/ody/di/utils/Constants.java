package com.ody.di.utils;

/**
 * Contains the application's constant values that are used across different components.
 * Provides a centralized location for easier management and modification of these values.
 *
 * @author Debidutt Prasad
 */
public final class Constants {

    /**
     * Represents the origin activity identifier for analyses.
     */
    public static final String ORIGIN_ANALYSES_ACTIVITY = "analyses_activity";

    /**
     * Key used for passing and retrieving a serial number.
     */
    public static final String SERIAL_NUMBER = "serialNumber";

    /**
     * Name of the application's shared preferences.
     */
    public static final String MY_APP_PREFERENCES = "MyAppPreferences";

    /**
     * Identifier for the file provider associated with object detection images.
     */
    public static final String OBJECTDETECTIONIMAGES_FILEPROVIDER = "com.ody.di.fileprovider";

    /**
     * Date format used in the application.
     */
    public static final String YYYYMMDDHHMMSS = "yyyyMMddHHmmss";

    // Private constructor to prevent instantiation.
    private Constants() {
    }
}

