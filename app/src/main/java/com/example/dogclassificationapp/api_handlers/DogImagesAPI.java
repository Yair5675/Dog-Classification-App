package com.example.dogclassificationapp.api_handlers;

/**
 * A utility class to retrieve images of specific dog breeds from the dog API.
 */
public class DogImagesAPI {

    // The endpoint to receive images of dogs by breeds and sub-breeds. The arguments that should
    // be replaced are: "{breed}", "{sub_breed}", "{num_images}":
    private static final String DOG_IMAGES_ENDPOINT = "https://dog.ceo/api/breed/{breed}/{sub_breed}/images/random/{num_images}";
}
