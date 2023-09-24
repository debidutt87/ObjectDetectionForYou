package com.ody.di.ui;

import android.os.Bundle;
import android.util.Log;

import com.ody.di.R;
import com.ody.di.database.AnalysisDatabase;
import com.ody.di.utils.NavigationUtils;
import com.ody.di.viewmodel.WelcomeViewModel;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

/**
 * Activity that serves as the entry point to the application, allowing the user to take snaps
 * or navigate to the analyses if they exist.
 *
 * @author Debidutt Prasad
 */
public class WelcomeActivity extends BaseActivity {
    private static final String TAG = "WelcomeActivity";
    private WelcomeViewModel viewModel;

    /**
     * Lifecycle method called when the activity is starting. Initializes UI components and sets up ViewModel observers.
     *
     * @param savedInstanceState Contains data most recently supplied in onSaveInstanceState(Bundle).
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setupUI();
        setupViewModel();
        observeAnalysisRecords();
    }

    /**
     * Initializes UI components and sets up event listeners.
     */
    private void setupUI() {
        findViewById(R.id.button_snap_picture).setOnClickListener(v -> NavigationUtils.navigate(WelcomeActivity.this, SnapActivity.class, false));
    }

    /**
     * Initializes the WelcomeViewModel.
     */
    private void setupViewModel() {
        AnalysisDatabase database = AnalysisDatabase.getDatabase(this);
        viewModel = new ViewModelProvider(this, new ViewModelProvider.Factory() {
            @NonNull
            @Override
            public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
                return (T) new WelcomeViewModel(database);
            }
        }).get(WelcomeViewModel.class);
    }

    /**
     * Sets up observer for analysis records. Navigates to the AnalysesActivity if records exist.
     */
    private void observeAnalysisRecords() {
        Bundle bundle = getIntent().getExtras();
        viewModel.hasAnalysisRecords().observe(this, hasRecords -> {
            if (hasRecords) {
                if (bundle == null) {
                    NavigationUtils.navigate(WelcomeActivity.this, AnalysesActivity.class, true);
                } else {
                    Log.d(TAG, "onCreate: Welcome to WelcomeActivity ;)");
                }
            }
        });
    }

}
