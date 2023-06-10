package com.example.dogclassificationapp.custom_views;

import android.content.Context;
import android.graphics.Canvas;
import android.transition.Slide;
import android.transition.TransitionManager;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

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
        this.setMode(DEFAULT_MODE);
    }

    public DogImageMainMode getMode() {
        return mode;
    }

    public void setMode(DogImageMainMode mode) {
        // Making a slide transition:
        if (this.mode != mode)
            TransitionManager.beginDelayedTransition(this, new Slide(Gravity.END));

        // Switching the mode:
        this.mode = mode;

        // Removing the current mode:
        this.removeAllViews();

        // Adding the new mode:
        switch (this.mode) {
            case PRE_CLASSIFIER:
                this.addView(this.preClassifierViews);
                break;

            case DATABASE:
                this.addView(this.databaseViews);
                break;
        }

        // Sending a request to update the view:
        requestLayout();
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
