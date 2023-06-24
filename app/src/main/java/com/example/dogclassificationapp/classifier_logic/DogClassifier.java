package com.example.dogclassificationapp.classifier_logic;

import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * A class to handle the logical part of processing and classifying images.
 */
public final class DogClassifier {
    // The context of the activity that started the model:
    private final Context context;

    // The different types of dogs the classifier can distinguish:
    private final ArrayList<String> labels;

    // The size of the image that will be passed into the model:
    private static final int IMAGE_SIZE = 256;

    public DogClassifier(Context context, AssetManager assets) {
        this.context = context;
        this.labels = DogClassifier.loadLabels(assets);

        // Printing the labels to the log:
        Log.i("Labels", this.labels.toString());
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
            Log.e("Dog Classifier", "Failed to load labels");
            e.printStackTrace();
        }

        return labels;
    }

    /**
     * Given an image of a dog, the model will return a list of breeds and sub-breeds of dogs, that
     * contain the probability that the dog inside the given image is of this breed.
     * @param dogImage The image with a dog that the model will process. Its dimensions must be
     *                 256x256 pixels.
     * @return A list of Breed objects that each contain the probability that the breed of the dog
     *         in the image is the current breed.
     */
    public ArrayList<Breed> getModelPredictions(Bitmap dogImage) {
        // TODO: Complete the function as per documentation
        return null;
    }

    /**
     * Given an image in Drawable format, the function extracts the RGB values of the image and
     * stores them in a three-dimensional array.
     * @param img The image whose pixels' RGB values will be returned.
     * @return A three dimensional array of the RGB values of the image. The first two dimensions of
     *         the array will be similar to the dimensions of the image, and the last dimension of
     *         the array will be 3 (RGB).
     */
    private static int[][][] getRGBValues(Bitmap img) {
        // Getting the dimensions of the image:
        final int WIDTH = img.getWidth();
        final int HEIGHT = img.getHeight();

        // Creating the array that will hold the values:
        final int[][][] rgbValues = new int[WIDTH][HEIGHT][3];

        // Scanning the pixels and extracting their RGB values:
        for (int y = 0; y < HEIGHT; y++) {
            for (int x = 0; x < WIDTH; x++) {
                final int pixel = img.getPixel(x, y);

                rgbValues[y][x][0] = (pixel >> 16) & 0xFF; // Red component
                rgbValues[y][x][1] = (pixel >> 8) & 0xFF; // Green component
                rgbValues[y][x][2] = pixel & 0xFF; // Blue component
            }
        }

        return rgbValues;
    }

    public String getLabel(int index) {
        return labels.get(index);
    }
}
