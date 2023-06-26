package com.example.dogclassificationapp.api_handlers;

import android.util.Log;

import com.example.dogclassificationapp.util.Result;

import java.net.HttpURLConnection;
import java.util.Optional;

/**
 * A utility class for retrieving information from the Wikipedia API.
 */
public class WikiAPI extends API {

    // The Wikipedia search is made of two parts: Searching for most relevant pages and extracting
    // them. Therefor there are two links, one for searching and one for extracting:

    // Replace "{breed}" with the desired dog breed to search:
    private static final String WIKI_SEARCH_URL = "https://en.wikipedia.org/w/api.php?action=query&format=json&list=search&srsearch={breed}";
    // Replace "{numSentences}" and "{pageId}":
    private static final String WIKI_EXTRACT_URL = "https://en.wikipedia.org/w/api.php?action=query&format=json&prop=extracts&exsentences={numSentences}&explaintext=true&pageids={pageId}";


    // The maximum amount of sentences that will be returned from the getInfo function:
    private static final int MAX_SENTENCES = 7;

    // Since the "getInfo" function must run in parallel to the main thread, this interface will
    // serve as a callback when the function has stopped running:
    public interface WikiCallback {
        /**
         * The function that will be executed if the "getInfo" function was executed successfully
         * and no problems occurred.
         * @param info The information about the dog breed that was requested.
         */
        void onSuccess(String info);

        /**
         * The function that will be executed if an error occurred while trying to get the info from
         * Wikipedia.
         * @param error A short description of the error that occurred
         */
        void onError(String error);
    }

    /**
     * The main function of the class, gathers information concurrently about the specified dog
     * breed and runs the callback that it was given once it is done.
     * @param breed The name of the dog breed that will be searched.
     * @param callback An interface containing two functions, one will be executed if the
     *                 information was received successfully ("onSuccess") and the other will be
     *                 executed if an error occurred
     */
    public static void getInfoAsync(String breed, WikiCallback callback) {
        // Creating a new thread to get the info:
        final Thread thread = new Thread(() -> {
            try {
                // Getting the info:
                final Result<String, String> result = getInfo(breed);

                // If the API call was successful, invoke the "onSuccess" method:
                if (result.isOk())
                    callback.onSuccess(result.getValue());
                    // If it failed, invoke the "onError" method:
                else
                    callback.onError(result.getError());
            } catch (Exception e) {
                // Handling any unforeseen exception (just in case):
                callback.onError(e.getMessage());
            }
        });

        // Starting the thread:
        thread.start();
    }

    /**
     * The main function of the class, returns information from Wikipedia about the given breed.
     * @param breed The name of the dog breed that will be searched.
     * @return If the API call was successful the information is returned, but if an error occurred
     *         a description of the error is returned.
     */
    private static Result<String, String> getInfo(String breed) {
        // Formatting the breed name to match the URL:
        final String formattedBreed = formatBreedName(breed);

        // Getting a search response from the API:
        final Optional<HttpURLConnection> searchResponseOpt = sendGetRequest(getFormattedSearchUrl(formattedBreed));
        // If the get request failed:
        if (!searchResponseOpt.isPresent()) {
            final String ERR = "Search get request failed";
            Log.e("Wiki error", ERR);
            return Result.failure(ERR);
        }

        // Getting the content:
        final Optional<String> searchContentOpt = convertResponseToString(searchResponseOpt.get());
        // If converting the response to string format failed:
        if (!searchContentOpt.isPresent()) {
            final String ERR = "Converting search response to string failed";
            Log.e("Wiki error", ERR);
            return Result.failure(ERR);
        }

        // Getting the ID of the first Wikipedia page:
        final Optional<Integer> pageIdOpt = getPageIDFromResponse(searchContentOpt.get());
        // If the page ID wasn't successfully extracted:
        if (!pageIdOpt.isPresent()) {
            final String ERR = "Extracting page ID failed";
            Log.e("Wiki error", ERR);
            return Result.failure(ERR);
        }

        // Sending a get request to extract info from the specific page:
        final Optional<HttpURLConnection> extractResponseOpt = sendGetRequest(getFormattedExtractURL(pageIdOpt.get()));
        // If the extract response failed:
        if (!extractResponseOpt.isPresent()) {
            final String ERR = "Extract get request failed";
            Log.e("Wiki error", ERR);
            return Result.failure(ERR);
        }

        // Getting the content of the response:
        final Optional<String> extractContentOpt = convertResponseToString(extractResponseOpt.get());
        // If converting the response to string format failed:
        if (!extractContentOpt.isPresent()) {
            final String ERR = "Converting extract response to string failed";
            Log.e("Wiki error", ERR);
            return Result.failure(ERR);
        }

        // Extracting the info:
        final Optional<String> info = getInfoFromExtractResponse(extractContentOpt.get());

        if (info.isPresent()) {
            return Result.success(
                        // Converting all unicode chars to their actual value:
                        convertUnicode(
                            // Getting the info only until the title:
                            getInfoUntilTitle(
                                    info.get()
                            )
                        )
            );
        }
        else {
            final String ERR = "Extracting info from extract response failed";
            Log.e("Wiki error", ERR);
            return Result.failure(ERR);
        }
    }

