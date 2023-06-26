package com.example.dogclassificationapp.classifier_logic;

import android.content.Context;
import android.content.res.AssetManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.util.Log;

import com.example.dogclassificationapp.ml.DogModelLite;

import org.tensorflow.lite.DataType;
import org.tensorflow.lite.support.tensorbuffer.TensorBuffer;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Optional;

/**
 * A class to handle the logical part of processing and classifying images.
 */
public final class DogClassifier {
    // The context of the activity that started the model:
    private final Context context;

    // The different types of dogs the classifier can distinguish:
    private final ArrayList<String> labels;

    // The labels that match the Dog Image API:
    private final ArrayList<String> apiLabels;

    // The size of the image that will be passed into the model:
    private static final int IMAGE_SIZE = 256;

    // The name of the file that contains the normal labels:
    private static final String LABELS_FILE = "labels.csv";

    // The name of the file that contains the labels that match the Dog Images API:
    private static final String API_LABELS_FILE = "api_labels.csv";

    public DogClassifier(Context context, AssetManager assets) {
        this.context = context;

        // Loading the labels:
        this.labels = loadLabels(assets, LABELS_FILE).orElse(new ArrayList<>());

        // Loading the API labels:
        this.apiLabels = loadLabels(assets, API_LABELS_FILE).orElse(new ArrayList<>());

        // Printing the labels to the log:
        Log.i("Labels", this.labels.toString());
    }

    /**
     * The function receives the name of a CSV file that contains labels for the TF-Lite model,
     * reads that file and returns an arraylist of all labels in that file's first line (which
     * should b the only line).
     * @param assets An AssetManager object to allow the function to access the assets folder.
     * @param labelsFile The name of the csv file that will be read.
     * @return If reading the file was successful, the function returns an arraylist of all the
     *         labels that were in the file. If an error occurred, an empty optional is returned.
     */
    private static Optional<ArrayList<String>> loadLabels(AssetManager assets, String labelsFile) {
        try {
            // Opening the file:
            InputStream inputStream = assets.open(labelsFile);
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

            // Reading the names of the dogs:
            String line = bufferedReader.readLine();
            ArrayList<String> labels = new ArrayList<>(Arrays.asList(line.split(",")));

            // Closing our readers to free up resources:
            bufferedReader.close();
            inputStreamReader.close();
            inputStream.close();

            return Optional.of(labels);

        } catch (IOException e) {
            Log.e("Dog Classifier", "Failed to load labels of file \"" + labelsFile + "\"");
            e.printStackTrace();
            return Optional.empty();
        }
    }

    /**
     * Given an image of a dog, the model will return a list of breeds and sub-breeds of dogs, that
     * contain the probability that the dog inside the given image is of this breed.
     * @param dogImage The image with a dog that the model will process. Its dimensions must be
     *                 256x256 pixels.
     * @param res Resources object in order to have access to the application's resources.
     * @return If the model was loaded successfully, the function returns a list of Breed objects
     *         that each contain the probability that the breed of the dog in the image is the
     *         current breed. If an error occurred, an empty optional is returned.
     */
    public Optional<ArrayList<Breed>> getModelPredictions(Bitmap dogImage, Resources res) {
        // TODO: For some reason the max result is always Kerry blue Terrier. Fix that
        // Making sure that the dimensions of the image are valid:
        final int WIDTH = dogImage.getWidth();
        final int HEIGHT = dogImage.getHeight();

        if (WIDTH != IMAGE_SIZE || HEIGHT != IMAGE_SIZE) {
            Log.e("Dog Classifier", "Given image dimensions are incompatible: Width=" + WIDTH + ", Height=" + HEIGHT);
            return Optional.empty();
        }

        // If the image's dimensions are correct:
        try {
            // Loading the model:
            DogModelLite model = DogModelLite.newInstance(context);

            // Loading the RGB values:
            final int[][][] RGB = getRGBValues(dogImage);

            // Loading the values into a byte-buffer:
            final ByteBuffer byteBuffer = loadRGBIntoByteBuffer(RGB);

            // Creates inputs for reference.
            TensorBuffer inputFeature0 = TensorBuffer.createFixedSize(new int[]{1, 256, 256, 3}, DataType.FLOAT32);
            inputFeature0.loadBuffer(byteBuffer);

            // Running model inference and getting the results:
            DogModelLite.Outputs outputs = model.process(inputFeature0);
            TensorBuffer outputFeature0 = outputs.getOutputFeature0AsTensorBuffer();
            Log.i("Classifier outputs",
                    Arrays.toString(outputFeature0.getFloatArray()));

            // Releases model resources:
            model.close();

            // Converting the outputs into a breeds arraylist:
            final ArrayList<Breed> breeds = convertOutputsToBreeds(outputFeature0, res);
            return Optional.of(breeds);

        } catch (IOException e) {
            Log.e("Dog Classifier", "Failed to load/use model");
            e.printStackTrace();
            return Optional.empty();
        }
    }

