package com.example.dogclassificationapp.classifier_logic;

import android.content.res.Resources;
import android.graphics.drawable.Drawable;

import androidx.core.content.res.ResourcesCompat;

import com.example.dogclassificationapp.R;
import com.example.dogclassificationapp.api_handlers.WikiAPI;

import java.util.Optional;

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
    private final String info;

    // Main and bonus images of a dog of the same breed:
    private Drawable mainImg;
    private Drawable bonusImg;

    // The default info message that will appear when loading information failed:
    private static final String DEFAULT_INFO = "Loading...";

    public Breed(Resources res, String name, double confidence) {
        this.name = name;
        this.confidence = confidence;

        // Loading information from Wikipedia:
        final Optional<String> wikiInfo = WikiAPI.getInfo(this.name);
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
