package com.example.mally;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class ActualiteAdapter extends RecyclerView.Adapter<ActualiteAdapter.ViewHolder> {

    private List<Actualite> actualites;

    public ActualiteAdapter(List<Actualite> actualites) {
        this.actualites = actualites;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView titre, description, date;

        public ViewHolder(View itemView) {
            super(itemView);
            titre = itemView.findViewById(R.id.txtTitre);
            description = itemView.findViewById(R.id.txtDescription);
            date = itemView.findViewById(R.id.txtDate);
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.activity_item_actualite, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Actualite actualite = actualites.get(position);

        holder.titre.setText(actualite.getTitre());
        holder.description.setText(actualite.getDescription());
        holder.date.setText(actualite.getDate());

        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(v.getContext(), DetailActualiteActivity.class);
            intent.putExtra("titre", actualite.getTitre());
            intent.putExtra("date", actualite.getDate());
            intent.putExtra("description", actualite.getDescription());
            v.getContext().startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return actualites.size();
    }
}
