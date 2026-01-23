package com.example.mally;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class LeaderboardAdapter extends RecyclerView.Adapter<LeaderboardAdapter.ViewHolder> {

    private List<PlayerScore> scores;

    public LeaderboardAdapter(List<PlayerScore> scores) {
        this.scores = scores;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(android.R.layout.simple_list_item_2, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        PlayerScore item = scores.get(position);
        holder.nameView.setText((position + 1) + ". " + item.getUsername());
        holder.scoreView.setText("Score: " + item.getScore());
    }

    @Override
    public int getItemCount() {
        return scores.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView nameView;
        public TextView scoreView;

        public ViewHolder(View view) {
            super(view);
            nameView = view.findViewById(android.R.id.text1);
            scoreView = view.findViewById(android.R.id.text2);
        }
    }
}