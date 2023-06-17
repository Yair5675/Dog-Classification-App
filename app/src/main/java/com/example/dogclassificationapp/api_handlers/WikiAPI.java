package com.example.dogclassificationapp.api_handlers;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Optional;

/**
 * A utility class for retrieving information from the Wikipedia API.
 */
public class WikiAPI {

    // The Wikipedia search is made of two parts: Searching for most relevant pages and extracting
    // them. Therefor there are two links, one for searching and one for extracting:

    // Replace "{breed}" with the desired god breed to search:
    private static final String WIKI_SEARCH_URL = "https://en.wikipedia.org/w/api.php?action=query&format=json&list=search&srsearch={breed}";
    // Replace "{numSentences}" and "{pageId}":
    private static final String WIKI_EXTRACT_URL = "https://en.wikipedia.org/w/api.php?action=query&format=json&prop=extracts&exsentences={numSentences}&explaintext=true&pageids={pageId}";


    // The maximum amount of sentences that will be returned from the getInfo function:
    private static final int MAX_SENTENCES = 7;

    /**
     * The main function of the class, returns information from Wikipedia about the given breed.
     * @param breed The name of the dog breed that will be searched.
     * @return If the API call was successful the information is returned, but if an error occurred
     *         an empty optional is returned.
     */
    public static Optional<String> getInfo(String breed) {
        // Formatting the breed name to match the URL:
        final String formattedBreed = formatBreedName(breed);

        // Getting a search response from the API:
        final Optional<HttpURLConnection> searchResponseOpt = sendGetRequest(getFormattedSearchUrl(formattedBreed));

        // Checking that the response was a success:
        if (searchResponseOpt.isPresent()) {
            // Getting the content:
            final Optional<String> searchContentOpt = convertResponseToString(searchResponseOpt.get());

            // Making sure reading the response was successful:
            if (searchContentOpt.isPresent()) {
                // Getting the ID of the first Wikipedia page:
                final Optional<Integer> pageIdOpt = getPageIDFromResponse(searchContentOpt.get());

                // Making sure the page ID was found:
                if (pageIdOpt.isPresent()) {
                    // Sending a get request to extract info from the specific page:
                    final Optional<HttpURLConnection> extractResponseOpt = sendGetRequest(getFormattedExtractURL(pageIdOpt.get()));

                    // Making sure the response was a success:
                    if (extractResponseOpt.isPresent()) {
                        // Getting the content of the response:
                        final Optional<String> extractContentOpt = convertResponseToString(extractResponseOpt.get());

                        // Making sure reading the response was successful:
                        if (extractContentOpt.isPresent()) {
                            // Extracting the info:
                            final Optional<String> info = getInfoFromExtractResponse(extractContentOpt.get());

                            // Making sure the information was successfully gathered:
                            if (info.isPresent()) {
                                return Optional.of(convertUnicode(getInfoUntilTitle(info.get())));
                            }
                        }
                    }
                }
            }
        }

        // If the code reached here, something went wrong and nothing will be returned.
        return Optional.empty();
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
     * Sends a get request to the Wikipedia URL and returns the response.
     * @param url The URL to the API which will return a response.
     * @return If the response from the Wikipedia API was successfully received, it is returned.
     *         If not, an empty optional will be returned.
     */
    private static Optional<HttpURLConnection> sendGetRequest(String url) {
        try {
            final HttpURLConnection searchConnection = (HttpURLConnection) new URL(url).openConnection();
            searchConnection.setRequestMethod("GET");
            return Optional.of(searchConnection);
        }
        catch (IOException e) {
            return Optional.empty();
        }
    }

    /**
     * Reads the HTTP response and converts it to String.
     * @param response An HTTP response that will be read.
     * @return If no error occurs, the function returns the given response's content in String
     *         format. If an error occurred, an empty Optional is returned.
     */
    private static Optional<String> convertResponseToString(HttpURLConnection response) {
        try {
            // Making variables to read the response:
            StringBuilder contentBuilder = new StringBuilder();
            BufferedReader contentReader = new BufferedReader(new InputStreamReader(response.getInputStream()));
            String line;

            // Going over the lines in the response:
            while ((line = contentReader.readLine()) != null)
                contentBuilder.append(line);
            contentReader.close();

            // Returning the content:
            return Optional.of(contentBuilder.toString());
        }
        catch (IOException e) {
            return Optional.empty();
        }
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
