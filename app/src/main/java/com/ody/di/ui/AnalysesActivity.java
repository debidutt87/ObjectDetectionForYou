package com.ody.di.ui;

import android.os.Bundle;
import android.os.Environment;
import android.util.Pair;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.ody.di.R;
import com.ody.di.database.entities.Analysis;
import com.ody.di.ui.adapter.AnalysesGridAdapter;
import com.ody.di.utils.Constants;
import com.ody.di.utils.NavigationUtils;
import com.ody.di.viewmodel.AnalysesViewModel;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.io.File;
import java.util.List;

import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import static com.ody.di.utils.Constants.ORIGIN_ANALYSES_ACTIVITY;

/**
 * Represents the activity that displays the analyses history.
 * Users can view past analyses and navigate to individual analysis details.
 * It also provides a floating action button to navigate back to the welcome activity.
 *
 * @author Debidutt Prasad
 */
public class AnalysesActivity extends BaseActivity {

    private AnalysesViewModel analysesViewModel;
    private long backPressedTime;
    private Toast backToast;

    /**
     * Called when the activity is starting.
     * Initializes the UI components and sets up ViewModel observers.
     *
     * @param savedInstanceState If the activity is being re-initialized after previously being
     *                           shut down then this Bundle contains the data it most recently
     *                           supplied in onSaveInstanceState(Bundle).
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_analyses);

        initializeUIComponents();
    }

    /**
     * Initializes UI components and sets up event listeners and view model observers.
     */
    private void initializeUIComponents() {
        TextView textViewAnalysesStatus = findViewById(R.id.text_view_history);
        RecyclerView gridViewAnalyses = setupGridView();
        FloatingActionButton floatingActionButton = findViewById(R.id.floating_action_camera);
        ProgressBar progressBar = findViewById(R.id.spinner);

        floatingActionButton.setOnClickListener(v -> navigateToWelcomeActivity());

        analysesViewModel = new ViewModelProvider(this).get(AnalysesViewModel.class);

        analysesViewModel.getAnalyses().observe(this, analyses -> updateUIBasedOnAnalyses(analyses, textViewAnalysesStatus, gridViewAnalyses, progressBar));
    }

    /**
     * Sets up the GridView for displaying analyses.
     *
     * @return Initialized RecyclerView (GridView).
     */
    private RecyclerView setupGridView() {
        RecyclerView gridViewAnalyses = findViewById(R.id.grid_view_history);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 2);
        gridViewAnalyses.setLayoutManager(gridLayoutManager);
        return gridViewAnalyses;
    }

    /**
     * Navigates to the WelcomeActivity.
     */
    private void navigateToWelcomeActivity() {
        NavigationUtils.navigate(
                this,
                WelcomeActivity.class,
                true,
                new Pair<>(ORIGIN_ANALYSES_ACTIVITY, ORIGIN_ANALYSES_ACTIVITY)
        );
    }

    /**
     * Updates the UI components based on the provided analyses.
     *
     * @param analyses               List of analysis to display.
     * @param textViewAnalysesStatus TextView to display the analysis status.
     * @param gridViewAnalyses       GridView to display the analyses.
     * @param progressBar            ProgressBar indicating the loading state.
     */
    private void updateUIBasedOnAnalyses(List<Analysis> analyses, TextView textViewAnalysesStatus, RecyclerView gridViewAnalyses, ProgressBar progressBar) {
        if (analyses.isEmpty()) {
            displayNoAnalysisHistory(textViewAnalysesStatus, gridViewAnalyses, progressBar);
        } else {
            displayAnalysisHistory(analyses, textViewAnalysesStatus, gridViewAnalyses, progressBar);
        }
    }

    private void displayNoAnalysisHistory(TextView textViewAnalysesStatus, RecyclerView gridViewAnalyses, ProgressBar progressBar) {
        textViewAnalysesStatus.setText(R.string.no_analysis_history);
        gridViewAnalyses.setVisibility(View.GONE);
        progressBar.setVisibility(View.GONE);
    }

    private void displayAnalysisHistory(List<Analysis> analyses, TextView textViewAnalysesStatus, RecyclerView gridViewAnalyses, ProgressBar progressBar) {
        textViewAnalysesStatus.setText(R.string.analysis_history);
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        AnalysesGridAdapter analysesGridAdapter = new AnalysesGridAdapter(this, analysesViewModel.mapper(analyses, storageDir));
        analysesGridAdapter.setOnItemClickListener(position -> navigateToAnalysisActivity(analyses.get(position).serialNumber));
        runOnUiThread(() -> {
            gridViewAnalyses.setAdapter(analysesGridAdapter);
            progressBar.setVisibility(View.GONE);
        });
    }

    /**
     * Navigates to the AnalysisActivity with the provided serial number.
     *
     * @param serialNumber The serial number of the analysis.
     */
    private void navigateToAnalysisActivity(long serialNumber) {
        NavigationUtils.navigate(
                this,
                AnalysisActivity.class,
                false,
                new Pair<>(ORIGIN_ANALYSES_ACTIVITY, ORIGIN_ANALYSES_ACTIVITY),
                new Pair<>(Constants.SERIAL_NUMBER, String.valueOf(serialNumber))
        );
    }
}
