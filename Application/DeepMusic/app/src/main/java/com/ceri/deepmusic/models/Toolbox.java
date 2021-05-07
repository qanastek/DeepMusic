package com.ceri.deepmusic.models;

public class Toolbox {

    final public static String PAUSE = "pause";
    final public static String STOP = "stop";

    public static boolean isPlaying = false;
    public static boolean isPaused = false;

    public static int CURRENT_MUSIC_ID = 1;
    public static String CURRENT_MUSIC;

    public static Server.Music CURRENT_MUSIC_MAJ;
}
