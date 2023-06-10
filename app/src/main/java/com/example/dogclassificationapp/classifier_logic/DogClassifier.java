package com.example.dogclassificationapp.classifier_logic;

import android.content.Context;
import android.content.res.AssetManager;
import android.util.Log;

import com.example.dogclassificationapp.ml.DogModelLite;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * A singleton to handle the logical part of processing and classifying images.
 */
public final class DogClassifier {
    // The actual classifier model:
    private DogModelLite model;

    // The different types of dogs the classifier can distinguish:
    private final ArrayList<String> labels;

    // The singleton instance of the class:
    private static DogClassifier classifier = null;

    private DogClassifier(Context context, AssetManager assets) {
        this.labels = DogClassifier.loadLabels(assets);

        // Printing the labels to the log:
        Log.i("Labels", this.labels.toString());

        // Loading the model:
        try {
            this.model = DogModelLite.newInstance(context);

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public static DogClassifier getInstance(Context context, AssetManager assets) {
        if (classifier == null)
            classifier = new DogClassifier(context, assets);
        return classifier;
    }

    /**
     * Loads the classifier labels from the labels.csv file.
     */
    private static ArrayList<String> loadLabels(AssetManager assets) {
        final String LABELS_FILE = "labels.csv";
        ArrayList<String> labels = new ArrayList<>();

        try {
            InputStream inputStream = assets.open(LABELS_FILE);
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

            // Reading the names of the dogs:
            String line = bufferedReader.readLine();
            labels = new ArrayList<>(Arrays.asList(line.split(",")));

            bufferedReader.close();
            inputStreamReader.close();
            inputStream.close();


        } catch (IOException e) {
            e.printStackTrace();
        }

        return labels;
    }

    public String getLabel(int index) {
        return labels.get(index);
    }
}
