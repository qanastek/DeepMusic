#!/usr/bin/env python
#
# Copyright (c) ZeroC, Inc. All rights reserved.
#

import sys
import Ice

Ice.loadSlice('Server.ice')
import Server

#
# Ice.initialize returns an initialized Ice communicator,
# the communicator is destroyed once it goes out of scope.
#
with Ice.initialize(sys.argv) as communicator:

    hello = Server.HelloPrx.checkedCast(communicator.stringToProxy("hello:default -h localhost -p 10000"))
    
    hello.sayHello()
    hello.sayFuckOff()
    hello.topGenres()
    hello.topArtist()
    hello.searchVoice("demarre D-Sturb & High Voltage")
    hello.searchBar("D-Sturb & High ")
    hello.library()
    hello.bookmarks()
    hello.music(10)
    hello.like(10)

    admin = Server.AdministrationPrx.checkedCast(communicator.stringToProxy("administration:default -h localhost -p 10000"))
    
    admin.add()
    admin.delete(10)
