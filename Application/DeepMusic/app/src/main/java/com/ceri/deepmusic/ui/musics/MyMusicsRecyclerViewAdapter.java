package com.ceri.deepmusic.ui.musics;

import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.ceri.deepmusic.R;

import java.util.List;

/**
 * {@link RecyclerView.Adapter} that can display a {@link OldMusic}.
 * TODO: Replace the implementation with code for your data type.
 */
public class MyMusicsRecyclerViewAdapter extends RecyclerView.Adapter<MyMusicsRecyclerViewAdapter.ViewHolder> {

    private final List<Server.Music> rawValues;
    private final List<Server.Music> mValues;

    public MyMusicsRecyclerViewAdapter(List<Server.Music> items) {
        rawValues = items;
        mValues = items;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.fragment_musics, parent, false);

        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d("-------------","Clicked");
            }
        });

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {

        // Get the current Music
        holder.mItem = mValues.get(position);

        holder.mIdView.setText(String.valueOf(position < 10 ? "0" + position : position));
        holder.mTitleView.setText(mValues.get(position).titre);
        holder.mAlbumView.setText(String.format("%s - %s", mValues.get(position).album, mValues.get(position).artiste));
    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public final View mView;

        public final TextView mIdView;
        public final TextView mTitleView;
        public final TextView mAlbumView;

        public Server.Music mItem;

        public ViewHolder(View view) {
            super(view);
            mView = view;

            mIdView = (TextView) view.findViewById(R.id.identifierMusic);
            mTitleView = (TextView) view.findViewById(R.id.title);
            mAlbumView = (TextView) view.findViewById(R.id.album);
        }

        @Override
        public String toString() {
            return super.toString() + " '" + mTitleView.getText() + "'";
        }
    }
}