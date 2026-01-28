package com.example.mally;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.mally.R;

import java.util.List;

public class ActualiteAdapter extends RecyclerView.Adapter<ActualiteAdapter.ActualiteViewHolder> {

    private List<Actualite> actualites;

    public ActualiteAdapter(List<Actualite> actualites) {
        this.actualites = actualites;
    }

    //Ajout du clic
    public  interface OnItemClickListener {
        void onItemClick(Actualite actualite);
    }
    private OnItemClickListener listener;
    public ActualiteAdapter(List<Actualite> actualites, OnItemClickListener listener) {
        this.actualites = actualites;
        this.listener = listener;
    }

    public class ActualiteViewHolder extends RecyclerView.ViewHolder {
        TextView titre, description;

        public ActualiteViewHolder(View itemView) {
            super(itemView);
            titre = itemView.findViewById(R.id.txtTitre);
            description = itemView.findViewById(R.id.txtDescription);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (listener != null) {
                        int position = getAdapterPosition();
                        if (position != RecyclerView.NO_POSITION && listener != null) {
                            listener.onItemClick(actualites.get(position));
                        }
                    }
                }
            });
        }
    }

    @Override
    public ActualiteViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.activity_item_actualite, parent, false);
        return new ActualiteViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ActualiteViewHolder holder, int position) {
        Actualite actualite = actualites.get(position);

        holder.titre.setText(actualite.getTitle());
        holder.description.setText(actualite.getDescription());

    }

    @Override
    public int getItemCount() {
        return actualites.size();
    }
}
