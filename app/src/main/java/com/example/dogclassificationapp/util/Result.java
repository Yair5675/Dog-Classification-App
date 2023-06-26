package com.example.dogclassificationapp.util;


/**
 * The `Result` class represents the outcome of an operation that could fail. It provides a way to
 * handle success and failure scenarios explicitly.
 */
public class Result <T, E> {
    // The value of the variable if the operation was successful
    private final T value;

    // The error in case the operation failed:
    private final E error;

    private Result(T value, E error) {
        if (value != null) {
            this.value = value;
            this.error = null;
        }
        else {
            this.value = null;
            this.error = error;
        }
    }

    /**
     * Creates a Result object of a successful operation.
     * @param value The successful value that will be wrapped with the Result class.
     * @return A successful Result object containing the given value.
     * @param <T> The type of the successful value.
     * @param <E> The type of the error in case of failure.
     */
    public static <T, E> Result<T, E> success(T value) {
        return new Result<>(value, null);
    }

    /**
     * Creates a Result object of a failed operation.
     * @param error A description of the error that occurred.
     * @return A successful Result object containing the given value.
     * @param <T> The type of the successful value.
     * @param <E> The type of the error in case of failure.
     */
    public static <T, E> Result<T, E> failure(E error) {
        return new Result<>(null, error);
    }

    public T getValue() {
        return value;
    }

    public E getError() {
        return error;
    }

    /**
     * Checks if the current Result object contains some value (and not an error).
     * @return True if the Result object contains some value, False if the Result object has an
     *         error description.
     */
    public boolean isOk() {
        return this.value != null;
    }
}
