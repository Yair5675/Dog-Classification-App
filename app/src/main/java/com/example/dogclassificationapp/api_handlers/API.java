package com.example.dogclassificationapp.api_handlers;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Optional;

public abstract class API {

    /**
     * Since APIs must be run at a separate thread, this interface will represent the callback that
     * will be run once the API call is finished.
     * @param <T> The type of the expected value from the API.
     * @param <E> The type of the error description (if one was raised).
     */
    public interface APICallback <T, E> {
        /**
         * The function that will be executed if the API call was executed successfully and no
         * problems occurred.
         * @param value The value that the API call returned.
         */
        void onSuccess(T value);

        /**
         * The function that will be executed if an error occurred while trying to get the info from
         * the API.
         * @param error A short description of the error that occurred.
         */
        void onError(E error);
    }


    /**
     * Sends a get request to the given URL and returns the response.
     * @param url The URL to the API which will return a response.
     * @return If the response from the API was successfully received, it is returned. If not, an
     *         empty optional will be returned.
     */
    protected static Optional<HttpURLConnection> sendGetRequest(String url) {
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
    protected static Optional<String> convertResponseToString(HttpURLConnection response) {
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
