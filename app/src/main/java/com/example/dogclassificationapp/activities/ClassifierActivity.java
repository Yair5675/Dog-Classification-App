package com.example.dogclassificationapp.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.widget.ImageView;

import com.example.dogclassificationapp.R;

public class ClassifierActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_classifier);

        // Loading the intent from the previous activity:
        final Intent intent = getIntent();
        final Bitmap chosenImg = intent.getParcelableExtra("dog_image");

        // Setting the main image as the bitmap:
        final ImageView mainImg = findViewById(R.id.main_img_classifier);
        mainImg.setImageBitmap(chosenImg);
    }

    /**
     * Uses the TF-Lite model to classify the given image and load the breeds attribute.
     * @param chosenImg The image of a dog that the user has chosen, will be classified using the
     *                  "model" attribute
     */
    private void initData(Bitmap chosenImg) {
        // TODO: Complete the function as per documentation
    }
}