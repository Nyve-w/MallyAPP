package com.example.mally;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class MusicAdapter extends RecyclerView.Adapter<MusicAdapter.Holder> {

    private final List<Music> list = new ArrayList<>();
    private final Context context;

    public MusicAdapter(Context ctx, List<Music> initialList) {
        this.context = ctx;
        list.addAll(initialList);
    }

    @Override
    public Holder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new Holder(
                LayoutInflater.from(context)
                        .inflate(R.layout.item_music, parent, false)
        );
    }

    @Override
    public void onBindViewHolder(Holder h, int pos) {
        Music m = list.get(pos);

        h.title.setText(m.title);
        h.artist.setText(m.artist);
        h.category.setText(m.category);

        boolean isCurrent = MyMusique.currentPos == pos;

        // 🎯 HIGHLIGHT
        h.root.setBackgroundResource(
                isCurrent ? R.drawable.item_playing : R.drawable.item_normal
        );

        boolean isPlaying =
                isCurrent &&
                        MyMusique.mediaPlayer != null &&
                        MyMusique.mediaPlayer.isPlaying();

        h.btnPlay.setText(isPlaying ? "⏸" : "▶");

        h.btnPlay.setOnClickListener(v -> {
            if (context instanceof MyMusique) {
                ((MyMusique) context).playMusicFromAdapter(m, pos);
            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public void updateList(List<Music> newList) {
        list.clear();
        list.addAll(newList);
        notifyDataSetChanged();
    }

    static class Holder extends RecyclerView.ViewHolder {

        LinearLayout root;
        TextView title, artist, category;
        Button btnPlay;

        Holder(View v) {
            super(v);
            root = v.findViewById(R.id.itemRoot);
            title = v.findViewById(R.id.txtTitle);
            artist = v.findViewById(R.id.txtArtist);
            category = v.findViewById(R.id.txtCategory);
            btnPlay = v.findViewById(R.id.btnPlay);
        }
    }
    public Music getItem(int position) {
        return list.get(position);
    }

}
