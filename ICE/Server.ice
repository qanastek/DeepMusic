//
// Copyright (c) ZeroC, Inc. All rights reserved.
//

#pragma once

module Server
{
    interface Hello
    {
        void sayHello();
        void sayFuckOff();
        void topGenres();
        void topArtist();
        void searchVoice(string text);
        void searchBar(string text);
        void library();
        void bookmarks();
        void music(int id);
        void like(int id);
    }
    interface Administration
    {
        void add();
        void delete(int id);
    }
}
