package com.example.mally;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class MusicAdapter extends RecyclerView.Adapter<MusicAdapter.Holder> {

    private List<Music> list;
    private Context context;

    public MusicAdapter(Context ctx, List<Music> list) {
        this.context = ctx;
        this.list = list;
    }

    @Override
    public Holder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new Holder(LayoutInflater.from(context)
                .inflate(R.layout.item_music, parent, false));
    }

    @Override
    public void onBindViewHolder(Holder h, int pos) {
        Music m = list.get(pos);

        h.title.setText(m.title);
        h.artist.setText(m.artist);
        h.category.setText(m.category);

        boolean isPlaying =
                MyMusique.currentPos == pos &&
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

    static class Holder extends RecyclerView.ViewHolder {
        TextView title, artist, category;
        Button btnPlay;

        Holder(View v) {
            super(v);
            title = v.findViewById(R.id.txtTitle);
            artist = v.findViewById(R.id.txtArtist);
            category = v.findViewById(R.id.txtCategory);
            btnPlay = v.findViewById(R.id.btnPlay);
        }
    }
}
