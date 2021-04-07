package com.ceri.deepmusic.ui.yourLibrary;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
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
import com.ceri.deepmusic.models.IceServer;
import com.ceri.deepmusic.models.Toolbox;
import com.ceri.deepmusic.ui.musics.MusicsFragment;
import com.ceri.deepmusic.ui.musics.dummy.DummyContent;

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

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public YourLibraryFragment() {
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.mContext = context;
    }

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        yourLibraryViewModel = ViewModelProviders.of(this).get(YourLibraryViewModel.class);

        View view = inflater.inflate(R.layout.fragment_library, container, false);

//        final TextView textView = root.findViewById(R.id.text_dashboard);
//
//        yourLibraryViewModel.getText().observe(getViewLifecycleOwner(), new Observer<String>() {
//            @Override
//            public void onChanged(@Nullable String s) {
//            textView.setText(s);
//            }
//        });


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

        Log.d("Zeroc-Ice","togglePlay");

        String url = iceServer.getHello().start(1);
        Log.d("Zeroc-Ice", url);

        if(isPlaying) {
            start(url);
        }
        else {
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

            mp.reset(); // new one
//                mp.setAudioStreamType(AudioManager.STREAM_MUSIC);
//                mp.setDataSource(getActivity(), Uri.parse(url));;

//                mp.setOnBufferingUpdateListener(mContext);
//                mp.setOnPreparedListener(mContext);

            mp.setDataSource(url);
            mp.setVolume(1.0f,1.0f);

            mp.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer media) {
                    media.start();
                }
            });

            mp.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {
                    mp.reset();
                    mp.release();
//                        mp = null;
                }
            });

//                mp.prepare();
            mp.prepareAsync();

            mp.start();

//                mp.setAudioStreamType(AudioManager.STREAM_MUSIC);

//                mp.setDataSource(String.valueOf(Uri.parse(url)));
//                mp.setDataSource(getActivity(), Uri.parse(url));
//                mp.setDataSource(getActivity(), Uri.parse("https://www.all-birds.com/Sound/western%20bluebird.wav"));

            //mp.prepareAsync();

//                mp.setAudioStreamType(AudioManager.STREAM_MUSIC);

//                mp.prepare(); // don't use prepareAsync for mp3 playback

//                mp.setOnBufferingUpdateListener(this);
//                mp.setOnPreparedListener(this);
//                mp.prepareAsync();
//                mp.start();

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

    private void togglePause() {

        Log.d("Zeroc-Ice","togglePause");

        // Pause
        if (isPlaying) {
            mp.pause();
            isPaused = true;
        }
    }

    private void toggleStop() {

        Log.d("Zeroc-Ice","toggleStop");

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

                Log.d("VoiceReco", "After");
            }

            Log.d("VoiceReco", "After Data Check");
        }
    }
}