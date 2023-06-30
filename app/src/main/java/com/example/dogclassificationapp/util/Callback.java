package com.example.dogclassificationapp.util;

/**
 * A utility interface serving as a callback to asynchronous operations.
 * @param <T> The type of the expected value from the operation.
 * @param <E> The type of the error description (if one was raised).
 */
public interface Callback <T, E> {
    /**
     * The function that will be executed if the operation was executed successfully and no problems
     * occurred.
     * @param value The value that the operations returned.
     */
    void onSuccess(T value);

    /**
     * The function that will be executed if an error occurred while performing the operation.
     * @param error A short description of the error that occurred.
     */
    void onError(E error);
}
