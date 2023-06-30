package com.example.dogclassificationapp.util;

/**
 * A utility class used for running asynchronous functions. The class will repeatedly perform the
 * given function until it is successful. If the function failed, a specified amount of time will be
 * waited before rerunning it. The given function must return a Result variable to indicate whether
 * it failed or not.
 * @param <T> The type of the value that should be returned in case of success.
 * @param <E> The type of error description in case one occurs.
 */
public class TaskExecuter <T, E> {

    // The amount of milliseconds that will be waited between running the function in case of
    // failure:
    private final long waitTime;

    // The function that the executer will run:
    private final Task <T, E> task;

    // The callback functions that will happen once the function was executed:
    private final Callback<T, E> callback;

    // The maximum amount of times that the executer will try to run the function. If the function
    // hadn't yet been executed successfully, the executer will give up. If set to -1, the executer
    // will run the function indefinitely until it is successful:
    private final int MAX_TRIES;

    /**
     * The interface which holds the function that will be run.
     * @param <T> The type of the value that should be returned in case of success.
     * @param <E> The type of error description in case one occurs.
     */
    public interface Task <T, E> {
        /**
         * The main function that the executer will run until it is successful.
         * @return A result object indicating whether the task was successfully executed.
         */
        Result<T, E> run();
    }

    public TaskExecuter(long waitTime, int MAX_TRIES, Task<T, E> task, Callback<T, E> callback) {
        // Setting the attributes:
        this.waitTime = waitTime;
        this.task = task;
        this.callback = callback;
        this.MAX_TRIES = MAX_TRIES;
    }

    /**
     * Starts running the task that was given until it is performed successfully or the amount of
     * times that the function was run exceeded the maximum amount of times set.
     * The task will be run on a separate thread.
     */
    public void start() {
        // Creating the new thread:
        final Thread thread = new Thread(() -> {
            // Saving the current number of attempt:
            int currentTry = 0;

            boolean taskSuccessful = false;
            // Keeping looping as long as MAX_TRIES is -1 or the current try is less than the
            // threshold:
            while (!taskSuccessful && (MAX_TRIES == -1 || currentTry < MAX_TRIES)) {
                // Saving the time when the current attempt had started:
                final long startTime = System.currentTimeMillis();

                // Running the function and getting its result:
                final Result<T, E> result = this.task.run();

                // If the result is successful, run the "onSuccess" callback and close the loop:
                if (result.isOk()) {
                    this.callback.onSuccess(result.getValue());
                    taskSuccessful = true;
                }
                // If the result failed:
                else {
                    // Run the "onError" callback:
                    this.callback.onError(result.getError());

                    // Waiting for the given time to pass:
                    long currentTime = System.currentTimeMillis();
                    while (currentTime - startTime < this.waitTime)
                        currentTime = System.currentTimeMillis();
                }

                // Adding one to the current try:
                currentTry++;
            }
        });

        // Running the thread:
        thread.start();
    }
}
