package com.example.dogclassificationapp.custom_views.dogs_activities_view;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import com.example.dogclassificationapp.R;

/**
 * This view represents most of the opening screen, and can switch between two modes as described
 * in the DogImageMainMode enum's documentation.
 * The view provides functionality for both activities and allows to transition between them.
 */
public class DogImageMainView extends ViewGroup {
    private DogImageMainMode mode; /* The current mode of the view */
    private View databaseViews; /* All the views of the database mode */
    private View preClassifierViews; /* All the views of the pre-classifier mode */

    // The default mode of the view (will be the mode when creating the view):
    private static final DogImageMainMode DEFAULT_MODE = DogImageMainMode.PRE_CLASSIFIER;

    public DogImageMainView(Context context) {
        super(context);
        this.init();
    }

    public DogImageMainView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.init();
    }

    public DogImageMainView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.init();
    }

    private void init() {
        // Setting the views of the two modes:
        final LayoutInflater inflater = LayoutInflater.from(getContext());

        this.databaseViews = inflater.inflate(R.layout.activity_database, this, false);
        this.preClassifierViews = inflater.inflate(R.layout.activity_preclassifier, this, false);

        // Setting the mode:
        this.mode = DEFAULT_MODE;
        this.updateViews();
    }

    public DogImageMainMode getMode() {
        return mode;
    }

    /**
     * Adds the correct views depending on the current mode
     */
    private void updateViews() {
        switch (this.mode) {
            case PRE_CLASSIFIER:
                this.addView(this.preClassifierViews);
                break;

            case DATABASE:
                this.addView(this.databaseViews);
                break;
        }
    }

    public void setMode(DogImageMainMode mode) {
        // Making a slide transition:
        if (this.mode != mode)
            this.animateTransition(mode);

        // Switching the mode:
        this.mode = mode;

        // Removing the current mode:
        this.removeAllViews();

        // Adding the new mode:
        this.updateViews();

        // Sending a request to update the view:
        requestLayout();
    }

    /**
     * Creates the animation that happens when transitioning between one mode and the other.
     * @param newMode The new mode that the view will transition into (should be different from the
     *                current mode).
     */
    private void animateTransition(DogImageMainMode newMode) {
        // Choosing the correct animation based on the new mode:
        int inAnimationID, outAnimationID;

        switch (newMode) {
            // If the new mode is pre-classifier, make the activity slide to the left:
            case PRE_CLASSIFIER:
                inAnimationID = R.anim.slide_in_left;
                outAnimationID = R.anim.slide_out_left;
                break;
            // If the new mode is database, make the activity slide to the right:
            case DATABASE:
                inAnimationID = R.anim.slide_in_right;
                outAnimationID = R.anim.slide_out_right;
                break;
            // The code shouldn't reach here, but if it does cancel the animation:
            default: return;
        }

        // Apply the animation:
        final Animation inAnimation = AnimationUtils.loadAnimation(getContext(), inAnimationID);
        final Animation outAnimation = AnimationUtils.loadAnimation(getContext(), outAnimationID);
        this.applyTransition(inAnimation, outAnimation);
    }

    /**
     * Applies the given animation on the view.
     * @param inAnimation The animation that will be applied to views coming into the screen.
     * @param outAnimation The animation that will be applied to views coming out of the screen.
     */
    private void applyTransition(Animation inAnimation, Animation outAnimation) {
        // Apply the out animation:
        this.applyAnimation(outAnimation);

        // Apply the in animation (with delay to ensure everything is executed in the right order):
        this.postDelayed(() -> applyAnimation(inAnimation), 0);
    }

    /**
     * Applies an animation to all current children-views of the view.
     * @param animation The animation that will be applied.
     */
    private void applyAnimation(Animation animation) {
        final int childCount = getChildCount();
        for (int i = 0; i < childCount; i++) {
            final View child = getChildAt(i);
            child.startAnimation(animation);
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        // Measure child views:
        int childCount = getChildCount();
        for (int i = 0; i < childCount; i++) {
            View child = getChildAt(i);
            measureChild(child, widthMeasureSpec, heightMeasureSpec);
        }
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        // Layout child views:
        final int childCount = getChildCount();
        for (int i = 0; i < childCount; i++) {
            final View child = getChildAt(i);
            child.layout(l, t, r, b);
        }
    }

    @Override
    protected void dispatchDraw(Canvas canvas) {
        super.dispatchDraw(canvas);

        // Draw child views:
        final int childCount = getChildCount();
        for (int i = 0; i < childCount; i++) {
            final View child = getChildAt(i);
            drawChild(canvas, child, getDrawingTime());
        }
    }

}
