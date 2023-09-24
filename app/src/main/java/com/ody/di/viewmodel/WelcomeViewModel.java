package com.ody.di.viewmodel;

import com.ody.di.database.AnalysisDao;
import com.ody.di.database.AnalysisDatabase;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.Transformations;
import androidx.lifecycle.ViewModel;

/**
 * ViewModel class for the Welcome module.
 * Responsible for interacting with the Analysis Database and checking if analysis records exist.
 *
 * @author Debidutt Prasad
 */
public class WelcomeViewModel extends ViewModel {

    private final AnalysisDao analysisDao;

    /**
     * Constructor to initialize the WelcomeViewModel with the provided AnalysisDatabase.
     *
     * @param database The analysis database.
     */
    public WelcomeViewModel(AnalysisDatabase database) {
        this.analysisDao = database.analysisDao();
    }

    /**
     * Checks if there are analysis records in the database.
     *
     * @return LiveData indicating if analysis records exist.
     */
    public LiveData<Boolean> hasAnalysisRecords() {
        return Transformations.map(analysisDao.getAll(), list -> !list.isEmpty());
    }
}

