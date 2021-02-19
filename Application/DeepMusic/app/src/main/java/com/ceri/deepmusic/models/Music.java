package com.ceri.deepmusic.models;

public class Music {

    public String titre;
    public String album;
    public String duration;

    public Music(String titre, String album, String duration) {
        this.titre = titre;
        this.album = album;
        this.duration = duration;
    }

    @Override
    public String toString() {
        return titre;
    }
}
