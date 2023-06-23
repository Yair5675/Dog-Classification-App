package com.example.dogclassificationapp.custom_views;

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

    // A list containing all the breeds in the adapter:
    private ArrayList<Breed> breedsList;

    public BreedAdapter(ArrayList<Breed> breedsList) {
        this.breedsList = breedsList;
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
        holder.shownBreedImgV.setImageDrawable(breed.getMainImg());

        // Setting the information title:
        holder.infoTitleTV.setText(breed.getFullName());

        // Setting the information paragraph's text:
        holder.infoTv.setText(breed.getInfo());

        // Setting the information paragraph's image:
        holder.infoTv.setCompoundDrawablesRelativeWithIntrinsicBounds(null, null, breed.getBonusImg(), null);

        // Changing the visibility of the expandable part according to the
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

        // The main layout of the row (contains all components of the View-Holder):
        private final ConstraintLayout mainLayout;

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

            // Loading the expandable layout:
            this.expandableLayout = itemView.findViewById(R.id.breed_expandable_layout);

            // Loading the main layout:
            this.mainLayout = itemView.findViewById(R.id.breed_main_layout);

            // Setting an OnClickListener for the main layout:
            this.mainLayout.setOnClickListener(
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
