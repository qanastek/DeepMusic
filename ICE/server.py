#!/usr/bin/env python
#
# Copyright (c) ZeroC, Inc. All rights reserved.
#

import os
import sys
import time
import datetime
import random

import Ice
import signal

# import Music
# import MusicI

import vlc
import pylev

Ice.loadSlice('Server.ice')
import Server

# class TimeOfDay(Ice.Object):
#     def __init__(self, hour=0, minute=0, second=0):
#         self.hour = hour
#         self.minute = minute
#         self.second = second
 
#     def ice_staticId():
#         return '::M::TimeOfDay'
#     ice_staticId = staticmethod(ice_staticId)

musics = [
    Server.Music(1,"Mike D-Sturb & High Voltage","Artiste Test 1","Album Test 1","musics/Checkie Brown - Rosalie (CB 104).mp3"),
    Server.Music(2,"Mike Dee Yan-Key - Hold on","Artiste Test 2","Album Test 2","musics/Lobo Loco - Bad Guys (ID 1333).mp3"),
    Server.Music(3,"Checkie Brown - Rosalie (CB 104)","Artiste Test 3","Album Test 3","musics/Dee Yan-Key - Hold on.mp3"),
]

start = ["lancer","lance","demarrer","demarre"]
stop = ["arrêter","arrêt","stopper","stop"]
pause = ["pause"]
actions = start + stop + pause

players = {}

class HelloI(Server.Hello):

    def test(self, current):
        time = TimeOfDay()
        return time

    def sayHello(self, current):
        print("Hello World!")

    def topGenres(self, current):
        print("top_genres!")

    def topArtist(self, current):
        print("top_artist!")

    def searchVoice(self, text):

        print("search_voice {}!".format(text))

        # Tokenize
        splitted = text.split()

        # Get action tokens
        actions_tokens = [a for a in splitted if a in actions]

        # Get others tokens
        others_tokens = [a for a in splitted if a not in actions_tokens]

        # Rebuild the title of the music
        title = ' '.join(others_tokens).lower()

        # Search for the music thanks to the title
        if len(musics) <= 0:
            return None, None

        items = []
        
        # For each music
        for m in musics:

            musicTitle = m.titre.lower()

            # Get the current levenshtein distance
            distance = pylev.levenshtein(musicTitle, title)

            print(musicTitle, distance)

            # Add the distance and the music in the tuple
            couple = (distance, m)

            # Add the tuple to the list
            items.append(couple)

        # Sort them
        sorted_items = sorted(items, key=lambda tup: tup[0])

        if len(sorted_items) > 0:

            # Return the closest Music instance
            music = sorted_items[0][1]

            # Display the title
            print("search_voice: {}".format(music.titre))

            return music, actions_tokens

        return None

    def startVoice(self, text, current):

        m, actions_tokens = self.searchVoice(text)

        if not m or not actions_tokens or len(actions_tokens) <= 0:
            return None, None

        action = actions_tokens[0]

        if action in start:
            start(m.identifier, current)
        elif action in pause:
            pause(current)
        else:
            pause(current)
        
        return m

    # Search for the music thanks to the title
    def searchBar(self, text, current):
        
        text = text.lower().replace('\n','')

        print("Start search_bar {}!".format(text))

        if len(musics) <= 0:
            return None

        items = []
        
        # For each music
        for m in musics:

            title = m.titre.lower()
            print("----------------")
            print(text)
            print(title)
            print(text in title)
            print("----------------")

            # Check if contains
            if text in title:

                print("in")

                # Add the tuple to the list
                items.append(m)

        print(items)
        return items

    def library(self, current):
        print("library!")

    def bookmarks(self, current):
        print("bookmarks!")

    def musicInfo(self, identifier, current):
        print("music {}!".format(id))

    def like(self, identifier, current):
        print("like {}!".format(id))

    def showAll(self, current):
        print(musics)
        print(len(musics))
        return [a.titre for a in musics]
    
    def findOne(self, current):
        return musics[0]

    def findAll(self, current):
        print(musics)
        print(len(musics))
        return musics

    # https://github.com/oaubert/python-vlc/blob/master/tests/test.py
    def start(self, identifier, current):
        print("------------------------ Play")

        # Music.identifier == identifier

        musicsFound = [m for m in musics if m.identifier == identifier]

        if len(musicsFound) <= 0:
            return None
        
        path = os.path.join(os.path.dirname(__file__), musicsFound[0].path)
        print(path)

        seconds = int(datetime.datetime.now().strftime("%s")) * 1000
        identifier = str(seconds) + "_" + str(random.randint(0, 999999999))
        
        port = 20000
        hostname = "localhost"

        urlPath = str(port) + "/stream_" + str(identifier) + ".mp3"
        streamStr = "#transcode{acodec=mp3,ab=128,channels=2,samplerate=44100}:http{dst=:" + str(urlPath) + "}"

        myLibVlcInstance = vlc.Instance()

        print(str(identifier))
        print(str(path))
        print(str(streamStr))
        print(myLibVlcInstance.vlm_add_broadcast)

        # output = 0
        # output = vlc.libvlc_vlm_add_broadcast(myLibVlcInstance,str(identifier),str(path),str(streamStr),0,None,True,False)
        output = myLibVlcInstance.vlm_add_broadcast(
            bytes(identifier,'utf-8'),
            bytes(path,'utf-8'),
            bytes(streamStr,'utf-8'),
            0,
            [],
            True,
            False)

        print("After")

        # players[identifier] = media

        # players[identifier].play()
        
        url = "http://" + hostname + ":" + urlPath;

        if output != 0:
            cout << "Error brodcasting !!!!!"
            return None

        myLibVlcInstance.vlm_play_media(identifier)

        return url
        
class AdministrationI(Server.Administration):

    def add(self, title, artist, album, path, current):

        print("Add!")

        # Créer la musique
        m = Server.Music(int(time.time()),title,artist,album, path)

        # Ajoute la musique à la liste
        musics.append(m)

        return m

    def delete(self, identifier, current):

        print("Delete {}!".format(identifier))
        
        for m in musics:

            if m.identifier == identifier:

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

    # servant = NodeI("Fred")
    # adapter.add(servant, id)


    adapter.activate()
    communicator.waitForShutdown()
