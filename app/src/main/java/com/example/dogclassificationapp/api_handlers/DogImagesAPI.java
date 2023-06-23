package com.example.dogclassificationapp.api_handlers;

import java.util.ArrayList;

/**
 * A utility class to retrieve images of specific dog breeds from the dog API.
 */
public class DogImagesAPI extends API {

    // The endpoint to receive images of dogs by breeds and sub-breeds. The arguments that should
    // be replaced are: "{breed}", "{sub_breed}", "{num_images}":
    private static final String DOG_IMAGES_ENDPOINT = "https://dog.ceo/api/breed/{breed}/{sub_breed}/images/random/{num_images}";

    /**
     * Returns a list of image URLs from the dog API. The length of the list is the amount of images
     * that was given as a parameter.
     * @param numImages The amount of images that will be returned
     * @return A list of image URLs from the dog API.
     */
    public static ArrayList<String> getImagesURLs(int numImages) {
        // TODO: Complete function per documentation.
        return null;
    }
}
