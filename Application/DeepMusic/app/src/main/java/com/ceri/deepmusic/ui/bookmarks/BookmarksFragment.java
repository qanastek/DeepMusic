package com.ceri.deepmusic.ui.bookmarks;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.ceri.deepmusic.R;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Locale;

public class BookmarksFragment extends Fragment {

    private Context mContext;

    private BookmarksViewModel bookmarksViewModel;

    private boolean isPlaying = false;

    private MediaPlayer mp;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext=context;
    }

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

        final Button toggle = root.findViewById(R.id.toggle);

        toggle.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                startSpeechToText();
            }
        });

        final Button player = root.findViewById(R.id.player);

        player.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                togglePlay();
            }
        });

        return root;
    }

    private void togglePlay() {

        if (!isPlaying)
        {
            isPlaying = true;

            mp = new MediaPlayer();

            try {

                mp.reset(); // new one

                mp.setDataSource(getActivity(), Uri.parse("https://www.all-birds.com/Sound/western%20bluebird.wav"));

                //mp.prepareAsync();

                mp.setAudioStreamType(AudioManager.STREAM_MUSIC);

                mp.prepare(); // don't use prepareAsync for mp3 playback

                mp.start();

                // String songTitle = songsList.get(songIndex).get("songTitle");
                // songTitleLabel.setText(songTitle);

//                songProgressBar.setProgress(0);
//                songProgressBar.setMax(100);
//
//                // Updating progress bar
//                updateProgressBar();

            } catch (Exception e) {
                e.printStackTrace();
            }

        }
        else
        {
            isPlaying = false;

            mp.release();// stop Playing

            //mp = null;
        }

    }

    private void startSpeechToText() {

        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);

        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, "fr-FR");
//        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Speak something...");

        try {

            startActivityForResult(intent, 666);

        } catch (ActivityNotFoundException a) {

            Toast.makeText(
                getActivity(),
                "Sorry! Speech recognition is not supported in this device.",
                Toast.LENGTH_SHORT
            ).show();
        }
    }

    /**
     * Callback for speech recognition activity
     * */
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 666) {

            Log.d("---------------- res", String.valueOf(resultCode));

            if (null != data) {

                ArrayList<String> result = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);

                if(result != null && result.size() > 0) {

                    Log.d("---------------- res", "Before");

                    String text = result.get(0);

                    Log.d("---------------- res", text);

                    if(text.contains("démarrer")) {
                        Log.d("---------------- res", "----- Start Up -----");
                    }
                }

                Log.d("---------------- res", "After");
            }

            Log.d("---------------- res", "After Data Check");
        }
    }
}