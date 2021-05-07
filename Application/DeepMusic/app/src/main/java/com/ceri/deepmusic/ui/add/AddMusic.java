package com.ceri.deepmusic.ui.add;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.ceri.deepmusic.R;
import com.ceri.deepmusic.models.IceServer;
import com.ceri.deepmusic.models.Toolbox;
import com.ceri.deepmusic.ui.yourLibrary.MyMusicsRecyclerViewAdapter;
import com.ceri.deepmusic.ui.yourLibrary.YourLibraryFragment;
import com.ceri.deepmusic.ui.yourLibrary.dummy.DummyContent;
import com.zeroc.Ice.InvocationFuture;
import com.zeroc.Ice.Util;

import java.io.File;
import java.io.FileInputStream;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.Random;
import java.util.concurrent.CompletableFuture;

import Server.Music;

public class AddMusic extends AppCompatActivity {

    private Context mContext;

    EditText title;
    EditText artist;
    EditText album;

    Button sendBtn;
    Button updateBtn;

    Server.Music music;

    // IceServer Singleton Instance
    private static IceServer iceServer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_music);

        this.mContext = getApplicationContext();

        music = (Music) getIntent().getSerializableExtra("music");

        // Get the IceServer Singleton Instance
        iceServer = IceServer.getInstance();

        title = (EditText)findViewById(R.id.title);
        artist   = (EditText)findViewById(R.id.artist);
        album   = (EditText)findViewById(R.id.album);

        if(music != null) {
            title.setText(music.titre);
            artist.setText(music.artiste);
            album.setText(music.album);
        }

        sendBtn   = (Button)findViewById(R.id.sendBtn);
        sendBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view)
            {
                add();
            }
        });

        updateBtn   = (Button)findViewById(R.id.updateBtn);
        updateBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view)
            {
                update();
            }
        });
    }

    private void checkPermissions() {

        if (ContextCompat.checkSelfPermission(mContext, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
        }

        if (ContextCompat.checkSelfPermission(mContext, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
        }

        if (ContextCompat.checkSelfPermission(mContext, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, 1);
        }
    }

    public void update() {

        String titleContent = title.getText().toString();
        String artistContent = artist.getText().toString();
        String albumContent = album.getText().toString();

        // Check Empty
        if(titleContent == null || artistContent == null || albumContent == null) return;

        Music m = iceServer.getHello().update(
            String.valueOf(music.identifier),
            titleContent,
            artistContent,
            albumContent,
            music.path
        );

        fetchContent(iceServer);

        Toast.makeText(this, "Music Updated", Toast.LENGTH_SHORT).show();

        finish();
    }

    public void add() {

        String path = upload();

        // Check Empty
        if(path == null) return;

        String titleContent = title.getText().toString();
        String artistContent = artist.getText().toString();
        String albumContent = album.getText().toString();

        Server.Music m = iceServer.getHello().add(titleContent,artistContent,albumContent,path);

        fetchContent(iceServer);

        finish();
    }

    public static void fetchContent(IceServer server) {

        Server.Music[] items = server.getHello().findAll();

        MyMusicsRecyclerViewAdapter.rawValues = Arrays.asList(items);
        MyMusicsRecyclerViewAdapter.mValues = Arrays.asList(items);

        YourLibraryFragment.adapter.notifyDataSetChanged();
    }

    public String upload() {

        checkPermissions();

        try {

            String path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).toString();

            path = path + "/sample.mp3";

            File song = new File(path);
            FileInputStream file = new FileInputStream(song);

            int chunkSize = 61440;
            int offset = 0;

            LinkedList<InvocationFuture<Void>> results = new LinkedList<InvocationFuture<Void>>();
            int numRequests = 5;

            String extension = MimeTypeMap.getFileExtensionFromUrl(song.toString());

            Random rand = new Random();
            int min = 1;
            int max = 999999;
            int rand1 = rand.nextInt(max - min + 1) + min;

            String remotePath = "musics/" + "android_" + rand1 + "_" + System.currentTimeMillis() + "." + extension;

            Log.d("Remote Path",remotePath);

            byte[] bs = new byte[chunkSize];

            while ((offset = file.read(bs)) != -1) {

                // Send up to numRequests + 1 chunks asynchronously.
                CompletableFuture<Void> f = AddMusic.iceServer.getHello().sendAsync(offset, bs, remotePath);
                offset += bs.length;

                // Wait until this request has been passed to the transport.
                InvocationFuture<Void> i = Util.getInvocationFuture(f);
                i.waitForSent();
                results.add(i);

                // Once there are more than numRequests, wait for the least
                // recent one to complete.
                while(results.size() > numRequests)
                {
                    i = results.getFirst();
                    results.removeFirst();
                    i.join();
                }
            }

            // Wait for any remaining requests to complete.
            while(results.size() > 0)
            {
                InvocationFuture<Void> i = results.getFirst();
                results.removeFirst();
                i.join();
            }

            return remotePath;

        } catch (Exception e) {
            System.err.println(e.toString());
        }

        return null;
    }
}