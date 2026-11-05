package br.udesc.ddm.plantcare.ui.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import br.udesc.ddm.plantcare.R;
import br.udesc.ddm.plantcare.data.local.entity.SpeciesModel;
import br.udesc.ddm.plantcare.utils.PlantImageHelper;

public class CatalogSpeciesAdapter extends RecyclerView.Adapter<CatalogSpeciesAdapter.CatalogViewHolder> {

    public interface OnCatalogActionListener {
        void onUseSpecies(SpeciesModel species);
    }

    private List<SpeciesModel> items = new ArrayList<>();
    private final OnCatalogActionListener listener;

    public CatalogSpeciesAdapter(List<SpeciesModel> initialItems, OnCatalogActionListener listener) {
        if (initialItems != null) {
            this.items.addAll(initialItems);
        }
        this.listener = listener;
    }

    public void updateList(List<SpeciesModel> filtered) {
        this.items = filtered != null ? filtered : new ArrayList<>();
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public CatalogViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_catalog_species, parent, false);
        return new CatalogViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CatalogViewHolder holder, int position) {
        SpeciesModel species = items.get(position);
        holder.ivCatalogThumb.setImageResource(PlantImageHelper.getDrawableForKey(species.getImageKey()));
        holder.tvCatalogName.setText(species.getName());
        holder.tvCatalogScientific.setText(species.getScientificName());
        holder.tvCatalogDifficulty.setText(species.getDifficultyBadge());
        holder.tvCatalogSummary.setText(species.getCareSummary());
        holder.tvCatalogParams.setText("💧 " + species.getFrequencyDays() + " dias • ☀️ " + species.getLightLabel());

        holder.btnUseSpecies.setOnClickListener(v -> {
            if (listener != null) listener.onUseSpecies(species);
        });
        holder.itemView.setOnClickListener(v -> {
            if (listener != null) listener.onUseSpecies(species);
        });
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    static class CatalogViewHolder extends RecyclerView.ViewHolder {
        ImageView ivCatalogThumb;
        TextView tvCatalogName, tvCatalogDifficulty, tvCatalogScientific, tvCatalogSummary, tvCatalogParams, btnUseSpecies;

        CatalogViewHolder(@NonNull View itemView) {
            super(itemView);
            ivCatalogThumb = itemView.findViewById(R.id.ivCatalogThumb);
            tvCatalogName = itemView.findViewById(R.id.tvCatalogName);
            tvCatalogDifficulty = itemView.findViewById(R.id.tvCatalogDifficulty);
            tvCatalogScientific = itemView.findViewById(R.id.tvCatalogScientific);
            tvCatalogSummary = itemView.findViewById(R.id.tvCatalogSummary);
            tvCatalogParams = itemView.findViewById(R.id.tvCatalogParams);
            btnUseSpecies = itemView.findViewById(R.id.btnUseSpecies);
        }
    }
}

