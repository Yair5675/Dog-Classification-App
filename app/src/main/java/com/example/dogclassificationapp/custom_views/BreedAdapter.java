package com.example.dogclassificationapp.custom_views;

import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

/**
 * This class's purpose is to bind Breed objects into a recyclerView
 */
public class BreedAdapter extends RecyclerView.Adapter<BreedAdapter.BreedVH> {
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
        public BreedVH(@NonNull View itemView) {
            super(itemView);
        }
    }
}
