package com.example.dogclassificationapp.classifier_logic;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import com.example.dogclassificationapp.R;
import com.example.dogclassificationapp.api_handlers.DogImagesAPI;
import com.example.dogclassificationapp.api_handlers.WikiAPI;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Optional;

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
    private final String info;

    // Main and bonus images of a dog of the same breed:
    private Bitmap mainImg;
    private Bitmap bonusImg;

    // Whether or not the current breed is expanded inside a recyclerView:
    private boolean expanding;

    // The default info message that will appear when loading information failed:
    private static final String DEFAULT_INFO = "Loading...";

    // The default image resource that will appear if loading the dog's image failed:
    private static final int DEFAULT_IMG_ID = R.drawable.classifier_default_dog;

    public Breed(Resources res, String name, double confidence) {
        // Getting the breed and sub-breed:
        final String[] breeds = getBreedAndSubBreed(name);
        this.breed = breeds[0];
        this.subBreed = breeds[1];

        // Setting confidence:
        this.confidence = confidence;

        // Upon creation the breed object did not expand:
        this.expanding = false;

        // Loading information from Wikipedia:
        final Optional<String> wikiInfo = WikiAPI.getInfo(this.getFullName());
        // If the Wikipedia info was received, set it as the info. If not, set default info:
        this.info = wikiInfo.orElse(DEFAULT_INFO);

        // Loading two random images of the current breed:
        final boolean imgSuccessful = this.loadMainAndBonusImages();
        // If the images weren't set successfully:
        if (!imgSuccessful) {
            this.mainImg = BitmapFactory.decodeResource(res, DEFAULT_IMG_ID);
            this.bonusImg = BitmapFactory.decodeResource(res, DEFAULT_IMG_ID);
        }

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

        // If there exists a sub-breed:
        if (words.length > 1)
            breeds[1] = words[0];

        return breeds;
    }

    /**
     * Sends an HTTP request to the dog API and receives two random images of the specified breed.
     * The function then sets the first image as the "mainImg" attribute and the second as the
     * "bonusImg" attribute. If the HTTP response is some sort of error the two image attributes
     * will not be set.
     * @return True if the two image attributes were set successfully, False otherwise.
     */
    private boolean loadMainAndBonusImages() {
        // Loading the images' urls from the API:
        final Optional<ArrayList<String>> urlsOpt = DogImagesAPI.getImagesURLs(this.breed, this.subBreed, 2);
        // If the API failed:
        if (!urlsOpt.isPresent())
            return false;

        // Unwrapping the links:
        final ArrayList<String> urls = urlsOpt.get();

        // Making sure the length of the URLs is 2:
        if (urls.size() != 2)
            return false;

        // Loading the main image:
        final Optional<Bitmap> mainImgOpt = getBitmapFromURL(urls.get(0));
        final Optional<Bitmap> bonusImgOpt = getBitmapFromURL(urls.get(1));

        // Making sure the conversion to drawable was successful:
        if (!mainImgOpt.isPresent() || !bonusImgOpt.isPresent())
            return false;

        // Loading the images:
        this.mainImg = mainImgOpt.get();
        this.bonusImg = bonusImgOpt.get();

        return true;
    }

    /**
     * Returns a Drawable object from the URL of an image.
     * @param imageUrl The URL of the image that will be turned into a drawable.
     * @return If the operation was successful the drawable is returned. Otherwise, an empty
     *         optional is returned.
     */
    private static Optional<Bitmap> getBitmapFromURL(String imageUrl) {
        try {
            // Loading the bitmap through the URL:
            HttpURLConnection connection = (HttpURLConnection) new URL(imageUrl).openConnection();
            connection.connect();
            InputStream input = connection.getInputStream();

            Bitmap imageBitmap = BitmapFactory.decodeStream(input);

            return Optional.of(imageBitmap);

        } catch (IOException e) {
            Log.e("Breed.java", "Failed loading bitmap from URL: \"" + imageUrl + "\"");
            return Optional.empty();
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

    public Bitmap getMainImg() {
        return mainImg;
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
}
