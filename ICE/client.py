#!/usr/bin/env python
#
# Copyright (c) ZeroC, Inc. All rights reserved.
#

import sys
import Ice

import vlc

Ice.loadSlice('Server.ice')
import Server

#
# Ice.initialize returns an initialized Ice communicator,
# the communicator is destroyed once it goes out of scope.
#
with Ice.initialize(sys.argv) as communicator:

    # ------------ CLIENT ------------
    hello = Server.HelloPrx.checkedCast(communicator.stringToProxy("hello:default -h localhost -p 10000"))
    
    hello.sayHello()
    hello.sayFuckOff()
    hello.topGenres()
    hello.topArtist()

    res1 = hello.searchVoice("demarre D-Sturb & High Voltage")
    res2 = hello.searchBar("D-Sturb & High ")

    print(res2)
 
    # creating vlc media player object
    media = vlc.MediaPlayer("musics/Dee Yan-Key - Hold on.mp3")
    
    # start playing video
    media.play()

    hello.library()
    hello.bookmarks()
    hello.music(10)
    hello.like(10)

    musics = hello.findAll()
    print("----------------musics")
    print(musics)

    # ------------ ADMIN ------------
    admin = Server.AdministrationPrx.checkedCast(communicator.stringToProxy("administration:default -h localhost -p 10000"))
    
    admin.add("Lobo Loco - Bad Guys (ID 1333)","Artiste Test 4","Album Test 4","musics/Dee Yan-Key - Hold on.mp3")
    admin.delete(10)
