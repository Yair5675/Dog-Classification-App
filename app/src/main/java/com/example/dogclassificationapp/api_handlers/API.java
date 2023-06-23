package com.example.dogclassificationapp.api_handlers;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Optional;

public abstract class API {

    /**
     * Sends a get request to the given URL and returns the response.
     * @param url The URL to the API which will return a response.
     * @return If the response from the API was successfully received, it is returned. If not, an
     *         empty optional will be returned.
     */
    public static Optional<HttpURLConnection> sendGetRequest(String url) {
        try {
            final HttpURLConnection searchConnection = (HttpURLConnection) new URL(url).openConnection();
            searchConnection.setRequestMethod("GET");
            return Optional.of(searchConnection);
        }
        catch (IOException e) {
            return Optional.empty();
        }
    }

}
