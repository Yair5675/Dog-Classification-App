package com.example.dogclassificationapp.classifier_logic;

import android.content.res.Resources;
import android.graphics.drawable.Drawable;

import androidx.core.content.res.ResourcesCompat;

import com.example.dogclassificationapp.R;

/**
 * A class that represents the information on a single breed, and includes how confident the model
 * is about the given dog image being the current breed.
 */
public class Breed {
    // The name of the breed:
    private final String name;

    // The confidence of the model that the dog image is this breed:
    private final double confidence;

    // Additional info about the breed:
    private String info;

    // Main and bonus images of a dog of the same breed:
    private Drawable mainImg;
    private Drawable bonusImg;

    // The default info message that will appear when loading information failed:
    private static final String DEFAULT_INFO = "Loading...";

    // The Wikipedia search is made of two parts: Searching for most relevant pages and extracting
    // them. Therefor there are two links, one for searching and one for extracting:

    // Replace "{breed}" with the desired god breed to search:
    private static final String WIKI_SEARCH_URL = "https://en.wikipedia.org/w/api.php?action=query&format=json&list=search&srsearch={breed}";
    // Replace "{numSentences}" and "{pageId}":
    private static final String WIKI_EXTRACT_URL = "https://en.wikipedia.org/w/api.php?action=query&format=json&prop=extracts&exsentences={numSentences}&explaintext=true&pageids={pageId}";

    public Breed(Resources res, String name, double confidence) {
        this.name = name;
        this.confidence = confidence;

        // Loading information from Wikipedia:
        final boolean wikiSuccessful = this.loadWikiInfo(name);
        // If the Wikipedia information wasn't set successfully:
        if (!wikiSuccessful)
            this.info = DEFAULT_INFO;

        // Loading two random images of the current breed:
        final boolean imgSuccessful = this.loadMainAndBonusImages(name);
        // If the images weren't set successfully:
        if (!imgSuccessful) {
            this.mainImg = ResourcesCompat.getDrawable(res, R.drawable.classifier_default_dog, null);
            this.bonusImg = ResourcesCompat.getDrawable(res, R.drawable.classifier_default_dog, null);
        }

    }

    /**
     * Sends an HTTP request to Wikipedia about the name of the given breed and sets the
     * response as the "info" attribute. If the HTTP response is some sort of error the info
     * attribute is not set.
     * @param breed The name of the breed that will be searched in Wikipedia.
     * @return True if the "info" attribute was successfully set, False otherwise.
     */
    private boolean loadWikiInfo(String breed) {
        // TODO: Complete the getWikiInfo function as per documentation
        return false;
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

    public String getBreedName() {
        return name;
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
