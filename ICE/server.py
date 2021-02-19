#!/usr/bin/env python
#
# Copyright (c) ZeroC, Inc. All rights reserved.
#

import signal
import sys
import Ice

import vlc
import pylev

Ice.loadSlice('Server.ice')
import Server

class Music:

    currentId = 0

    def __init__(self, titre, artiste, album, path):

        Music.currentId += 1

        self.id = Music.currentId
        self.titre = titre
        self.artiste = artiste
        self.album = album
        self.path = path

        print(self.id)

musics = [
    Music("D-Sturb & High Voltage","Artiste Test 1","Album Test 1","musics/Dee Yan-Key - Hold on.mp3"),
    Music("Dee Yan-Key - Hold on","Artiste Test 2","Album Test 2","musics/Dee Yan-Key - Hold on.mp3"),
    Music("Checkie Brown - Rosalie (CB 104)","Artiste Test 3","Album Test 3","musics/Dee Yan-Key - Hold on.mp3"),
]

start = ["lancer","lance","demarrer","demarre"]
stop = ["arrêter","arrêt","stopper","stop"]
pause = ["pause"]
actions = start + stop + pause

def search(title):

    if len(musics) <= 0:
        return None

    items = []
    
    # For each music
    for m in musics:

        # Get the current levenshtein distance
        distance = pylev.levenshtein(m.titre, title)

        # Add the distance and the music in the tuple
        couple = (distance, m)

        # Add the tuple to the list
        items.append(couple)

    # Sort them
    sorted_items = sorted(items, key=lambda tup: tup[0])

    # Return the closest Music instance
    return sorted_items[0][1]

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

        splitted = text.split()

        actions_tokens = [a for a in splitted if a in actions]

        others_tokens = [a for a in splitted if a not in actions_tokens]

        title = ' '.join(others_tokens)

        music = search(title)

        print("search_voice: {}".format(music.titre))

    def searchBar(self, text, current):
        
        print("search_bar {}!".format(text))

        music = search(text)

        print("search_bar: {}".format(music.titre))

        # TODO: Return the ICE URL
        return music.path

    def library(self, current):
        print("library!")

    def bookmarks(self, current):
        print("bookmarks!")

    def music(self, id, current):
        print("music {}!".format(id))

    def like(self, id, current):
        print("like {}!".format(id))

    def findAll(self, current):
        return musics
        
class AdministrationI(Server.Administration):

    def add(self, title, artist, album, path, current):

        print("Add!")

        # Créer la musique
        m = Music(title,artist,album, path)

        # Ajoute la musique à la liste
        musics.append(m)

    def delete(self, identifier, current):

        print("Delete {}!".format(identifier))
        
        for m in musics:

            if m.id == identifier:

                # Supprime la musique
                musics.remove(m)

                return True
        
        print("Not found!")

        return False

# Ice.initialize returns an initialized Ice communicator,
# the communicator is destroyed once it goes out of scope.
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
