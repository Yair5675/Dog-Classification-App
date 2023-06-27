package com.example.dogclassificationapp.custom_views;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.example.dogclassificationapp.R;
import com.example.dogclassificationapp.classifier_logic.Breed;

import java.util.ArrayList;

/**
 * This class's purpose is to bind Breed objects into a recyclerView
 */
public class BreedAdapter extends RecyclerView.Adapter<BreedAdapter.BreedVH> {

    // Resources object to handle bitmaps later:
    private final Resources res;

    // A list containing all the breeds in the adapter:
    private final ArrayList<Breed> breedsList;

    // The maximum size of the main and bonus images:
    private static final int MAX_IMAGE_SIZE = 400;

    public BreedAdapter(ArrayList<Breed> breedsList, Resources res) {
        this.breedsList = breedsList;
        this.res = res;
    }

    @NonNull
    @Override
    public BreedVH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Getting the breed_row file as a view:
        final View rowView = LayoutInflater.from(parent.getContext()).inflate(R.layout.breed_row, parent, false);
        // Using the custom View-Holder:
        return new BreedVH(rowView);
    }

    @Override
    public void onBindViewHolder(@NonNull BreedVH holder, int position) {
        // Getting the breed that is currently being bind to the VH:
        final Breed breed = this.breedsList.get(position);

        // Setting the View-Holder's attributes using the data of the breed:

        // Setting the breed name:
        final String breedName = "Breed: " + breed.getFullName();
        holder.breedTV.setText(breedName);

        // Setting the confidence of the model in the breed:
        double confidence = 100 * breed.getConfidence();
        // Rounding to the second digit:
        confidence = Math.round(100 * confidence) / 100.0;

        final String confidenceTxt = "Confidence: " + confidence;
        holder.confidenceTV.setText(confidenceTxt);

        // Setting the shown image:
        holder.shownBreedImgV.setImageBitmap(breed.getMainImg());

        // Setting the information title:
        holder.infoTitleTV.setText(breed.getFullName());

        // Setting the information paragraph's text:
        holder.infoTv.setText(breed.getInfo());

        // Setting the information paragraph's image:
        // Setting the max size of the image:
        Bitmap bonusImg = breed.getBonusImg();

        if (bonusImg != null) {
            if (bonusImg.getWidth() > MAX_IMAGE_SIZE || bonusImg.getHeight() > MAX_IMAGE_SIZE)
                bonusImg = Bitmap.createScaledBitmap(bonusImg, MAX_IMAGE_SIZE, MAX_IMAGE_SIZE, false);
        }
        final Drawable bonusImgDrawable = new BitmapDrawable(this.res, bonusImg);
        holder.infoTv.setCompoundDrawablesRelativeWithIntrinsicBounds(null, null, bonusImgDrawable, null);

        // Changing the visibility of the expandable part according to the "expandable" attribute
        // of the current breed:
        holder.expandableLayout.setVisibility(
                breed.isExpanding() ? View.VISIBLE : View.GONE
        );
        Log.i("Breed adapter", "finished");
    }

    @Override
    public int getItemCount() {
        return this.breedsList.size();
    }

    public class BreedVH extends RecyclerView.ViewHolder {

        // The actual information paragraph (with a bonus image at the end):
        private final TextView infoTv;

        // The textView that will present the dog's breed:
        private final TextView breedTV;

        // The title of the information paragraph:
        private final TextView infoTitleTV;

        // The textView that will present the confidence of the model in the current breed:
        private final TextView confidenceTV;

        // The image that will be shown without expanding the breed:
        private final ImageView shownBreedImgV;

        // The expandable part of the row:
        private final ConstraintLayout expandableLayout;

        public BreedVH(@NonNull View itemView) {
            super(itemView);

            // Loading the main image:
            this.shownBreedImgV = itemView.findViewById(R.id.shown_breed_img);

            // Loading all the text views:
            this.breedTV = itemView.findViewById(R.id.breed_name_tv);
            this.confidenceTV = itemView.findViewById(R.id.breed_confidence_tv);
            this.infoTitleTV = itemView.findViewById(R.id.breed_info_title);
            this.infoTv = itemView.findViewById(R.id.breed_info_text);

            // Underlining the info title:
            this.infoTitleTV.setPaintFlags(this.infoTitleTV.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);

            // Loading the expandable layout:
            this.expandableLayout = itemView.findViewById(R.id.breed_expandable_layout);

            // The main layout of the row (contains all components of the View-Holder):
            ConstraintLayout mainLayout = itemView.findViewById(R.id.breed_main_layout);

            // Setting an OnClickListener for the main layout:
            mainLayout.setOnClickListener(
                    view -> {
                        // Getting the index of the pressed breed:
                        final int pressedIdx = getAdapterPosition();

                        // Closing any other breeds that are expanding:
                        for (int i = 0; i < breedsList.size(); i++) {
                            if (i != pressedIdx) {
                                breedsList.get(i).setExpanding(false);
                                notifyItemChanged(i);
                            }
                        }

                        // Negating the "expandable" attribute of the pressed breed:
                        final Breed pressedBreed = breedsList.get(pressedIdx);
                        pressedBreed.setExpanding(!pressedBreed.isExpanding());

                        // Notifying an item has changed:
                        notifyItemChanged(pressedIdx);
                    }
            );
        }
    }
}
