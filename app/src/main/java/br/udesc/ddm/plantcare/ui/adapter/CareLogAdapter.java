package br.udesc.ddm.plantcare.ui.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import br.udesc.ddm.plantcare.R;
import br.udesc.ddm.plantcare.data.local.entity.CareLogEntity;

public class CareLogAdapter extends RecyclerView.Adapter<CareLogAdapter.LogViewHolder> {

    private List<CareLogEntity> logs = new ArrayList<>();

    public void setLogs(List<CareLogEntity> newLogs) {
        this.logs = newLogs != null ? newLogs : new ArrayList<>();
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public LogViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_care_log, parent, false);
        return new LogViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull LogViewHolder holder, int position) {
        CareLogEntity log = logs.get(position);
        holder.tvLogDate.setText(log.getTimeAgoAndDate());
        holder.tvLogCategory.setText(log.getCategoryBadge());
        holder.tvLogDescription.setText(log.getDescription());

        String type = log.getIconType() != null ? log.getIconType() : "WATER";
        switch (type) {
            case "NUTRITION":
                holder.tvLogTimelineDot.setText("🌱");
                break;
            case "PRUNE":
                holder.tvLogTimelineDot.setText("✂️");
                break;
            case "WATER":
            default:
                holder.tvLogTimelineDot.setText("💧");
                break;
        }
    }

    @Override
    public int getItemCount() {
        return logs.size();
    }

    static class LogViewHolder extends RecyclerView.ViewHolder {
        TextView tvLogTimelineDot, tvLogDate, tvLogCategory, tvLogDescription;

        LogViewHolder(@NonNull View itemView) {
            super(itemView);
            tvLogTimelineDot = itemView.findViewById(R.id.tvLogTimelineDot);
            tvLogDate = itemView.findViewById(R.id.tvLogDate);
            tvLogCategory = itemView.findViewById(R.id.tvLogCategory);
            tvLogDescription = itemView.findViewById(R.id.tvLogDescription);
        }
    }
}

