package com.ody.di.viewmodel;

import android.app.Application;
import android.content.pm.PackageManager;
import android.net.Uri;

import com.ody.di.repository.ImageRepository;
import com.ody.di.utils.Constants;

import java.io.File;

import androidx.core.content.FileProvider;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

/**
 * ViewModel class for handling UI-related data in the Snap module.
 * Responsible for triggering camera actions, handling image capturing and navigation logic.
 *
 * @author Debidutt Prasad
 */
public class SnapViewModel extends ViewModel {

    private final MutableLiveData<Uri> imageUriLiveData = new MutableLiveData<>();
    private final MutableLiveData<Boolean> _navigateToAnalysisLiveData = new MutableLiveData<>();
    public LiveData<Boolean> navigateToAnalysisLiveData = _navigateToAnalysisLiveData;
    private final MutableLiveData<Boolean> permissionDeniedLiveData = new MutableLiveData<>();
    private final MutableLiveData<Boolean> isImageCapturedLiveData = new MutableLiveData<>(false);

    private final ImageRepository imageRepository;
    private final Application application;

    /**
     * Constructor to initialize the SnapViewModel with necessary dependencies.
     *
     * @param application The application context.
     */
    public SnapViewModel(Application application) {
        this.application = application;
        this.imageRepository = new ImageRepository(application);
    }

    /**
     * Retrieves the image URI LiveData.
     *
     * @return LiveData containing the image URI.
     */
    public LiveData<Uri> getImageUriLiveData() {
        return imageUriLiveData;
    }

    /**
     * Retrieves the navigation status to the analysis screen.
     *
     * @return LiveData indicating if navigation should be triggered.
     */
    public LiveData<Boolean> getNavigateToAnalysisLiveData() {
        return navigateToAnalysisLiveData;
    }

    /**
     * Sets the status of image capture.
     *
     * @param status Image capture status to set.
     */
    public void setImageCapturedLiveData(boolean status) {
        isImageCapturedLiveData.postValue(status);
    }

    /**
     * Retrieves the status of image capture.
     *
     * @return LiveData containing the status of image capture.
     */
    public LiveData<Boolean> getIsImageCapturedLiveData() {
        return isImageCapturedLiveData;
    }

    /**
     * Retrieves the LiveData indicating if permission was denied.
     *
     * @return LiveData containing the permission denial status.
     */
    public LiveData<Boolean> getPermissionDeniedLiveData() {
        return permissionDeniedLiveData;
    }

    /**
     * Initiates the camera action.
     */
    public void onCameraAction() {
        createImageAndCapture();
    }

    /**
     * Creates an image and captures it using the camera.
     */
    public void createImageAndCapture() {
        File photoFile = imageRepository.createImageFile();
        if (photoFile != null) {
            imageRepository.saveImagePathToPrefs(photoFile.getAbsolutePath());

            Uri imageUri = FileProvider.getUriForFile(application, Constants.OBJECTDETECTIONIMAGES_FILEPROVIDER, photoFile);
            imageUriLiveData.postValue(imageUri);
        }
    }

    /**
     * Handles the event when the Analyze button is clicked.
     */
    public void onAnalyzeClicked() {
        _navigateToAnalysisLiveData.setValue(true);
    }

    /**
     * Handles the result of permission requests.
     *
     * @param grantResults The results of the permission requests.
     */
    public void handlePermissionsResult(int[] grantResults) {
        boolean allPermissionsGranted = true;
        for (int grantResult : grantResults) {
            if (grantResult != PackageManager.PERMISSION_GRANTED) {
                allPermissionsGranted = false;
                break;
            }
        }
        if (allPermissionsGranted) {
            onCameraAction();
        } else {
            permissionDeniedLiveData.setValue(true);
        }
    }

    /**
     * Resets the navigation status to the analysis screen.
     */
    public void resetNavigateToAnalysisLiveData() {
        _navigateToAnalysisLiveData.setValue(false);
    }
}


