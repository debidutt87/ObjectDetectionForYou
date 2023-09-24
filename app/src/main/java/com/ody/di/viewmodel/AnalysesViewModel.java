package com.ody.di.viewmodel;

import android.app.Application;
import android.content.Context;
import android.net.Uri;
import android.util.Log;

import com.ody.di.database.entities.Analysis;
import com.ody.di.repository.AnalysisRepository;
import com.ody.di.ui.model.AnalysesModel;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.core.content.FileProvider;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

/**
 * ViewModel for handling operations related to analyses.
 * Provides data required for the UI and communicates with the AnalysisRepository.
 *
 * @author Debidutt Prasad
 */
public class AnalysesViewModel extends AndroidViewModel {
   private final AnalysisRepository analysisRepository;
   private final LiveData<List<Analysis>> analyses;

   private final static String TAG = "AnalysesViewModel";

   /**
    * Constructor to instantiate an AnalysesViewModel.
    *
    * @param application Application reference for context.
    */
   public AnalysesViewModel(@NonNull Application application) {
      super(application);
      analysisRepository = new AnalysisRepository(application);
      analyses = analysisRepository.getAllAnalyses();
   }

   /**
    * Converts a list of {@link Analysis} to a list of {@link AnalysesModel}.
    *
    * @param analyses    List of {@link Analysis} from the database.
    * @param storageDir  Directory where the images are stored.
    * @return A list of {@link AnalysesModel} containing URI references to the images.
    */
   public List<AnalysesModel> mapper(List<Analysis> analyses, File storageDir) {
      List<AnalysesModel> modelList = new ArrayList<>();

      Context appContext = getApplication().getApplicationContext();
      String fileProviderString = appContext.getPackageName() + ".fileprovider";

      for (Analysis analysis : analyses) {
         File file = new File(storageDir, analysis.imageReference);
         if (file.exists()) {
            Uri contentUri = FileProvider.getUriForFile(appContext, fileProviderString, file);
            AnalysesModel analysedModel = new AnalysesModel(String.valueOf(analysis.serialNumber), contentUri);
            modelList.add(analysedModel);
         } else {
            Log.d(TAG, "mapper: analysed image doesn't exist");
            // You could also notify your view about this issue
         }
      }
      return modelList;
   }

   /**
    * Get LiveData reference to the list of {@link Analysis}.
    *
    * @return LiveData reference of list of {@link Analysis}.
    */
   public LiveData<List<Analysis>> getAnalyses() {
      return analyses;
   }
}
