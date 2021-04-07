package com.ceri.deepmusic.ui.searchbar;

import androidx.lifecycle.ViewModelProviders;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.ceri.deepmusic.R;
import com.ceri.deepmusic.models.IceServer;
import com.ceri.deepmusic.ui.musics.dummy.DummyContent;
import com.ceri.deepmusic.ui.yourLibrary.YourLibraryViewModel;

public class SearchBar extends Fragment {

    // Get the IceServer Singleton Instance
    private static IceServer iceServer = IceServer.getInstance();

    private SearchBarViewModel mViewModel;

    private EditText searchbar;

    public static SearchBar newInstance() {
        return new SearchBar();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        mViewModel = ViewModelProviders.of(this).get(SearchBarViewModel.class);

        View view = inflater.inflate(R.layout.search_bar_fragment, container, false);

        final Button searchBtn = view.findViewById(R.id.searchBtn);
        searchBtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                search();
            }
        });

        searchbar = (EditText) view.findViewById(R.id.searchbar);

        return view;
    }

    private void search() {
        Log.d("SearchBar", "-------- Search ------------");

        String content = searchbar.getText().toString();

        Log.d("SearchBar", content);

        Server.Music[] items = SearchBar.iceServer.getHello().searchBar(content);

        for (Server.Music m : items) {
            Log.d("SearchBar", m.titre);
        }
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = ViewModelProviders.of(this).get(SearchBarViewModel.class);
        // TODO: Use the ViewModel
    }

}