    /**
     * The function accepts the outputs of the model and converts them into an arraylist of Breed
     * objects.
     * @param outputs The confidence of the model in each breed (by order).
     * @return An arraylist of Breed objects that's built using the given confidences of the model.
     */
    private ArrayList<Breed> convertOutputsToBreeds(TensorBuffer outputs, Resources res) {
        // Creating the list:
        ArrayList<Breed> breeds = new ArrayList<>();

        // Converting the tensor into a float array:
        final float[] confidences = outputs.getFloatArray();

        // Creating the breeds one by one:
        for (int i = 0; i < confidences.length; i++) {
            final Breed current = new Breed(res,
                                            this.getLabel(i),
                                            this.getAPILabel(i),
                                            confidences[i]);
            breeds.add(current);
        }

        return breeds;
    }

    /**
     * The function receives a three-dimensional array representing the RGB values of the image, and
     * loads them into a ByteBuffer to be used as inputs for the TF-Lite model. The function also
     * normalizes to values (changes them from 0-255 into 0-1).
     * @param rgbValues A three dimensional array representing the RGB values of an image.
     * @return A byte buffer loaded with the normalized RGB values.
     */
    private static ByteBuffer loadRGBIntoByteBuffer(int[][][] rgbValues) {
        // Loading the dimensions of the image:
        final int WIDTH = rgbValues[0].length;
        final int HEIGHT = rgbValues.length;
        final int CHANNELS = 3;

        // Creating a ByteBuffer after allocating enough memory:
        final ByteBuffer byteBuffer = ByteBuffer.allocateDirect(WIDTH * HEIGHT * CHANNELS * 4); // 4 bytes per float

        // Configure byte order for TensorFlow Lite
        byteBuffer.order(ByteOrder.nativeOrder());

        // Normalize pixel values and store in the ByteBuffer
        for (int[][] row : rgbValues) {
            for (int[] pixel : row) {
                for (int channel : pixel) {
                    // Extract the RGB component and normalize it:
                    final float value = channel / 255.0f;
                    // Add the normalized value to the ByteBuffer:
                    byteBuffer.putFloat(value);
                }
            }
        }

        // Rewind the ByteBuffer before using it as input
        byteBuffer.rewind();

        return byteBuffer;
    }

    /**
     * Given an image in Drawable format, the function extracts the RGB values of the image and
     * stores them in a three-dimensional array.
     * @param img The image whose pixels' RGB values will be returned.
     * @return A three dimensional array of the RGB values of the image. The first two dimensions of
     *         the array will be similar to the dimensions of the image, and the last dimension of
     *         the array will be 3 (RGB).
     */
    private static int[][][] getRGBValues(Bitmap img) {
        // Getting the dimensions of the image:
        final int WIDTH = img.getWidth();
        final int HEIGHT = img.getHeight();

        // Creating the array that will hold the values:
        final int[][][] rgbValues = new int[WIDTH][HEIGHT][3];

        // Scanning the pixels and extracting their RGB values:
        for (int y = 0; y < HEIGHT; y++) {
            for (int x = 0; x < WIDTH; x++) {
                final int pixel = img.getPixel(x, y);

                rgbValues[y][x][0] = (pixel >> 16) & 0xFF; // Red component
                rgbValues[y][x][1] = (pixel >> 8) & 0xFF; // Green component
                rgbValues[y][x][2] = pixel & 0xFF; // Blue component
            }
        }

        return rgbValues;
    }

    public String getLabel(int index) {
        return labels.get(index);
    }

    public String getAPILabel(int index) {
        return apiLabels.get(index);
    }
}
