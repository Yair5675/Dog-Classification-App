package com.example.dogclassificationapp.classifier_logic;

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

    /**
     * The main function of the class, returns information from Wikipedia about the given breed.
     * @param breed The name of the dog breed that will be searched.
     * @return If the API call was successful the information is returned, but if an error occurred
     *         an empty optional is returned.
     */
    public static Optional<String> getInfo(String breed) {
        // TODO: Complete the getInfo function based on the documentation
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
     * @param formattedSearchUrl The search URL after being modified to search for the wanted breed.
     * @return If the response from the Wikipedia API was successfully received, it is returned. If not, an empty
     *         optional will be returned.
     */
    private static Optional<HttpURLConnection> getSearchResponse(String formattedSearchUrl) {
        try {
            final HttpURLConnection searchConnection = (HttpURLConnection) new URL(formattedSearchUrl).openConnection();
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
     * @return If no error occurs, the function returns the given response's content in String format. If an error
     *         occurred, an empty Optional is returned.
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
}
