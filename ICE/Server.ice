//
// Copyright (c) ZeroC, Inc. All rights reserved.
//

#pragma once

module Server
{
    class Music {
        int currentId;
        string id;
        string titre;
        string artiste;
        string album;
        string path;
    }
    
    ["python:seq:default"] sequence<Music> MusicList;

    interface Hello
    {
        void sayHello();
        void sayFuckOff();
        void topGenres();
        void topArtist();
        void searchVoice(string text);
        string searchBar(string text);
        void library();
        void bookmarks();
        void music(int id);
        void like(int id);

        MusicList findAll(out MusicList res);
    }
    
    interface Administration
    {
        void add(string title, string artist, string album, string path);
        bool delete(int identifier);
    }
}
