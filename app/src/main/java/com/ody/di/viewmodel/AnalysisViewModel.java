package com.ody.di.viewmodel;

import android.app.Application;
import android.graphics.Bitmap;
import android.icu.text.SimpleDateFormat;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;

import com.ody.di.database.entities.Analysis;
import com.ody.di.repository.AnalysisRepository;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.tensorflow.lite.examples.detection.tflite.Detector;
import org.tensorflow.lite.examples.detection.tflite.TFLiteObjectDetectionAPIModel;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import androidx.annotation.NonNull;
import androidx.core.content.FileProvider;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import static com.ody.di.utils.Constants.YYYYMMDDHHMMSS;
import static com.ody.di.utils.ImageUtils.rotateBitmap;
import static com.ody.di.utils.ImageUtils.uriToBitmap;

/**
 * ViewModel for handling operations related to analyses.
 * Provides methods for detector operations, image processing, and database operations via AnalysisRepository.
 *
 * @author Debidutt Prasad
 */
public class AnalysisViewModel extends AndroidViewModel {
    private final AnalysisRepository analysisRepository;
    private Detector detector;
    private final Application application;

    // LiveData declarations
    private final MutableLiveData<Boolean> isDetectorInitialized = new MutableLiveData<>();
    private final MutableLiveData<Uri> imageUri = new MutableLiveData<>();
    private final MutableLiveData<Bitmap> processedImage = new MutableLiveData<>();
    private final MutableLiveData<List<Detector.Recognition>> detectedObjects = new MutableLiveData<>();
    private final MutableLiveData<Boolean> deletionSuccess = new MutableLiveData<>();
    private final MutableLiveData<Boolean> saveButtonVisibility = new MutableLiveData<>();
    private final MutableLiveData<Boolean> deleteButtonVisibility = new MutableLiveData<>();
    private final MutableLiveData<Boolean> insertionSuccess = new MutableLiveData<>();

    // Constructor
    public AnalysisViewModel(@NonNull Application application, @NonNull AnalysisRepository analysisRepository) {
        super(application);
        this.analysisRepository = analysisRepository;
        this.application = application;
    }

    // Various LiveData getters and other methods...

    /**
     * Checks if the image path saved in SharedPreferences exists and updates the imageUri LiveData.
     */
    public void checkSavedImagePath() {
        String storedString = analysisRepository.getImageCapturedPath();
        File file = new File(storedString);
        if (file.exists()) {
            imageUri.setValue(Uri.fromFile(file));
        }
    }

    /**
     * Deletes an analysis entry based on its serial number.
     *
     * @param serialNumber The serial number of the analysis entry to be deleted.
     */
    public void deleteAnalysis(long serialNumber) {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.execute(() -> {
            int result = analysisRepository.deleteAnalysisBySerialNumber(serialNumber);
            deletionSuccess.postValue(result > 0);
        });
    }

    /**
     * Conduct inference on the provided image Uri, performing necessary preprocessing.
     *
     * @param uri The Uri of the image to be processed.
     */
    public void doInference(Uri uri) {
        // Conversion and rotation logic...

        Bitmap bitmap = uriToBitmap(this.getApplication(), uri);
        Bitmap rotatedBitmap = rotateBitmap(this.getApplication(), bitmap, uri); // implement this method
        List<Detector.Recognition> results = detector.recognizeImage(rotatedBitmap); // implement detector method
        processedImage.setValue(rotatedBitmap);
        detectedObjects.setValue(results);
    }

    /**
     * Gets the LiveData indicating if the detector is initialized.
     *
     * @return LiveData indicating detector initialization status.
     */
    public LiveData<Boolean> getIsDetectorInitialized() {
        return isDetectorInitialized;
    }

    /**
     * Gets the LiveData of the processed image bitmap.
     *
     * @return LiveData containing the processed image.
     */
    public LiveData<Bitmap> getProcessedImage() {
        return processedImage;
    }

    /**
     * Gets the LiveData of the list of detected objects from the image.
     *
     * @return LiveData containing the list of detected objects.
     */
    public LiveData<List<Detector.Recognition>> getDetectedObjects() {
        return detectedObjects;
    }

    /**
     * Gets the LiveData indicating if the delete button should be visible.
     *
     * @return LiveData indicating delete button visibility status.
     */
    public LiveData<Boolean> getDeleteButtonVisibility() {
        return deleteButtonVisibility;
    }

    /**
     * Gets the LiveData indicating the success status of data insertion.
     *
     * @return LiveData indicating insertion success status.
     */
    public LiveData<Boolean> getInsertionSuccess() {
        return insertionSuccess;
    }

    /**
     * Gets the LiveData of the image URI.
     *
     * @return LiveData containing the image URI.
     */
    public LiveData<Uri> getImageUri() {
        return imageUri;
    }

    /**
     * Gets the LiveData indicating the success status of data deletion.
     *
     * @return LiveData indicating deletion success status.
     */
    public LiveData<Boolean> getDeletionSuccess() {
        return deletionSuccess;
    }


    /**
     * Initializes the object detector by loading the TFLite model.
     * Upon successful initialization, updates the isDetectorInitialized LiveData to true.
     * If there's an error during initialization, sets isDetectorInitialized to false and logs the error.
     */
    public void initializeDetector() {
        new Thread(() -> {
            try {
                detector = TFLiteObjectDetectionAPIModel.create(
                        getApplication(),
                        "efficientdet_lite2_.tflite",
                        "labelfruit.txt",
                        320,
                        true
                );
                isDetectorInitialized.postValue(true);
                Log.d("log", "Detector initialization success");
            } catch (final IOException e) {
                isDetectorInitialized.postValue(false);
                Log.d("Exception", "Error initializing detector: " + e.getMessage());
            }
        }).start();
    }


    /**
     * Fetch analysis data based on the serial number and update LiveData objects accordingly.
     *
     * @param serialNumber The serial number of the analysis entry to be fetched.
     */
    public void fetchAnalysis(long serialNumber) {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.execute(() -> {
            Analysis analysis = this.analysisRepository.getAnalysisBySerialNumber(serialNumber);
            File storageDir = application.getExternalFilesDir(Environment.DIRECTORY_PICTURES);
            imageUri.postValue(FileProvider.getUriForFile(this.getApplication(), this.application.getPackageName() + ".fileprovider", new File(storageDir, analysis.imageReference)));
            Gson gson = new Gson();
            Type listType = new TypeToken<List<Detector.Recognition>>() {
            }.getType();
            List<Detector.Recognition> deserializedList = gson.fromJson(analysis.detectedObjects, listType);
            detectedObjects.postValue(deserializedList);
            deleteButtonVisibility.postValue(true);
            saveButtonVisibility.postValue(false);
        });
    }

    /**
     * Saves an analysis entry with the given recognized objects and image filename.
     *
     * @param list          List of recognized objects.
     * @param imageFileName Name of the image file associated with this analysis.
     */
    public void saveAnalysis(List<Detector.Recognition> list, String imageFileName) {
        saveButtonVisibility.postValue(false);
        Analysis analysis = new Analysis();
        analysis.serialNumber = Long.parseLong(new SimpleDateFormat(YYYYMMDDHHMMSS, Locale.getDefault()).format(new Date()));

        Gson gson = new Gson();
        analysis.detectedObjects = gson.toJson(list);
        analysis.imageReference = imageFileName;
        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.execute(() -> {
            long result = analysisRepository.insertAnalysis(analysis);
            insertionSuccess.postValue(result != -1);
        });
    }
}