    /**
     * Changes the given breed name to better suit the search.
     * @param breed The raw breed name.
     * @return A formatted version of the raw breed name that better suits the search later on.
     */
    private static String formatBreedName(String breed) {
        return breed.replace(' ', '_') + "_(dog)";
    }

    /**
     * Modifies the search URL to search for the given breed.
     * @param formattedBreed The breed name after being modified to better suit the search link.
     * @return A modified version of the search URL to search for the given breed.
     */
    private static String getFormattedSearchUrl(String formattedBreed) {
        return WIKI_SEARCH_URL.replace("{breed}", formattedBreed);
    }

    /**
     * Extracts the page ID of the first page in the given Wikipedia response.
     * @param responseString A string version of the Wikipedia response.
     * @return If the response contains a page ID, the function will return it. If not, an empty
     *         Optional will be returned.
     */
    private static Optional<Integer> getPageIDFromResponse(String responseString) {
        // Creating the tags that will identify the page ID:
        final String startTag = "\"pageid\":";
        final String endTag = ",";

        // Searching for the page ID:
        if (responseString.contains(startTag) && responseString.contains(endTag)){
            // Extracting the page ID:
            final int startIdx = responseString.indexOf(startTag) + startTag.length();
            final int endIdx = startIdx + responseString.substring(startIdx).indexOf(endTag);

            final String pageIDStr = responseString.substring(startIdx, endIdx);

            // Making sure the page ID is a number:
            try {
                final int pageID = Integer.parseInt(pageIDStr);
                return Optional.of(pageID);
            }
            catch (NumberFormatException e) {
                return Optional.empty();
            }
        }
        // If the response doesn't contain a page ID, return an empty Optional:
        else {
            return Optional.empty();
        }
    }

    /**
     * Modifies the extract URL to get the specific page with the given page-ID.
     * @param pageID The ID of the page that will be looked for.
     * @return A modified version of the extract URL to get the specific page with the given
     *         page-ID.
     */
    private static String getFormattedExtractURL(int pageID) {
        return WIKI_EXTRACT_URL
                .replace(
                        "{numSentences}", Integer.toString(MAX_SENTENCES)
                ).replace(
                        "{pageId}", Integer.toString(pageID)
                );
    }

    /**
     * Receives the response in a string format and returns only the information about the dog breed
     * in the response.
     * @param responseString The response of the extract request in a string format.
     * @return If there is information about the dog breed, it returns it. If not, an empty optional
     *         is returned.
     */
    private static Optional<String> getInfoFromExtractResponse(String responseString) {
        // Creating tags that will identify the information part in the response:
        final String startTag = "\"extract\":\"";
        final String endTag = "\"}";

        // Making sure both tags are present in the response:
        if (responseString.contains(startTag) && responseString.contains(endTag)) {
            // Getting the start and end indices:
            final int startIdx = responseString.indexOf(startTag) + startTag.length();
            final int endIdx = startIdx + responseString.substring(startIdx).indexOf(endTag);

            // Returning the information:
            return Optional.of(responseString.substring(startIdx, endIdx));
        }
        // If they aren't present, return an empty optional:
        else {
            return Optional.empty();
        }
    }

    /**
     * Given the response's string, this function returns the part in the Wikipedia page before the
     * first title. Only the summary on the dog breed is returned.
     * @param responseInfo The response from Wikipedia in string format.
     * @return The information on the dog only until the first title.
     */
    private static String getInfoUntilTitle(String responseInfo) {
        final int endIdx = responseInfo.indexOf("\\n");
        if (endIdx >= 0)
            return responseInfo.substring(0, endIdx);
        else
            return responseInfo;
    }

    /**
     * Finds every unicode code (i.e: "\\u00f10"), and converts it to the actual unicode value.
     * @param input A string that may or may not contain codes for unicode characters.
     * @return A modified version of the string where every unicode code is converted to a unicode character.
     */
    private static String convertUnicode(String input) {
        // Creating a variable to store the parts we moved over:
        StringBuilder builder = new StringBuilder();

        // Looping over the input:
        int length = input.length();
        for (int i = 0; i < length; i++) {
            char c = input.charAt(i);
            // Checking if the current value is a unicode value:
            if (c == '\\' && i + 1 < length && input.charAt(i + 1) == 'u') {
                if (i + 5 < length) {
                    String unicodeHex = input.substring(i + 2, i + 6);
                    // Trying to convert the unicode code to a unicode char:
                    try {
                        int unicodeValue = Integer.parseInt(unicodeHex, 16);
                        builder.append((char) unicodeValue);
                        i += 5; // Skip the Unicode escape sequence
                    } catch (NumberFormatException e) {
                        // Invalid Unicode escape sequence, skip it:
                        builder.append(c);
                    }
                }
                // Invalid Unicode escape sequence, append as is:
                else {
                    builder.append(c);
                }
            }
            // Normal char, add to the builder:
            else {
                builder.append(c);
            }
        }
        return builder.toString();
    }
}
