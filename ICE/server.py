#!/usr/bin/env python

# LABRAK Yanis
# M1 ILSEN ALT

import os
import sys
import time
import datetime
import random

import Ice
import signal

import vlc
import pylev

Ice.loadSlice('Server.ice')
import Server

hostname = "192.168.0.29"
# hostname = "localhost"

from db import DB

# The database proxy
db = DB()

musics = db.getMusics()
print(musics)

start = ["lancer","lance","demarrer","demarre"]
stop = ["arrêter","arrêt","stopper","stop","éteint","éteins","étein","étain"]
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

    def send(self, offset, bytes, path, current):

        # print(offset)
        # print(bytes)

        # Open the file in Byte mode
        musicFile = open(path,'ab')

        # Move in the file
        musicFile.seek(offset)
        
        # Write the bytes
        musicFile.write(bytes)

        # Close the stream
        musicFile.close()

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

            if not actions_tokens or len(actions_tokens) <= 0:
                actions_tokens = [start[0]]

            return music, actions_tokens

        return None

    def startVoice(self, text, current):

        m, actions_tokens = self.searchVoice(text)

        print("------------------------")
        print(m)
        print(m.identifier)
        print(actions_tokens)

        if not m or not actions_tokens or len(actions_tokens) <= 0:
            return None, None

        action = actions_tokens[0]
        print(action)

        res = "stop"

        if action in start:
            res = self.start(m.identifier, current)
        elif action in pause:
            res = "pause"
        
        return res

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

        # Update the local instance
        musics = db.getMusics()

        print(musics)
        print(len(musics))
        return musics

    def demoSSL(self, current):            
        return "Je suis du contenu sensible qui doit être sécuriser!!!"

    # https://github.com/oaubert/python-vlc/blob/master/tests/test.py
    def start(self, identifier, current):

        musicsFound = [m for m in musics if m.identifier == identifier]

        if len(musicsFound) <= 0:
            return None
        
        path = os.path.join(os.path.dirname(__file__), musicsFound[0].path)
        print(path)

        seconds = int(datetime.datetime.now().strftime("%s")) * 1000
        identifier = str(seconds) + "_" + str(random.randint(0, 999999999))
        
        port = 20000

        # File Extension
        extension = path.split(".")[-1]

        urlPath = str(port) + "/stream_" + str(identifier) + "." + extension
        streamStr = "#transcode{acodec=mp3,ab=128,channels=2,samplerate=44100}:http{dst=:" + str(urlPath) + "}"

        myLibVlcInstance = vlc.Instance()

        print(str(identifier))
        print(str(path))
        print(str(streamStr))
        print(myLibVlcInstance.vlm_add_broadcast)

        output = myLibVlcInstance.vlm_add_broadcast(
            bytes(identifier,'utf-8'),
            bytes(path,'utf-8'),
            bytes(streamStr,'utf-8'),
            0,
            [],
            True,
            False)

        print("After")

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

        # # Ajoute la musique à la liste
        db.insert(m)

        # Update the local instance
        musics = db.getMusics()

        return m

    def delete(self, identifier, current):

        print("Delete {}!".format(identifier))
        
        # Remove the music from the database
        res = db.removeMusic(identifier)

        # Update the local instance
        musics = db.getMusics()

        return res

with Ice.initialize(sys.argv, "config.server") as communicator:

    signal.signal(signal.SIGINT, lambda signum, frame: communicator.shutdown())
    if hasattr(signal, 'SIGBREAK'):
        signal.signal(signal.SIGBREAK, lambda signum, frame: communicator.shutdown())

    adapter = communicator.createObjectAdapterWithEndpoints("Hello", "default -h " + hostname + " -p 10001:ssl -p 4064")

    adapter.add(HelloI(), Ice.stringToIdentity("hello"))
    adapter.add(AdministrationI(), Ice.stringToIdentity("administration"))

    adapter.activate()
    communicator.waitForShutdown()
