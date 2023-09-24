package com.ody.di.repository;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.icu.text.SimpleDateFormat;
import android.os.Environment;

import com.ody.di.utils.Constants;

import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.Locale;

import static com.ody.di.utils.Constants.MY_APP_PREFERENCES;

/**
 * Repository class for Image which provides methods related to image file creation
 * and storing/retrieving image paths from SharedPreferences.
 *
 * @author Debidutt Prasad
 */
public class ImageRepository {

    // Reference to the Application, useful for accessing app-wide features like SharedPreferences.
    private final Application application;

    // Constants related to SharedPreferences.
    static final String IMAGE_CAPTURED_PATH = "ImageCapturedPath";

    /**
     * Constructor that initializes the Application reference.
     *
     * @param application The application context used for accessing app-wide features.
     */
    public ImageRepository(Application application) {
        this.application = application;
    }

    /**
     * Method to create a new image file with a unique name based on the current timestamp.
     * The file is created in the external files directory specific to this application.
     *
     * @return The newly created image file.
     */
    public File createImageFile() {
        String imageFileName = "JPEG_" + new SimpleDateFormat(Constants.YYYYMMDDHHMMSS, Locale.getDefault()).format(new Date()) + "_";
        File storageDir = application.getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = null;
        try {
            image = File.createTempFile(imageFileName, ".jpg", storageDir);
        } catch (IOException e) {
            // Print the exception stack trace to log the error.
            e.printStackTrace();
        }
        return image;
    }

    /**
     * Method to save the image path to SharedPreferences for later retrieval.
     *
     * @param path The path of the image to be saved.
     */
    public void saveImagePathToPrefs(String path) {
        SharedPreferences sharedPreferences = application.getSharedPreferences(MY_APP_PREFERENCES, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(IMAGE_CAPTURED_PATH, path);
        editor.apply();
    }

    /**
     * Method to retrieve the saved image path from SharedPreferences.
     *
     * @return The saved image path from SharedPreferences or null if not found.
     */
    public String getImagePathFromPrefs() {
        SharedPreferences sharedPreferences = application.getSharedPreferences(MY_APP_PREFERENCES, Context.MODE_PRIVATE);
        return sharedPreferences.getString(IMAGE_CAPTURED_PATH, null);
    }
}