#!/usr/bin/env python
#
# Copyright (c) ZeroC, Inc. All rights reserved.
#

import signal
import sys
import Ice

Ice.loadSlice('Server.ice')
import Server


class HelloI(Server.Hello):

    def sayHello(self, current):
        print("Hello World!")

    def sayFuckOff(self, current):
        print("Fuck Off!")

    def topGenres(self, current):
        print("top_genres!")

    def topArtist(self, current):
        print("top_artist!")

    def searchVoice(self, text, current):
        print("search_voice {}!".format(text))

    def searchBar(self, text, current):
        print("search_bar {}!".format(text))

    def library(self, current):
        print("library!")

    def bookmarks(self, current):
        print("bookmarks!")

    def music(self, id, current):
        print("music {}!".format(id))

    def like(self, id, current):
        print("like {}!".format(id))
        
class AdministrationI(Server.Administration):

    def add(self, current):
        print("Add!")

    def delete(self, id, current):
        print("Delete {}!".format(id))


#
# Ice.initialize returns an initialized Ice communicator,
# the communicator is destroyed once it goes out of scope.
#
with Ice.initialize(sys.argv) as communicator:

    #
    # Install a signal handler to shutdown the communicator on Ctrl-C
    #
    signal.signal(signal.SIGINT, lambda signum, frame: communicator.shutdown())
    if hasattr(signal, 'SIGBREAK'):
        signal.signal(signal.SIGBREAK, lambda signum, frame: communicator.shutdown())

    adapter = communicator.createObjectAdapterWithEndpoints("Hello", "default -h localhost -p 10000")

    adapter.add(HelloI(), Ice.stringToIdentity("hello"))
    adapter.add(AdministrationI(), Ice.stringToIdentity("administration"))

    adapter.activate()
    communicator.waitForShutdown()
