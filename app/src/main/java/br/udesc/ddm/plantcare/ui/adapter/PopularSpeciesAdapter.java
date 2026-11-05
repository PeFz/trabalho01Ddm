package br.udesc.ddm.plantcare.ui.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import br.udesc.ddm.plantcare.R;
import br.udesc.ddm.plantcare.data.local.entity.SpeciesModel;
import br.udesc.ddm.plantcare.utils.PlantImageHelper;

public class PopularSpeciesAdapter extends RecyclerView.Adapter<PopularSpeciesAdapter.SpeciesViewHolder> {

    public interface OnSpeciesSelectListener {
        void onSpeciesSelected(SpeciesModel species);
    }

    private final List<SpeciesModel> speciesList;
    private final OnSpeciesSelectListener listener;

    public PopularSpeciesAdapter(List<SpeciesModel> speciesList, OnSpeciesSelectListener listener) {
        this.speciesList = speciesList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public SpeciesViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_popular_species, parent, false);
        return new SpeciesViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SpeciesViewHolder holder, int position) {
        SpeciesModel species = speciesList.get(position);
        holder.tvDifficultyBadge.setText(species.getDifficultyBadge());
        holder.tvSpeciesName.setText(species.getName());
        holder.tvSpeciesScientific.setText(species.getScientificName());
        holder.tvSpeciesMeta.setText("💧 " + species.getFrequencyDays() + " dias  ☀️ " + species.getLightLabel());
        holder.ivSpeciesThumb.setImageResource(PlantImageHelper.getDrawableForKey(species.getImageKey()));

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) listener.onSpeciesSelected(species);
        });
    }

    @Override
    public int getItemCount() {
        return speciesList != null ? speciesList.size() : 0;
    }

    static class SpeciesViewHolder extends RecyclerView.ViewHolder {
        ImageView ivSpeciesThumb;
        TextView tvDifficultyBadge, tvSpeciesName, tvSpeciesScientific, tvSpeciesMeta;

        SpeciesViewHolder(@NonNull View itemView) {
            super(itemView);
            ivSpeciesThumb = itemView.findViewById(R.id.ivSpeciesThumb);
            tvDifficultyBadge = itemView.findViewById(R.id.tvDifficultyBadge);
            tvSpeciesName = itemView.findViewById(R.id.tvSpeciesName);
            tvSpeciesScientific = itemView.findViewById(R.id.tvSpeciesScientific);
            tvSpeciesMeta = itemView.findViewById(R.id.tvSpeciesMeta);
        }
    }
}

