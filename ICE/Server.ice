//
// Yanis LABRAK - Master 1 ILSEN, Avignon University.
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
        string startVoice(string text);
        MusicList searchBar(string text);

        string start(int identifier);

        TitleList showAll();

        MusicList findAll();
        Music add(string title, string artist, string album, string path);
        bool delete(int identifier);
        
        void send(int offset, ByteSeq bytes, string path);

        string demoSSL();
    };
}
