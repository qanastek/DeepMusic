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
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
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

import java.util.ArrayList;
import java.util.Locale;

public class YourLibraryFragment extends Fragment {

    private YourLibraryViewModel yourLibraryViewModel;

    // IceServer Singleton Instance
    private static IceServer iceServer;

    private Context mContext;

    private boolean isPlaying = false;
    private boolean isPaused = false;

    private MediaPlayer mp;

    private String currentMusic = null;

    private static final String ARG_COLUMN_COUNT = "column-count";
    private int mColumnCount = 1;

    RecyclerView recyclerView;

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

        yourLibraryViewModel = ViewModelProviders.of(this).get(YourLibraryViewModel.class);

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

        // Get the IceServer Singleton Instance
        iceServer = IceServer.getInstance();

        return view;
    }

    private void togglePlay() {

        Log.d("Zeroc-Ice","Toggle");

        String url = iceServer.getHello().start(1);
        Log.d("Zeroc-Ice", url);

        if(!isPlaying) {
            Log.d("Zeroc-Ice", "isPlaying");
            start(url);
        }
        else {
            Log.d("Zeroc-Ice", "toggleStop");
            toggleStop();
        }
    }

    private void start(String url) {

        this.currentMusic = url;
        Log.d("Zeroc-Ice", this.currentMusic);

        isPlaying = true;
        isPaused = false;

        mp = new MediaPlayer();

        try {

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

    private void togglePause() {

        Log.d("Zeroc-Ice","togglePause");

        // Pause
        if (isPlaying && !isPaused) {
            Log.d("Zeroc-Ice","togglePause isPlaying");
            mp.pause();
            isPaused = true;
        }
        else if (isPaused) {
            Log.d("Zeroc-Ice","togglePause isPaused");
            mp.start();
            isPaused = false;
        }
    }

    private void toggleStop() {

        Log.d("Zeroc-Ice","inside toggleStop");

        // Stop the audio stream
        if (isPlaying || isPaused) {

            mp.stop();
            mp.release();

            isPlaying = false;
            isPaused = false;
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