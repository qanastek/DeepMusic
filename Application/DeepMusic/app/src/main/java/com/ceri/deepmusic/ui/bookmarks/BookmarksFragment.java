package com.ceri.deepmusic.ui.bookmarks;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.ceri.deepmusic.R;

public class BookmarksFragment extends Fragment {

    private BookmarksViewModel bookmarksViewModel;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        bookmarksViewModel = ViewModelProviders.of(this).get(BookmarksViewModel.class);

        View root = inflater.inflate(R.layout.fragment_bookmarks, container, false);

        final TextView textView = root.findViewById(R.id.text_bookmarks);

        bookmarksViewModel.getText().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
            textView.setText(s);
            }
        });

        return root;
    }
}