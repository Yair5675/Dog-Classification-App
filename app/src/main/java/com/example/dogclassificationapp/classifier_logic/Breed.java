package com.example.dogclassificationapp.classifier_logic;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.Log;

import androidx.core.content.res.ResourcesCompat;

import com.example.dogclassificationapp.R;
import com.example.dogclassificationapp.api_handlers.WikiAPI;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
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
    private Drawable mainImg;
    private Drawable bonusImg;

    // The default info message that will appear when loading information failed:
    private static final String DEFAULT_INFO = "Loading...";

    public Breed(Resources res, String name, double confidence) {
        // Getting the breed and sub-breed:
        final String[] breeds = getBreedAndSubBreed(name);
        this.breed = breeds[0];
        this.subBreed = breeds[1];

        // Setting confidence:
        this.confidence = confidence;

        // Loading information from Wikipedia:
        final Optional<String> wikiInfo = WikiAPI.getInfo(this.getFullName());
        // If the Wikipedia info was received, set it as the info. If not, set default info:
        this.info = wikiInfo.orElse(DEFAULT_INFO);

        // Loading two random images of the current breed:
        final boolean imgSuccessful = this.loadMainAndBonusImages(name);
        // If the images weren't set successfully:
        if (!imgSuccessful) {
            this.mainImg = ResourcesCompat.getDrawable(res, R.drawable.classifier_default_dog, null);
            this.bonusImg = ResourcesCompat.getDrawable(res, R.drawable.classifier_default_dog, null);
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
     * @param breed The name of the dog breed that will be in the images.
     * @return True if the two image attributes were set successfully, False otherwise.
     */
    private boolean loadMainAndBonusImages(String breed) {
        // TODO: Complete the loadMainAndBonusImages function per documentation
        return false;
    }

    /**
     * Returns a Drawable object from the URL of an image.
     * @param imageUrl The URL of the image that will be turned into a drawable.
     * @return If the operation was successful the drawable is returned. Otherwise, an empty
     *         optional is returned.
     */
    private static Optional<Drawable> getDrawableFromURL(String imageUrl) {
        try {
            // Loading the bitmap through the URL:
            HttpURLConnection connection = (HttpURLConnection) new URL(imageUrl).openConnection();
            connection.connect();
            InputStream input = connection.getInputStream();

            Bitmap imageBitmap = BitmapFactory.decodeStream(input);

            // Converting the bitmap to drawable:
            Drawable imgDrawable = new BitmapDrawable(Resources.getSystem(), imageBitmap);

            return Optional.of(imgDrawable);

        } catch (IOException e) {
            Log.e("Breed.java", "Failed loading drawable from URL: \"" + imageUrl + "\"");
            return Optional.empty();
        }
    }

    public String getBreed() {
        return breed;
    }

    public String getSubBreed() {
        return subBreed;
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

    public Drawable getMainImg() {
        return mainImg;
    }

    public Drawable getBonusImg() {
        return bonusImg;
    }
}
