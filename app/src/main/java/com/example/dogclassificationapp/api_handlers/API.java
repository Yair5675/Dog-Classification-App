package com.example.dogclassificationapp.api_handlers;

import com.example.dogclassificationapp.util.Result;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Optional;

public abstract class API {

    /**
     * Sends a get request to the given URL and returns the response.
     * @param url The URL to the API which will return a response.
     * @return If the response from the API was successfully received, it is returned. If not, the
     *         error that occurred will be returned.
     */
    protected static Result<HttpURLConnection, IOException> sendGetRequest(String url) {
        try {
            final HttpURLConnection searchConnection = (HttpURLConnection) new URL(url).openConnection();
            searchConnection.setRequestMethod("GET");
            return Result.success(searchConnection);
        }
        catch (IOException e) {
            return Result.failure(e);
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
