package com.ody.di.ui.model;

import android.net.Uri;

/**
 * Represents a data model for analysis, holding a name and associated image URI.
 * Each instance of this class can be used to represent an analysis entry with its associated image.
 *
 * @author Debidutt Prasad
 */
public class AnalysesModel {

    private String name;    // The name associated with the analysis
    private Uri imageUri;   // The URI pointing to the image associated with the analysis

    /**
     * Constructs a new instance of the {@code AnalysesModel} with the specified name and image URI.
     *
     * @param name    The name of the analysis.
     * @param imageUri The URI pointing to the associated image.
     */
    public AnalysesModel(String name, Uri imageUri) {
        this.name = name;
        this.imageUri = imageUri;
    }

    /**
     * Sets the name of the analysis.
     *
     * @param name The name to set.
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Sets the URI pointing to the associated image.
     *
     * @param imageUri The image URI to set.
     */
    public void setImageUri(Uri imageUri) {
        this.imageUri = imageUri;
    }

    /**
     * Returns the name of the analysis.
     *
     * @return The name of the analysis.
     */
    public String getName() {
        return name;
    }

    /**
     * Returns the URI pointing to the associated image.
     *
     * @return The image URI.
     */
    public Uri getImageUri() {
        return imageUri;
    }
}

