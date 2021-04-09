package com.ceri.deepmusic.ui.yourLibrary;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.ceri.deepmusic.R;
import com.ceri.deepmusic.models.IceServer;
import com.ceri.deepmusic.models.Toolbox;
import com.ceri.deepmusic.ui.yourLibrary.dummy.DummyContent;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.Locale;

public class YourLibraryFragment extends Fragment {

    // IceServer Singleton Instance
    private static IceServer iceServer;

    private Context mContext;

    private static MediaPlayer mp;

    private static final String ARG_COLUMN_COUNT = "column-count";
    private int mColumnCount = 1;

    static RecyclerView recyclerView;

    EditText searchbar;

    public YourLibraryFragment() {
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.mContext = context;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            mColumnCount = getArguments().getInt(ARG_COLUMN_COUNT);
        }
    }

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_library, container, false);

        Context context = view.getContext();

        recyclerView = (RecyclerView) view.findViewById(R.id.list);;

        if (mColumnCount <= 1) {
            recyclerView.setLayoutManager(new LinearLayoutManager(context));
        } else {
            recyclerView.setLayoutManager(new GridLayoutManager(context, mColumnCount));
        }
        recyclerView.setAdapter(new MyMusicsRecyclerViewAdapter(DummyContent.ITEMS));

        final Button toggle = view.findViewById(R.id.toggle);
        toggle.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                startSpeechToText();
            }
        });

        final Button playerPlay = view.findViewById(R.id.playerPlay);
        playerPlay.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                togglePlay();
            }
        });

        final Button playerPause = view.findViewById(R.id.playerPause);
        playerPause.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                togglePause();
            }
        });

        final Button playerStop = view.findViewById(R.id.playerStop);
        playerStop.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                toggleStop();
            }
        });

//        final Button searchBtn = view.findViewById(R.id.searchBtn);
//        searchBtn.setOnClickListener(new View.OnClickListener() {
//
//            @Override
//            public void onClick(View arg0) {
//                search();
//            }
//        });

        searchbar = (EditText) view.findViewById(R.id.searchbar);

        searchbar.addTextChangedListener(new TextWatcher() {

            public void afterTextChanged(Editable s) {}

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String content = searchbar.getText().toString();
                Log.d("Zeroc-Ice",content);

                MyMusicsRecyclerViewAdapter adapter = (MyMusicsRecyclerViewAdapter) recyclerView.getAdapter();

                Log.d("adapter rawValues", String.valueOf(adapter.rawValues.size()));
                Log.d("adapter mValues", String.valueOf(adapter.mValues.size()));

                adapter.filter(content);

//                YourLibraryFragment frag = ((YourLibraryFragment) this.getParentFragment());
//                frag.
//                MyMusicsRecyclerViewAdapter.filter(content);
            }
        });

        // Get the IceServer Singleton Instance
        iceServer = IceServer.getInstance();

        return view;
    }

    public static void togglePlay() {

        Log.d("Zeroc-Ice","Toggle");

        String url = iceServer.getHello().start(Toolbox.CURRENT_MUSIC_ID);
        Log.d("Zeroc-Ice", url);

        if(!Toolbox.isPlaying) {
            Log.d("Zeroc-Ice", "isPlaying");
            start(url);
        }
        else {
            Log.d("Zeroc-Ice", "toggleStop");
            toggleStop();
        }
    }

    private static void start(String url) {

        Toolbox.CURRENT_MUSIC = url;
        Log.d("Zeroc-Ice", Toolbox.CURRENT_MUSIC);

        Toolbox.isPlaying = true;
        Toolbox.isPaused = false;

        mp = new MediaPlayer();

        try {

            Snackbar.make(
                recyclerView,
                "Start Audio",
                Snackbar.LENGTH_LONG
            ).show();

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {

                mp.setAudioAttributes(
                    new AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_MEDIA)
                    .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                    .setLegacyStreamType(AudioManager.STREAM_MUSIC)
                    .build()
                );

            } else {
                mp.setAudioStreamType(AudioManager.STREAM_MUSIC);
            }
            try {

                mp.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                    @Override
                    public void onPrepared(MediaPlayer player) {
                        player.start();
                    }
                });

                mp.setDataSource(url);
                mp.prepareAsync();

            } catch (Exception e) {
                e.printStackTrace();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void togglePause() {

        Log.d("Zeroc-Ice","togglePause");

        // Pause
        if (Toolbox.isPlaying && !Toolbox.isPaused) {

            Log.d("Zeroc-Ice","togglePause isPlaying");

            mp.pause();

            Toolbox.isPaused = true;

            Snackbar.make(
                recyclerView,
                "Pause Audio",
                Snackbar.LENGTH_LONG
            ).show();
        }
        else if (Toolbox.isPaused) {
            Log.d("Zeroc-Ice","togglePause isPaused");
            mp.start();
            Toolbox.isPaused = false;
        }
    }

    public static void toggleStop() {

        Log.d("Zeroc-Ice","inside toggleStop");

        // Stop the audio stream
        if (Toolbox.isPlaying || Toolbox.isPaused) {

            mp.stop();
            mp.release();

            Toolbox.isPlaying = false;
            Toolbox.isPaused = false;

            Snackbar.make(
                recyclerView,
                "Audio Stopped",
                Snackbar.LENGTH_LONG
            ).show();
        }
    }

    // Speech2Text
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

//    private void search() {
//
//        Log.d("SearchBar", "-------- Search ------------");
//
////        Log.d("SearchBar", content);
////
////        Server.Music[] items = SearchBar.iceServer.getHello().searchBar(content);
////
////        for (Server.Music m : items) {
////            Log.d("SearchBar", m.titre);
////        }
//    }

    /**
     * Callback for speech recognition activity
     * */
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 666) {

            Log.d("---------------- res", String.valueOf(resultCode));

            // If recieve data
            if (null != data) {

                ArrayList<String> result = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);

                // If not empty
                if(result != null && result.size() > 0) {

                    Log.d("VoiceReco", "Before");

                    String text = result.get(0);

                    Log.d("VoiceReco", text);

                    // Get the voice action
                    String action = YourLibraryFragment.iceServer.getHello().startVoice(text);
                    Log.d("VoiceReco", action);

                    if(action.equals(Toolbox.PAUSE)) {
                        Log.d("VoiceReco", "----- PAUSE -----");
                        togglePause();

                    }
                    else if(action.equals(Toolbox.STOP)) {
                        Log.d("VoiceReco", "----- STOP -----");
                        toggleStop();
                    }
                    else {
                        Log.d("VoiceReco", "----- Start Up -----");
                        start(action);
                    }
                }
            }
        }
    }

}