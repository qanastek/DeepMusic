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
        Music searchVoice(string text);
        MusicList searchBar(string text);
        void library();
        void bookmarks();
        void musicInfo(int identifier);
        void like(int identifier);

        void testVlc();

        TitleList showAll();

        MusicList findAll();
        Music findOne();
    };
    
    interface Administration
    {
        Music add(string title, string artist, string album, string path);
        bool delete(int identifier);
    };
}
