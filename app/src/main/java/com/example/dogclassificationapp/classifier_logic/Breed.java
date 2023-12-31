package com.example.dogclassificationapp.classifier_logic;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import com.example.dogclassificationapp.R;
import com.example.dogclassificationapp.api_handlers.DogImagesAPI;
import com.example.dogclassificationapp.api_handlers.WikiAPI;
import com.example.dogclassificationapp.util.Callback;
import com.example.dogclassificationapp.util.Result;
import com.example.dogclassificationapp.util.TaskExecuter;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.function.Function;

/**
 * A class that represents the information on a single breed, and includes how confident the model
 * is about the given dog image being the current breed.
 */
public class Breed {
    // The name of the breed:
    private final String breed;

    // The name of the sub-breed:
    private final String subBreed;

    // The confidence of the model that the dog image is this breed:
    private final double confidence;

    // Additional info about the breed:
    private String info;

    // Main and bonus images of a dog of the same breed:
    private Bitmap mainImg;
    private Bitmap bonusImg;

    // Whether or not the current breed is expanded inside a recyclerView:
    private boolean expanding;

    // The default info message that will appear when loading information failed:
    private static final String DEFAULT_INFO = "Loading...";

    // The default image resource that will appear if loading the dog's image failed:
    private static final int DEFAULT_IMG_ID = R.drawable.classifier_default_dog;

    // An OnDataLoadedListener to handle what will happen once the Wiki info or the images are
    // loaded from the appropriate APIs:
    private OnDataLoadedListener onDataLoadedListener;

    /**
     * Since the breed's images and information are loaded on a separate thread, this interface
     * provides a way to handle the event when they are loaded.
     */
    public interface OnDataLoadedListener {
        /**
         * The function that will be called once the information of the breed or the breed's images
         * will be loaded.
         * @param breed The breed whose data was loaded.
         */
        void onDataLoaded(Breed breed);
    }


    /**
     * The constructor of the Breed class.
     * @param res A resources object to get images from the res.drawable directory.
     * @param normalBreed The normal name of the dog breed, should be fetched from the "labels.csv"
     *                    file.
     * @param apiBreed The name of the dgo breed that suits the dog images API, should be fetched
     *                 from the "api_labels.csv" file.
     * @param confidence The confidence of the TF-Lite model that the current breed is the breed of
     *                   the dog in the image that was given to the model.
     */
    public Breed(Resources res, String normalBreed, String apiBreed, double confidence) {
        // Getting the breed and sub-breed:
        String[] normalBreeds = getBreedAndSubBreed(normalBreed);

        // Changing the first letter of each word to uppercase:
        Function<String, String> capitalizeFirstLetter = input -> {
            if (input == null || input.isEmpty()) {
                return input;
            }
            return input.substring(0, 1).toUpperCase() + input.substring(1);
        };

        normalBreeds = Arrays.stream(normalBreeds).map(capitalizeFirstLetter).toArray(String[]::new);

        // Loading the breed and sub-breed
        this.breed = normalBreeds[0];
        this.subBreed = normalBreeds[1];

        // Setting confidence:
        this.confidence = confidence;

        // Upon creation the breed object did not expand:
        this.expanding = false;

        // Setting the information initially as the default info:
        this.info = DEFAULT_INFO;

        // Loading information from Wikipedia concurrently:
        this.loadWikiInfo();

        // Setting the main and bonus images to their default value initially:
        this.setMainImg(BitmapFactory.decodeResource(res, DEFAULT_IMG_ID));
        this.setBonusImg(BitmapFactory.decodeResource(res, DEFAULT_IMG_ID));

        // Loading two random images of the current breed:
        this.loadMainAndBonusImages(apiBreed);
    }

    /**
     * Given the full dog name, the function separates and extracts the main breed and sub-breed.
     * @param fullName The full name of the dog breed (example: "Japanese Spaniel").
     * @return An array whose first index is the main breed and second index is sub-breed. If the
     *         full name contains only one word, sub-breed will be an empty string.
     */
    private static String[] getBreedAndSubBreed(String fullName) {
        // Splitting the full name to words:
        String[] words = fullName.split(" ");

        // First word is sub-breed, last word is main breed:
        String[] breeds = {words[words.length - 1], ""};

        // Adding the remaining words to the sub-breed:
        for (int i = 0; i < words.length - 1; i++) {
            if (breeds[1].isEmpty())
                breeds[1] = words[i];
            else
                breeds[1] += " " + words[i];
        }

        return breeds;
    }

