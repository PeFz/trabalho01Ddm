package br.udesc.ddm.plantcare.ui.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import br.udesc.ddm.plantcare.R;
import br.udesc.ddm.plantcare.data.local.entity.PlantEntity;
import br.udesc.ddm.plantcare.utils.PlantImageHelper;

public class PlantAdapter extends RecyclerView.Adapter<PlantAdapter.PlantViewHolder> {

    public interface OnPlantInteractionListener {
        void onPlantClick(PlantEntity plant);
        void onWaterClick(PlantEntity plant);
    }

    private List<PlantEntity> plants = new ArrayList<>();
    private final OnPlantInteractionListener listener;

    public PlantAdapter(OnPlantInteractionListener listener) {
        this.listener = listener;
    }

    public void setPlants(List<PlantEntity> newPlants) {
        this.plants = newPlants != null ? newPlants : new ArrayList<>();
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public PlantViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_plant_card, parent, false);
        return new PlantViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PlantViewHolder holder, int position) {
        PlantEntity plant = plants.get(position);
        Context context = holder.itemView.getContext();

        holder.tvPlantRoom.setText("🛋️ " + plant.getRoom());
        holder.tvPlantFrequency.setText("a cada " + plant.getWateringFrequencyDays() + " dias");
        holder.tvPlantName.setText(plant.getName());
        holder.tvPlantScientific.setText(plant.getScientificName());
        holder.tvHydrationStatus.setText(plant.getStatusText());
        holder.tvHydrationPercent.setText(plant.getHydrationPercent() + "%");
        holder.pbHydration.setProgress(plant.getHydrationPercent());
        holder.tvSoilStatus.setText(plant.getSoilStatus());

        PlantImageHelper.loadPlantImage(holder.ivPlantThumb, plant.getImageKey(), plant.getCustomPhotoUri());

        if (plant.needsWater()) {
            // Estado Urgente / Precisa de Água (Fiel ao protótipo: Costela-de-Adão e Samambaia)
            holder.tvOverlayBadge.setText("!");
            holder.tvOverlayBadge.setBackgroundResource(R.drawable.bg_circle_urgent);
            holder.tvHydrationStatus.setTextColor(ContextCompat.getColor(context, R.color.terracotta_primary));
            holder.pbHydration.setProgressDrawable(ContextCompat.getDrawable(context, R.drawable.progress_hydration_urgent));

            holder.btnWaterAction.setText("💧 Regar Agora");
            holder.btnWaterAction.setBackgroundResource(R.drawable.bg_pill_terracotta_filled);
            holder.btnWaterAction.setTextColor(ContextCompat.getColor(context, R.color.white));
        } else {
            // Estado Hidratado (Fiel ao protótipo: Zamioculca e Cacto Ouriço)
            holder.tvOverlayBadge.setText("✓");
            holder.tvOverlayBadge.setBackgroundResource(R.drawable.bg_circle_healthy);
            holder.tvHydrationStatus.setTextColor(ContextCompat.getColor(context, R.color.green_primary));
            holder.pbHydration.setProgressDrawable(ContextCompat.getDrawable(context, R.drawable.progress_hydration_healthy));

            holder.btnWaterAction.setText("✓ Marcar Regada");
            holder.btnWaterAction.setBackgroundResource(R.drawable.bg_pill_outline);
            holder.btnWaterAction.setTextColor(ContextCompat.getColor(context, R.color.text_primary));
        }

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) listener.onPlantClick(plant);
        });

        holder.btnWaterAction.setOnClickListener(v -> {
            if (listener != null) listener.onWaterClick(plant);
        });
    }

    @Override
    public int getItemCount() {
        return plants.size();
    }

    static class PlantViewHolder extends RecyclerView.ViewHolder {
        ImageView ivPlantThumb;
        TextView tvOverlayBadge, tvPlantRoom, tvPlantFrequency, tvPlantName, tvPlantScientific;
        TextView tvHydrationStatus, tvHydrationPercent, tvSoilStatus, btnWaterAction;
        ProgressBar pbHydration;

        PlantViewHolder(@NonNull View itemView) {
            super(itemView);
            ivPlantThumb = itemView.findViewById(R.id.ivPlantThumb);
            tvOverlayBadge = itemView.findViewById(R.id.tvOverlayBadge);
            tvPlantRoom = itemView.findViewById(R.id.tvPlantRoom);
            tvPlantFrequency = itemView.findViewById(R.id.tvPlantFrequency);
            tvPlantName = itemView.findViewById(R.id.tvPlantName);
            tvPlantScientific = itemView.findViewById(R.id.tvPlantScientific);
            tvHydrationStatus = itemView.findViewById(R.id.tvHydrationStatus);
            tvHydrationPercent = itemView.findViewById(R.id.tvHydrationPercent);
            pbHydration = itemView.findViewById(R.id.pbHydration);
            tvSoilStatus = itemView.findViewById(R.id.tvSoilStatus);
            btnWaterAction = itemView.findViewById(R.id.btnWaterAction);
        }
    }
}

