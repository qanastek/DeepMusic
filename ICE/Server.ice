//
// Copyright (c) ZeroC, Inc. All rights reserved.
//

#pragma once

module Server
{
    struct Music {
        int identifier;
        string titre;
        string artiste;
        string album;
        string path;
    };
    
    sequence<Music> MusicList;
    sequence<string> TitleList;

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
        void musicInfo(int identifier);
        void like(int identifier);

        TitleList showAll();

        MusicList findAll();
        Music findOne();
    };
    
    interface Administration
    {
        void add(string title, string artist, string album, string path);
        bool delete(int identifier);
    };
}
