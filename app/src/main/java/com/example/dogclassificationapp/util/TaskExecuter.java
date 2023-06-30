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

    // The current amount of times that the function was run:
    private int currentTry;

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

    public TaskExecuter(long waitTime, Task<T, E> task, int MAX_TRIES) {
        // Resetting the current try count:
        this.currentTry = 0;

        // Setting the other attributes:
        this.waitTime = waitTime;
        this.task = task;
        this.MAX_TRIES = MAX_TRIES;
    }
}
