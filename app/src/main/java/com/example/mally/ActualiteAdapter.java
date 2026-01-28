package com.example.mally;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.List;

public class ActualiteAdapter extends RecyclerView.Adapter<ActualiteAdapter.ViewHolder> {
    private List<Actualite> actualites;

    public ActualiteAdapter(List<Actualite> actualites) {
        this.actualites = actualites;
    }
    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView titre, description, date;
        ImageView imgArticle;
        public ViewHolder(View itemView) {
            super(itemView);
            titre=itemView.findViewById(R.id.txtTitre);
            description=itemView.findViewById(R.id.txtDescription);
            date=itemView.findViewById(R.id.txtDate);
            imgArticle=itemView.findViewById(R.id.imgArticle);
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.activity_item_actualite, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Actualite actualite=actualites.get(position);
        holder.titre.setText(actualite.getTitre());
        holder.description.setText(actualite.getDescription());
        holder.date.setText(actualite.getPublishedAt());
        holder.itemView.setOnClickListener(v -> {
            Intent intent= new Intent(v.getContext(), DetailActualiteActivity.class);

            intent.putExtra("titre", actualite.getTitre());
            intent.putExtra("date", actualite.getPublishedAt());
            intent.putExtra("description", actualite.getDescription());

            v.getContext().startActivity(intent);
        });
        Glide.with(holder.itemView.getContext()).load(actualite.getUrlToImage()).placeholder(R.drawable.image_error).into(holder.imgArticle);

    }

    @Override
    public int getItemCount() {
        return actualites.size();
    }

}