    /**
     * Using the WikiAPI class and the TaskExecuter, the function continuously tries to load info
     * from Wikipedia about the current breed. If the info can't be loaded after various attempts,
     * the info will not be changed.
     */
    private void loadWikiInfo() {
        // Saving hyper-parameters for the task executer:
        final long WAIT_TIME = 200;
        final int MAX_TRIES = 10;

        // Creating the task executer to load the info:
        final TaskExecuter<String, String> taskExecuter = new TaskExecuter<>(WAIT_TIME, MAX_TRIES,
                () -> WikiAPI.getInfo(getFullName()),
                new Callback<String, String>() {
                    @Override
                    public void onSuccess(String value) {
                        setInfo(value);

                        // Activating the OnDataLoadedListener:
                        if (onDataLoadedListener != null)
                            onDataLoadedListener.onDataLoaded(Breed.this);
                    }

                    @Override
                    public void onError(String error) {
                        Log.e("Wiki error", error);

                        // Activating the OnDataLoadedListener:
                        if (onDataLoadedListener != null)
                            onDataLoadedListener.onDataLoaded(Breed.this);
                    }
                });

        // Starting loading the task:
        taskExecuter.start();
    }

    /**
     * Uses the DogImagesAPI and the TaskExecuter to receive two random images of the current breed.
     * The function then sets the first image as the "mainImg" attribute and the second as the
     * "bonusImg" attribute.
     * @param apiBreed The full name of the breed (includes both breed and sub-breed).
     */
    private void loadMainAndBonusImages(String apiBreed) {
        // Breaking down the breed into breed and sub-breed:
        final String[] apiBreeds = getBreedAndSubBreed(apiBreed);

        // Saving hyper-parameters for the task executer:
        final long WAIT_TIME = 200;
        final int MAX_TRIES = 10;

        // Creating the task executer that will load the images:
        final TaskExecuter<ArrayList<String>, String> taskExecuter = new TaskExecuter<>(WAIT_TIME, MAX_TRIES,
                () -> DogImagesAPI.getImagesURLs(apiBreeds[0], apiBreeds[1], 2),
                new Callback<ArrayList<String>, String>() {
                    @Override
                    public void onSuccess(ArrayList<String> urls) {
                        // Making sure the length of the URLs is 2:
                        if (urls.size() == 2) {
                            // Loading the main image:
                            final Result<Bitmap, String> mainImgOpt = getBitmapFromURL(urls.get(0));
                            final Result<Bitmap, String> bonusImgOpt = getBitmapFromURL(urls.get(1));

                            // Setting the images that were retrieved successfully:
                            if (mainImgOpt.isErr())
                                onError(mainImgOpt.getError());
                            if (bonusImgOpt.isErr())
                                onError(bonusImgOpt.getError());
                            if (mainImgOpt.isOk())
                                setMainImg(mainImgOpt.getValue());
                            if (bonusImgOpt.isOk())
                                setBonusImg(bonusImgOpt.getValue());

                            // Activating the OnDataLoadedListener:
                            if (onDataLoadedListener != null)
                                onDataLoadedListener.onDataLoaded(Breed.this);
                        }
                    }

                    @Override
                    public void onError(String error) {
                        // Logging the error:
                        Log.e("Dog Images API error", error);

                        // Activating the OnDataLoadedListener:
                        if (onDataLoadedListener != null)
                            onDataLoadedListener.onDataLoaded(Breed.this);
                    }
                }
        );

        // Starting the task executer:
        taskExecuter.start();
    }

    /**
     * Returns a Bitmap object from the URL of an image.
     * @param imageUrl The URL of the image that will be turned into a bitmap.
     * @return If the operation was successful the bitmap is returned. Otherwise, a Result object
     *         containing details of the error is returned.
     */
    private static Result<Bitmap, String> getBitmapFromURL(String imageUrl) {
        try {
            // Loading the bitmap through the URL:
            HttpURLConnection connection = (HttpURLConnection) new URL(imageUrl).openConnection();
            connection.connect();
            InputStream input = connection.getInputStream();

            Bitmap imageBitmap = BitmapFactory.decodeStream(input);

            return Result.success(imageBitmap);

        } catch (IOException e) {
            return Result.failure(e.getMessage());
        }
    }

    public String getFullName() {
        return String.format("%s %s", this.subBreed, this.breed);
    }

    public double getConfidence() {
        return confidence;
    }

    public String getInfo() {
        return info;
    }

    private void setInfo(String info) {
        this.info = info;
    }

    public Bitmap getMainImg() {
        return mainImg;
    }

    private void setMainImg(Bitmap mainImg) {
        this.mainImg = mainImg;
    }

    private void setBonusImg(Bitmap bonusImg) {
        this.bonusImg = bonusImg;
    }

    public Bitmap getBonusImg() {
        return bonusImg;
    }

    public boolean isExpanding() {
        return expanding;
    }

    public void setExpanding(boolean expanding) {
        this.expanding = expanding;
    }

    public void setOnDataLoadedListener(OnDataLoadedListener onDataLoadedListener) {
        this.onDataLoadedListener = onDataLoadedListener;
    }
}
