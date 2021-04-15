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
    sequence<byte> ByteSeq;

    interface Hello
    {
        void sayHello();
        void topGenres();
        void topArtist();
        string startVoice(string text);
        MusicList searchBar(string text);
        void library();
        void bookmarks();
        void musicInfo(int identifier);
        void like(int identifier);

        string start(int identifier);

        TitleList showAll();

        MusicList findAll();
        Music findOne();
        Music add(string title, string artist, string album, string path);
        bool delete(int identifier);
        
        void send(int offset, ByteSeq bytes, string path);

        string demoSSL();
    };
}
