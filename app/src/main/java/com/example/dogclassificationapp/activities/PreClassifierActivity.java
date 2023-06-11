package com.example.dogclassificationapp.activities;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.dogclassificationapp.R;

import java.io.IOException;

public class PreClassifierActivity extends AppCompatActivity {

    // The button to take a picture from the camera:
    private Button cameraBtn;

    // The button to take a picture from the gallery:
    private Button galleryBtn;

    // The button that leads to the database activity in the app:
    private ImageView menuDatabaseBtn;

    // The button to start classification on the given image (only if one was given):
    private Button confirmBtn;

    // The actual image (will be null at first):
    private Bitmap dogImage;

    // The dog image given by the user:
    private ImageView dogImageView;

    // Since the classifier only accepts images of size 256x256, the presented dog image must be
    // resized to those dimensions:
    private static final int IMAGE_WIDTH = 256;
    private static final int IMAGE_HEIGHT = 256;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pre_classifier);

        // Loading the views:
        this.cameraBtn = findViewById(R.id.camera_btn_pre_classifier);
        this.galleryBtn = findViewById(R.id.gallery_btn_pre_classifier);
        this.confirmBtn = findViewById(R.id.confirm_img_btn_pre_classifier);
        this.dogImageView = findViewById(R.id.chosen_img_pre_classifier);
        this.menuDatabaseBtn = findViewById(R.id.menu_dataset_btn);

        // Setting the confirmation button to not visible because no image is selected:
        this.confirmBtn.setVisibility(View.GONE);

        // Resetting the dog image to its default:
        this.resetDogImage();

        // Configuring the buttons' onClickListeners:
        this.menuDatabaseBtn.setOnClickListener(this::onMenuDatabaseButtonClick);
        this.cameraBtn.setOnClickListener(this::onCameraButtonClick);
        this.galleryBtn.setOnClickListener(this::onGalleryButtonClick);
        this.confirmBtn.setOnClickListener(this::onConfirmButtonClick);
    }

    /**
     * Resets the chosen dog image to its default image.
     */
    private void resetDogImage() {
        this.dogImage = null;
        this.dogImageView.setImageResource(R.drawable.pre_classifier_default_dog_img);

        // Setting the confirmation button visibility to gone:
        this.confirmBtn.setVisibility(View.GONE);
    }

    /**
     * Sets the picture displayed on the screen to the given bitmap.
     */
    private void setDogImage(Bitmap newImg) {
        this.dogImageView.setImageBitmap(newImg);

        // Saving the actual image in a scaled version for the model later:
        this.dogImage = Bitmap.createScaledBitmap(newImg, IMAGE_WIDTH, IMAGE_HEIGHT, false);

        // Setting the visibility of the confirmation button to visible:
        this.confirmBtn.setVisibility(View.VISIBLE);
    }

    /**
     * Handles the events that are caused by clicking the camera button.
     * @param view The camera button that was clicked.
     */
    private void onCameraButtonClick(View view) {
        // Checking for the permission to use the camera:
        if (checkSelfPermission(Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
            // Launching the camera activity:
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            cameraLauncher.launch(intent);
        }
        // If permission was not granted, ask for permission:
        else {
            final String[] neededPermissions = { Manifest.permission.CAMERA };
            requestPermissions(neededPermissions, 100);
        }
    }

    // The activityResultLauncher that will open up the camera:
    private final ActivityResultLauncher<Intent> cameraLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),

            // Handling what will happen when the camera activity has ended:
            result -> {
                // If no image was retrieved or something went wrong:
                if (result == null){
                    this.makeCameraToastError();
                }
                // If everything went smoothly:
                else if (result.getResultCode() == RESULT_OK) {
                    if (result.getData() != null) {
                        // Receiving the image from the camera:
                        final Bitmap cameraImg = (Bitmap) result.getData().getExtras().get("data");

                        // Updating the screen:
                        this.setDogImage(cameraImg);

                    } else {
                        this.makeCameraToastError();
                    }
                }

            }
    );

    /**
     * Creates and shows an error toast message about the camera.
     */
    private void makeCameraToastError() {
        final String CAMERA_ERROR_MSG = getString(R.string.pre_classifier_toast_camera_error_txt);
        Toast.makeText(this, CAMERA_ERROR_MSG, Toast.LENGTH_LONG).show();
    }

    /**
     * Handles the events that are caused by clicking the gallery button.
     * @param view The gallery button that was clicked.
     */
    private void onGalleryButtonClick(View view) {
        // Launching the gallery activity:
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        galleryLauncher.launch(intent);
    }

    // The activityResultLauncher that will open up the gallery:
    private final ActivityResultLauncher<Intent> galleryLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),

            // Handing what will happen when the gallery activity has ended:
            result -> {
                // If no image was retrieved or something went wrong:
                if (result == null){
                    this.makeGalleryToastError();
                }
                // If everything went smoothly:
                else if (result.getResultCode() == RESULT_OK) {
                    if (result.getData() != null) {
                        // Receiving the image from the gallery:
                        final Uri data = result.getData().getData();

                        try {
                            final Bitmap galleryImg = MediaStore.Images.Media.getBitmap(this.getContentResolver(), data);
                            // Updating the screen:
                            this.setDogImage(galleryImg);

                        } catch (IOException e) {
                            this.makeGalleryToastError();
                        }
                    } else {
                        this.makeGalleryToastError();
                    }
                }
            }
    );

    /**
     * Creates and shows an error toast message about the gallery.
     */
    private void makeGalleryToastError() {
        final String CAMERA_ERROR_MSG = getString(R.string.pre_classifier_toast_gallery_error_txt);
        Toast.makeText(this, CAMERA_ERROR_MSG, Toast.LENGTH_LONG).show();
    }

    /**
     * Handles the events that are caused by clicking the confirmation button.
     * @param view The confirmation button that was clicked.
     */
    private void onConfirmButtonClick(View view) {

    }

    /**
     * Handles the events that are caused by clicking the menu's database button.
     * @param view The menu's database button which was clicked.
     */
    private void onMenuDatabaseButtonClick(View view) {

    }
}