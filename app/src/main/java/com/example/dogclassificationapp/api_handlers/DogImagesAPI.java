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

    /**
     * Returns a formatted version of the dog images URL that returns random images of the given
     * sub-breed.
     * @param breed The main breed of the dog (example: "Afghan hound", "hound" is the breed).
     * @param subBreed The sub-breed of the dog (example: "Afghan hound", "Afghan is the sub-breed).
     * @param numImages The number of images of the sub-breed that should be returned.
     * @return A URL for the endpoint of the dog API that gives the specified dog breed.
     */
    private static String getFormattedImagesEndpoint(String breed, String subBreed, int numImages) {
        return DOG_IMAGES_ENDPOINT
                .replace("{breed}", breed)
                .replace("sub_breed", subBreed)
                .replace("{num_images}", Integer.toString(numImages))
                ;
    }
}
