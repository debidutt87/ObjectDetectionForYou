package com.ody.di.ui;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.Settings;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.ody.di.R;
import com.ody.di.utils.NavigationUtils;
import com.ody.di.viewmodel.SnapViewModel;
import com.ody.di.viewmodel.factory.GenericViewModelFactory;

import java.util.stream.Stream;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModelProvider;

/**
 * Activity for capturing, retaking, and analyzing snaps.
 *
 * @author Debidutt Prasad
 */
public class SnapActivity extends AppCompatActivity {

    private static final int REQUEST_CODE_PERMISSIONS = 1001;
    private static final String[] REQUIRED_PERMISSIONS = {
            Manifest.permission.CAMERA,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };

    private SnapViewModel viewModel;
    private Button buttonOpenCamera;
    private TextView textViewSnapPrompt;
    private ImageView imageView;
    private final ActivityResultLauncher<Uri> takePictureLauncher = registerForActivityResult(
            new ActivityResultContracts.TakePicture(),
            success -> handleImageCaptureResult(success)
    );

    /**
     * Lifecycle method called when the activity is starting. Initializes UI components and sets up ViewModel observers.
     *
     * @param savedInstanceState Contains data most recently supplied in onSaveInstanceState(Bundle).
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_snap);

        initializeViewModel();
        initializeUIComponents();
        setupObservers();
    }

    /**
     * Initializes the SnapViewModel.
     */
    private void initializeViewModel() {
        GenericViewModelFactory<SnapViewModel> factory = new GenericViewModelFactory<>(() -> new SnapViewModel(getApplication()));
        viewModel = new ViewModelProvider(this, factory).get(SnapViewModel.class);
    }

    /**
     * Initializes UI components and sets up event listeners.
     */
    private void initializeUIComponents() {
        textViewSnapPrompt = findViewById(R.id.text_view_snap_prompt);
        imageView = findViewById(R.id.snapped_image);

        buttonOpenCamera = findViewById(R.id.button_open_camera);
        buttonOpenCamera.setOnClickListener(v -> handleCameraButtonAction());

        findViewById(R.id.button_retake).setOnClickListener(v -> viewModel.onCameraAction());
        findViewById(R.id.button_analyse).setOnClickListener(v -> viewModel.onAnalyzeClicked());
    }

    /**
     * Sets up LiveData observers.
     */
    private void setupObservers() {
        viewModel.getImageUriLiveData().observe(this, this::launchCameraWithUri);
        viewModel.getIsImageCapturedLiveData().observe(this, this::toggleUIElements);
        viewModel.getNavigateToAnalysisLiveData().observe(this, this::navigateToAnalysisActivity);
        viewModel.getPermissionDeniedLiveData().observe(this, isDenied -> showPermissionDeniedDialog());
    }

    /**
     * Handles the action when the camera button is clicked.
     */
    private void handleCameraButtonAction() {
        if (!hasNecessaryPermissions()) {
            ActivityCompat.requestPermissions(this, REQUIRED_PERMISSIONS, REQUEST_CODE_PERMISSIONS);
        } else {
            viewModel.onCameraAction();
        }
    }

    /**
     * Handles the result of the image capture process.
     *
     * @param success Indicates whether the image was successfully captured.
     */
    private void handleImageCaptureResult(boolean success) {
        if (success) {
            imageView.setImageURI(viewModel.getImageUriLiveData().getValue());
            viewModel.setImageCapturedLiveData(true);
        }
    }

    /**
     * Launches the camera intent with the specified URI.
     *
     * @param uri URI to store the captured image.
     */
    private void launchCameraWithUri(Uri uri) {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
        takePictureLauncher.launch(uri);
    }

    /**
     * Toggles visibility of UI elements based on whether an image was captured.
     *
     * @param isImageCaptured Indicates if an image was captured.
     */
    private void toggleUIElements(boolean isImageCaptured) {
        buttonOpenCamera.setVisibility(isImageCaptured ? View.GONE : View.VISIBLE);
        textViewSnapPrompt.setVisibility(isImageCaptured ? View.GONE : View.VISIBLE);
        imageView.setVisibility(isImageCaptured ? View.VISIBLE : View.GONE);
        findViewById(R.id.button_retake).setVisibility(isImageCaptured ? View.VISIBLE : View.GONE);
        findViewById(R.id.button_analyse).setVisibility(isImageCaptured ? View.VISIBLE : View.GONE);
    }

    /**
     * Navigates to the AnalysisActivity and resets the navigation LiveData.
     *
     * @param shouldNavigate Indicates if the app should navigate to the AnalysisActivity.
     */
    private void navigateToAnalysisActivity(boolean shouldNavigate) {
        if (shouldNavigate) {
            NavigationUtils.navigate(SnapActivity.this, AnalysisActivity.class, true);
            viewModel.resetNavigateToAnalysisLiveData();
        }
    }

    /**
     * Checks if the necessary permissions are granted.
     *
     * @return True if permissions are granted, false otherwise.
     */
    public boolean hasNecessaryPermissions() {
        return Stream.of(REQUIRED_PERMISSIONS)
                .allMatch(permission -> ContextCompat.checkSelfPermission(this, permission) == PackageManager.PERMISSION_GRANTED);
    }

    /**
     * Handles the result of the permission request.
     *
     * @param requestCode  The request code passed during the permission request.
     * @param permissions  List of permissions.
     * @param grantResults The grant results for the corresponding permissions.
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CODE_PERMISSIONS) {
            viewModel.handlePermissionsResult(grantResults);
        }
    }

    /**
     * Shows an alert dialog when permissions are denied.
     */
    private void showPermissionDeniedDialog() {
        new AlertDialog.Builder(this)
                .setTitle(R.string.permissions_denied)
                .setMessage(R.string.camera_and_storage_permissions_are_necessary)
                .setPositiveButton(R.string.go_to_settings, (dialog, which) -> redirectToAppSettings())
                .setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss())
                .show();
    }

    /**
     * Redirects the user to the app settings.
     */
    private void redirectToAppSettings() {
        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        Uri uri = Uri.fromParts("package", getPackageName(), null);
        intent.setData(uri);
        startActivity(intent);
    }
}


