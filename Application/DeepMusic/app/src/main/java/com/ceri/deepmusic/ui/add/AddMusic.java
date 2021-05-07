package com.ceri.deepmusic.ui.add;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
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
import com.ceri.deepmusic.models.MyFileUtils;
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

    String path;

    private Button buttonBrowse;

    Server.Music music;

    // IceServer Singleton Instance
    private static IceServer iceServer;

    private static final int MY_REQUEST_CODE_PERMISSION = 1000;
    private static final int MY_RESULT_CODE_FILECHOOSER = 2000;
    private static final String LOG_TAG = "AndroidExample";

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

        sendBtn   = (Button) findViewById(R.id.sendBtn);
        sendBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view)
            {
                add();
            }
        });

        updateBtn   = (Button) findViewById(R.id.updateBtn);
        updateBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view)
            {
                update();
            }
        });

        buttonBrowse = (Button) findViewById(R.id.button_browse);
        buttonBrowse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                askPermissionAndBrowseFile();
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

        String remotePath = upload(path);

        // Check Empty
        if(remotePath == null) return;

        String titleContent = title.getText().toString();
        String artistContent = artist.getText().toString();
        String albumContent = album.getText().toString();

        Server.Music m = iceServer.getHello().add(titleContent,artistContent,albumContent,music != null ? music.path : remotePath);

        fetchContent(iceServer);

        finish();
    }

    public static void fetchContent(IceServer server) {

        Server.Music[] items = server.getHello().findAll();

        MyMusicsRecyclerViewAdapter.rawValues = Arrays.asList(items);
        MyMusicsRecyclerViewAdapter.mValues = Arrays.asList(items);

        YourLibraryFragment.adapter.notifyDataSetChanged();
    }

    private void askPermissionAndBrowseFile()  {

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) { // Level 23

            // Check if we have Call permission
            int permisson = ActivityCompat.checkSelfPermission(mContext, Manifest.permission.READ_EXTERNAL_STORAGE);

            if (permisson != PackageManager.PERMISSION_GRANTED) {
                this.requestPermissions(
                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                        MY_REQUEST_CODE_PERMISSION
                );
                return;
            }
        }

        doBrowseFile();
    }

    private void doBrowseFile()  {

        Intent chooseFileIntent = new Intent(Intent.ACTION_GET_CONTENT);

        chooseFileIntent.setType("*/*");
        chooseFileIntent.addCategory(Intent.CATEGORY_OPENABLE);
        chooseFileIntent = Intent.createChooser(chooseFileIntent, "Choose a file");

        startActivityForResult(chooseFileIntent, MY_RESULT_CODE_FILECHOOSER);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == MY_RESULT_CODE_FILECHOOSER) {

            if (resultCode == Activity.RESULT_OK) {

                if (data != null) {

                    Uri fileUri = data.getData();

                    Log.i(LOG_TAG, "Uri: " + fileUri);

                    String filePath = null;

                    try {

                        filePath = MyFileUtils.getPath(mContext, fileUri);

                    } catch (Exception e) {

                        Log.e(LOG_TAG, "Error: " + e);
                        Toast.makeText(mContext, "Error: " + e, Toast.LENGTH_SHORT).show();
                    }

                    path = filePath;
                }
            }
        }

        super.onActivityResult(requestCode, resultCode, data);
    }

    public String upload(String path) {

        checkPermissions();

        try {

//            String path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).toString();
//            path = path + "/sample.mp3";

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