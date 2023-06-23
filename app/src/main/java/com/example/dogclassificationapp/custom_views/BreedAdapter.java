package com.example.dogclassificationapp.custom_views;

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
        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull BreedVH holder, int position) {

    }

    @Override
    public int getItemCount() {
        return 0;
    }

    public static class BreedVH extends RecyclerView.ViewHolder {

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

            // Loading the expandable layout:
            this.expandableLayout = itemView.findViewById(R.id.breed_expandable_layout);
        }
    }
}
