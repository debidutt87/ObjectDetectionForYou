package com.ody.di.ui;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.icu.text.SimpleDateFormat;
import android.os.Bundle;
import android.os.Environment;
import android.util.Pair;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.ody.di.R;
import com.ody.di.repository.AnalysisRepository;
import com.ody.di.ui.adapter.DetectedObjectsRecyclerAdapter;
import com.ody.di.utils.Constants;
import com.ody.di.utils.ImageUtils;
import com.ody.di.utils.NavigationUtils;
import com.ody.di.viewmodel.AnalysisViewModel;
import com.ody.di.viewmodel.factory.GenericViewModelFactory;

import org.tensorflow.lite.examples.detection.tflite.Detector;

import java.io.File;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import static com.ody.di.utils.Constants.ORIGIN_ANALYSES_ACTIVITY;
import static com.ody.di.utils.Constants.YYYYMMDDHHMMSS;

/**
 * Represents the activity that performs image analysis and displays the results.
 * Users can view detailed analysis, save their analysis, or delete their analysis.
 *
 * @author Debidutt Prasad
 */
public class AnalysisActivity extends AppCompatActivity {

    private static final String TAG = "AnalysisActivity";
    private AnalysisViewModel viewModel;
    AnalysisRepository analysisRepository;
    private long serialNum;
    private String imageFileName;
    private Bitmap mutableBmp;
    private Button buttonSaveAnalysis;
    private Button buttonDeleteAnalysis;
    private ProgressBar progressBar;
    ImageView imageView;
    LinearLayout tableHeader;
    RecyclerView recyclerView;

    /**
     * Called when the activity is starting.
     * Initializes the UI components, sets up the view models and observes changes.
     *
     * @param savedInstanceState If the activity is being re-initialized after previously being
     *                           shut down then this Bundle contains the data it most recently
     *                           supplied in onSaveInstanceState(Bundle).
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_analysis);
        initializeUIComponents();
        setupViewModel();
        observeViewModel();
    }

    /**
     * Observes data changes in the view model and updates UI accordingly.
     */
    private void observeViewModel() {
        viewModel.getImageUri().observe(this, imageView::setImageURI);

        viewModel.getInsertionSuccess().observe(this, status -> {
            if (status) {
                Toast.makeText(this, "Image Analysis has been stored, successfully!", Toast.LENGTH_SHORT).show();
                NavigationUtils.navigate(AnalysisActivity.this, AnalysesActivity.class, true);
            } else {
                Toast.makeText(this, "Error! Image Analysis has not been stored!", Toast.LENGTH_SHORT).show();
            }
        });

        viewModel.getDeleteButtonVisibility().observe(this, visibility -> {
            buttonDeleteAnalysis.setVisibility(View.VISIBLE);
            buttonSaveAnalysis.setVisibility(View.GONE);
        });

        viewModel.getDetectedObjects().observe(this, detections -> {
            progressBar.setVisibility(View.GONE);
            tableHeader.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.VISIBLE);
            imageView.setVisibility(View.VISIBLE);
            buttonSaveAnalysis.setEnabled(true);
            DetectedObjectsRecyclerAdapter adapter = new DetectedObjectsRecyclerAdapter(detections);
            recyclerView.setAdapter(adapter);

            buttonSaveAnalysis.setOnClickListener(v -> {
                buttonSaveAnalysis.setEnabled(false);
                progressBar.setVisibility(View.VISIBLE);
                File analysedPhoto = createImageFile();
                ImageUtils.saveBitmapAsPNG(mutableBmp, analysedPhoto);
                viewModel.saveAnalysis(detections, imageFileName);
            });
        });

