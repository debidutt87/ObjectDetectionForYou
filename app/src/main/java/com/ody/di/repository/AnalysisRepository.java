package com.ody.di.repository;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;

import com.ody.di.database.AnalysisDao;
import com.ody.di.database.AnalysisDatabase;
import com.ody.di.database.entities.Analysis;
import com.ody.di.utils.Constants;

import java.util.List;

import androidx.lifecycle.LiveData;

/**
 * AnalysisRepository provides an abstraction layer between the DAO (Data Access Object)
 * and the calling code, simplifying database operations and centralizing the database logic.
 * This repository class allows for more modular and structured code.
 *
 * @author Debidutt Prasad
 */
public class AnalysisRepository {

    private final AnalysisDao analysisDao;
    private final Application application;

    /**
     * Constructor for the AnalysisRepository.
     *
     * @param application The application context, used to get the database instance.
     */
    public AnalysisRepository(Application application) {
        this.application = application;
        AnalysisDatabase database = AnalysisDatabase.getDatabase(this.application);
        this.analysisDao = database.analysisDao();
    }

    /**
     * Inserts an analysis record into the database.
     *
     * @param analysis The analysis object to insert.
     * @return The ID (serial number) of the newly inserted analysis.
     */
    public long insertAnalysis(Analysis analysis) {
        return this.analysisDao.insertAnalysis(analysis);
    }

    /**
     * Deletes an analysis record based on the given serial number.
     *
     * @param serialNum The serial number of the analysis to delete.
     * @return The number of rows deleted.
     */
    public int deleteAnalysisBySerialNumber(long serialNum) {
        return this.analysisDao.deleteAnalysisBySerialNumber(serialNum);
    }

    /**
     * Retrieves an analysis record based on the given serial number.
     *
     * @param serialNum The serial number of the analysis to retrieve.
     * @return The Analysis object corresponding to the given serial number.
     */
    public Analysis getAnalysisBySerialNumber(long serialNum) {
        return this.analysisDao.getAnalysisBySerialNumber(serialNum);
    }

    /**
     * Retrieves all analysis records from the database.
     *
     * @return A LiveData list of all Analysis objects in the database.
     */
    public LiveData<List<Analysis>> getAllAnalyses() {
        return this.analysisDao.getAll();
    }

    /**
     * Fetches the image captured path stored in shared preferences.
     *
     * @return The path of the captured image, or "Default String Value" if not found.
     */
    public String getImageCapturedPath() {
        SharedPreferences sharedPreferences = application.getSharedPreferences(Constants.MY_APP_PREFERENCES, Context.MODE_PRIVATE);
        return sharedPreferences.getString(ImageRepository.IMAGE_CAPTURED_PATH, "Default String Value");
    }
}
