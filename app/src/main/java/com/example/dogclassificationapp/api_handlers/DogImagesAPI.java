package com.example.dogclassificationapp.api_handlers;

import com.example.dogclassificationapp.util.Callback;
import com.example.dogclassificationapp.util.Result;

import java.net.HttpURLConnection;
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
     * Gathers URLs of images concurrently from the dog API. Th length of the gathered list of URLs
     * depends on the numImages parameter, but if this number is too large the API may provide less.
     * @param breed The breed of the dog that will appear in the images.
     * @param subBreed The sub-breed of the dog that will appear in the images.
     * @param numImages The desired amount of images. The actual amount may be less if there aren't
     *                  enough in the API.
     * @param callback The callback functions that will run at the end of the API call, when the
     *                 info is gathered. If the URLs were gathered successfully, the "onSuccess"
     *                 function will be invoked. Otherwise, the "onError" function will be invoked.
     */
    public static void getImagesURLsAsync(String breed, String subBreed, int numImages,
                                          Callback<ArrayList<String>, String> callback) {
        // Created the thread that will gather the information:
        final Thread thread = new Thread(() -> {
            try {
                // Getting the URLs:
                final Result<ArrayList<String>, String> info = getImagesURLs(breed, subBreed, numImages);

                // If the URLs were gathered successfully:
                if (info.isOk())
                    callback.onSuccess(info.getValue());
                // If some error arose:
                else
                    callback.onError(info.getError());
            } catch (Exception e) {
                // Catch any unforeseen exception that were thrown (just in case):
                callback.onError(e.getMessage());
            }
        });

        // Starting the thread:
        thread.start();
    }

    /**
     * Returns a list of image URLs from the dog API. The length of the list is the amount of images
     * that was given as a parameter.
     * @param breed The main breed of the dog (example: "Afghan hound", "hound" is the breed).
     * @param subBreed The sub-breed of the dog (example: "Afghan hound", "Afghan is the sub-breed).
     * @param numImages The amount of images that will be returned
     * @return A list of image URLs from the dog API.
     */
    public static Result<ArrayList<String>, String> getImagesURLs(String breed, String subBreed, int numImages) {
        // Getting the URL for the appropriate endpoint for the specified breed:
        final String FORMATTED_URL = getFormattedImagesEndpoint(breed, subBreed, numImages);

        // Getting the HTTP response:
        final Optional<HttpURLConnection> responseOpt = sendGetRequest(FORMATTED_URL);
        // If the GET request failed:
        if (!responseOpt.isPresent()) {
            final String ERR = "Get request failed";
            return Result.failure(ERR);
        }

        // Unwrapping and reading the response:
        final HttpURLConnection response = responseOpt.get();
        final Optional<String> contentOpt = convertResponseToString(response);

        // If reading the content failed:
        if (!contentOpt.isPresent()) {
            final String ERR = "Reading content from response failed";
            return Result.failure(ERR);
        }

        // Unwrapping the response's content:
        final String content = contentOpt.get();

        // Getting the URls:
        final Optional<ArrayList<String>> urlsOpt = getURLsFromResponse(content);

        // If extracting the URLs failed:
        if (!urlsOpt.isPresent()) {
            final String ERR = "Extracting urls from content failed";
            return Result.failure(ERR);
        }

        // Unwrapping the URLs:
        final ArrayList<String> urls = urlsOpt.get();
        return Result.success(urls);
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
                .replace("{sub_breed}", subBreed)
                .replace("{num_images}", Integer.toString(numImages))
                .replace("//", "/")
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