        viewModel.getDeletionSuccess().observe(this, result -> {
            if (result) {
                Toast.makeText(this, "Record deletion successfully!", Toast.LENGTH_SHORT).show();
                NavigationUtils.navigate(AnalysisActivity.this, AnalysesActivity.class, true);
            } else {
                Toast.makeText(this, "Record deletion failed! Please try again!", Toast.LENGTH_SHORT).show();
            }
        });

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            String serialNumber = bundle.getString(Constants.SERIAL_NUMBER);
            serialNum = Long.parseLong(serialNumber);
            viewModel.fetchAnalysis(serialNum);
        } else {
            viewModel.initializeDetector();

            viewModel.getIsDetectorInitialized().observe(this, isInitialized -> {
                if (isInitialized) {
                    viewModel.checkSavedImagePath();
                    viewModel.getImageUri().observe(this, uri -> {
                        if (uri != null) {
                            viewModel.doInference(uri);
                            viewModel.getDetectedObjects().observe(this, objects -> {
                                if (objects.isEmpty()) {
                                    progressBar.setVisibility(View.GONE);
                                    recyclerView.setVisibility(View.GONE);
                                    tableHeader.setVisibility(View.GONE);
                                } else {
                                    mutableBmp = viewModel.getProcessedImage().getValue().copy(Bitmap.Config.ARGB_8888, true);
                                    final Canvas canvas = new Canvas(mutableBmp);
                                    Paint p = new Paint();
                                    p.setColor(Color.RED);
                                    p.setStyle(Paint.Style.STROKE);
                                    p.setStrokeWidth(mutableBmp.getWidth() / 95);

                                    Paint paintText = new Paint();
                                    paintText.setColor(Color.BLUE);
                                    paintText.setTextSize(mutableBmp.getWidth() / 10);
                                    paintText.setFakeBoldText(true);

                                    Paint confidenceScoreText = new Paint();
                                    confidenceScoreText.setColor(Color.GREEN);
                                    confidenceScoreText.setTextSize(mutableBmp.getWidth() / 20);
                                    confidenceScoreText.setFakeBoldText(true);

                                    List<Detector.Recognition> list = viewModel.getDetectedObjects().getValue();

                                    for (int i = 0; i < list.size(); i++) {
                                        float CONFIDENCE_THRESHOLD = 0.3f;
                                        if (list.get(i).getConfidence() > CONFIDENCE_THRESHOLD) {
                                            canvas.drawText((i + 1) + " " + list.get(i).getTitle(), list.get(i).getLocation().left, list.get(i).getLocation().top, paintText);
                                            canvas.drawRect(list.get(i).getLocation(), p);
                                            canvas.drawText(String.valueOf((int) (list.get(i).getConfidence() * 100)), list.get(i).getLocation().left, list.get(i).getLocation().bottom, confidenceScoreText);
                                        }
                                    }
                                    imageView.setImageBitmap(mutableBmp);
                                }
                            });

                        }
                    });
                } else {
                    // Detector failed to initialize
                    Toast.makeText(this, "Analysis failed! Please try again!", Toast.LENGTH_SHORT).show();
                    NavigationUtils.navigate(AnalysisActivity.this, SnapActivity.class, true);
                }
            });
        }
    }

    /**
     * Sets up the view model for this activity.
     */
    private void setupViewModel() {
        analysisRepository = new AnalysisRepository(getApplication());
        GenericViewModelFactory<AnalysisViewModel> factory = new GenericViewModelFactory<>(() -> new AnalysisViewModel(getApplication(), analysisRepository));
        viewModel = new ViewModelProvider(this, factory).get(AnalysisViewModel.class);

    }

    /**
     * Initializes the UI components of this activity.
     */
    private void initializeUIComponents() {
        imageView = findViewById(R.id.analysed_image);
        recyclerView = findViewById(R.id.detected_objects_view);
        tableHeader = findViewById(R.id.table_header);
        buttonSaveAnalysis = findViewById(R.id.button_save_analysis);
        buttonDeleteAnalysis = findViewById(R.id.button_delete_analysis);
        progressBar = findViewById(R.id.spinner);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        buttonDeleteAnalysis.setOnClickListener(v -> {
            viewModel.deleteAnalysis(serialNum);
        });
    }

    /**
     * Creates an image file to save the analysis result.
     *
     * @return The created image file.
     */
    File createImageFile() {
        imageFileName = "Analysed_" + new SimpleDateFormat(YYYYMMDDHHMMSS, Locale.getDefault()).format(new Date()) + ".jpg";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        return new File(storageDir, imageFileName);
    }

    /**
     * Called when the activity has detected the user's press of the back key.
     * Manages the back press behavior based on the activity state.
     */
    @Override
    public void onBackPressed() {
        if (buttonDeleteAnalysis.getVisibility() == View.VISIBLE) {
            super.onBackPressed();
        } else {
            super.onBackPressed();
            NavigationUtils.navigate(AnalysisActivity.this, WelcomeActivity.class, true, new Pair<>(ORIGIN_ANALYSES_ACTIVITY, ORIGIN_ANALYSES_ACTIVITY));
        }
    }
}
