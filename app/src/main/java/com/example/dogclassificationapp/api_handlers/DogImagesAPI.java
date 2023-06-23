package com.example.dogclassificationapp.api_handlers;

import java.util.ArrayList;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
     * @param breed The main breed of the dog (example: "Afghan hound", "hound" is the breed).
     * @param subBreed The sub-breed of the dog (example: "Afghan hound", "Afghan is the sub-breed).
     * @param numImages The amount of images that will be returned
     * @return A list of image URLs from the dog API.
     */
    public static ArrayList<String> getImagesURLs(String breed, String subBreed, int numImages) {
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

    /**
     * Given a response in string format, the function returns a list of all the image URLs in the
     * response.
     * @param response The response from the dog API containing various URLs of dog pictures.
     * @return If the function extracts the URLs successfully, it returns those URLs. If something
     *         went wrong, an empty optional is returned.
     */
    private static Optional<ArrayList<String>> getURLsFromResponse(String response) {
        // Establishing starting and ending tags to extract the URLs later:
        final String startTag = "\"message\":[";
        final String endTag = "],\"status\"";

        // Finding the indices:
        final int startIdx = response.indexOf(startTag) + startTag.length();
        final int endIdx = response.indexOf(endTag);

        // Validating the indices:
        if (startIdx < startTag.length() || endIdx < startIdx)
            return Optional.empty();

        // Extracting the URLs using regex:
        final String URLS = response.substring(startIdx, endIdx);

        final Pattern pattern = Pattern.compile("\"(.*?)\"");
        final Matcher matcher = pattern.matcher(URLS);

        final ArrayList<String> urlsList = new ArrayList<>();

        // Looping over the string until there are no more links:
        String url;
        while (matcher.find())
            if ((url = matcher.group(1)) != null)
                // Removing The unnecessary "\" from the links:
                urlsList.add(url.replace("\\", ""));

        return Optional.of(urlsList);
    }
}
