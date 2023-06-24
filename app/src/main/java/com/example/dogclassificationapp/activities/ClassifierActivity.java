package com.example.dogclassificationapp.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.dogclassificationapp.R;
import com.example.dogclassificationapp.classifier_logic.Breed;
import com.example.dogclassificationapp.classifier_logic.DogClassifier;
import com.example.dogclassificationapp.custom_views.BreedAdapter;

import java.util.ArrayList;

public class ClassifierActivity extends AppCompatActivity {

    // A list of all breeds that will be displayed in the recycler view
    private ArrayList<Breed> breeds;

    // The TF-Lite model that will classify the given image:
    private DogClassifier classifier;

    // The recycler view that will present all the breeds:
    private RecyclerView breedsRV;

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

        // Creating the classifier:
        classifier = new DogClassifier(this, getAssets());

        // Loading the recycler-view:
        breedsRV = findViewById(R.id.breeds_list_classifier);

        // Initializing the data:
        this.initData(chosenImg);

        // Initializing the recycler view:
        this.setBreedsRecyclerView();
    }

    /**
     * Uses the classifier to classify the given image and load the "breeds" attribute. The function
     * also sets the title of the classifier activity as the breed with the highest confidence.
     * @param chosenImg The image of a dog that the user has chosen, will be classified using the
     *                  "classifier" attribute
     */
    private void initData(Bitmap chosenImg) {
        // Create the list of breeds using the classifier:
        this.breeds = this.classifier.getModelPredictions(chosenImg, getResources()).orElse(new ArrayList<>());

        // Sorting the breeds from highest confidence to lowest:
        this.breeds.sort((b1, b2) -> Double.compare(b2.getConfidence(), b1.getConfidence()));

        // Setting the title as the name of the breed with the highest confidence:
        if (this.breeds.size() > 0) {
            final TextView title = findViewById(R.id.result_title_classifier);
            final String titleTxt = "Result: " + this.breeds.get(0).getFullName();
            title.setText(titleTxt);
        }

    }

    /**
     * Initializes the recycler view with the custom BreedAdapter and the "breeds" attribute.
     */
    private void setBreedsRecyclerView() {
        final BreedAdapter adapter = new BreedAdapter(this.breeds);
        this.breedsRV.setAdapter(adapter);
        this.breedsRV.setHasFixedSize(true);
    }
}