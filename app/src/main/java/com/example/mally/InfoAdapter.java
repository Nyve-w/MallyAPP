package com.example.mally;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class InfoAdapter extends RecyclerView.Adapter<InfoAdapter.InfoViewHolder> {

    private Context context;
    private List<InfoItem> items;

    public InfoAdapter(Context context, List<InfoItem> items) {
        this.context = context;
        this.items = items;
    }

    @NonNull
    @Override
    public InfoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_info, parent, false);
        return new InfoViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull InfoViewHolder holder, int position) {
        InfoItem item = items.get(position);

        holder.title.setText(item.title);
        holder.content.setText(item.content);

        // Texte déroulable
        holder.content.setMaxLines(3);
        holder.content.setEllipsize(TextUtils.TruncateAt.END);
        holder.content.setOnClickListener(v -> {
            if (holder.content.getMaxLines() == 3) {
                holder.content.setMaxLines(Integer.MAX_VALUE);
                holder.content.setEllipsize(null);
            } else {
                holder.content.setMaxLines(3);
                holder.content.setEllipsize(TextUtils.TruncateAt.END);
            }
            holder.content.requestLayout();
        });

        // Source + date
        holder.sourceDate.setText(formatSourceDate(item.sourceName, item.publishedAt));

        // Image Glide avec placeholder
        Glide.with(context)
                .load(item.imageUrl != null && !item.imageUrl.isEmpty() ? item.imageUrl : "https://via.placeholder.com/400x200.png?text=No+Image")
                .placeholder(android.R.color.darker_gray)
                .error(android.R.color.darker_gray)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(holder.image);

        // Click sur la card → ouvrir navigateur
        holder.itemView.setOnClickListener(v -> {
            if (item.url != null && !item.url.isEmpty()) {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(item.url));
                context.startActivity(browserIntent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    private String formatSourceDate(String source, String dateStr) {
        String formattedDate = "";
        if (dateStr != null && !dateStr.isEmpty()) {
            SimpleDateFormat input = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.getDefault());
            SimpleDateFormat output = new SimpleDateFormat("dd MMM yyyy", Locale.getDefault());
            try {
                Date date = input.parse(dateStr);
                formattedDate = output.format(date);
            } catch (ParseException e) {
                e.printStackTrace();
                formattedDate = dateStr;
            }
        }
        return source + " • " + formattedDate;
    }

    static class InfoViewHolder extends RecyclerView.ViewHolder {
        TextView title, content, sourceDate;
        ImageView image;

        InfoViewHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.itemTitle);
            content = itemView.findViewById(R.id.itemContent);
            sourceDate = itemView.findViewById(R.id.itemSourceDate);
            image = itemView.findViewById(R.id.itemImage);
        }
    }
}
