#!/usr/bin/env python
#
# Copyright (c) ZeroC, Inc. All rights reserved.
#

from tkinter import * 
from tkinter import scrolledtext
from tkinter.filedialog import askopenfilename
from tkinter import messagebox

import os
import sys
import Ice
import IceGrid
import time
from random import randrange

import vlc

Ice.loadSlice('Server.ice')
import Server

# Window instance
window = Tk()

# Window settings
window.title("Client")
# window.geometry('1280x720')

currentMusic = None
mediaPlayer = None
myLibVlcInstance = vlc.Instance()

f1 = Frame(window)
f1.pack(side = TOP)

f2 = Frame(window)
f2.pack(side = BOTTOM)

communicator = Ice.initialize(sys.argv, "configGrid.client")

hello = None

try:
    print("****** Connect to hello ******")
    hello = Server.HelloPrx.checkedCast(communicator.stringToProxy("hello"))
except Ice.NotRegisteredException:
    print("****** Connect to query ******")
    query = IceGrid.QueryPrx.checkedCast(communicator.stringToProxy("Server/Query"))
    hello = Server.HelloPrx.checkedCast(query.findObjectByType("::Server::Hello"))

if not hello:
    print("couldn't find a `::Server::Hello' object.")
    sys.exit(1)
    
musics = hello.findAll()
print(musics)

# Input Text
txt = Entry(f1)
txt.pack()

filePath = ""

# OnClick
def search():
    
    # Current text in the TextArea
    currentText = txt.get()
    print(currentText)

    # Fetch the music
    res = hello.searchBar(currentText)

    listView.delete(0,'end')
    for m in res:
        listView.insert(m.identifier, m.titre)

# OnClick
def voice():
    
    # Current text in the TextArea
    currentText = txt.get()
    print(currentText)

    # Fetch the music
    res = hello.startVoice(currentText)

# Button
btn = Button(f1, text="Search", command=search)
btn.pack(side = LEFT, fill = BOTH)

# Button
btn1 = Button(f1, text="Voice", command=voice)
btn1.pack(side = RIGHT, fill = BOTH)

# Fill Up the ListView
listView = Listbox(f2)
listView.pack(side = RIGHT, fill = BOTH)
for i, m in enumerate(musics):
    listView.insert(i, m.titre)

scrollbar = Scrollbar(f2)
scrollbar.pack(side = RIGHT, fill = BOTH, expand = False) 
listView.config(yscrollcommand = scrollbar.set)
scrollbar.config(command = listView.yview)

def fileChoose():
    global filePath
    filePath = askopenfilename()

def fileUpload(path):

    file = open(path,'rb')
    chunkSize = 4096
    offset = 0
    
    results = []
    numRequests = 5    

    # File Extension
    extension = path.split(".")[-1]
    
    remotePath = "musics/" + str(randrange(999999)) + "_" + str(int(time.time())) + "." + extension

    while True:
        
        chuck = file.read(chunkSize) # Read a chunk

        if chuck == bytes('','utf-8') or chuck == None:
            break
    
        r = hello.begin_send(offset, chuck, remotePath)
        offset += len(chuck)

        r.waitForSent()
        results.append(r)
    
        while len(results) > numRequests:
            r = results[0]
            del results[0]
            r.waitForCompleted()
    
    while len(results) > 0:
        r = results[0]
        del results[0]
        r.waitForCompleted()

    print("Finished")

    return remotePath
    
# Add a Music to the database
def add():

    global filePath

    print(filePath)

    if not filePath and not titre.get() and not album.get() and not artiste.get():
        messagebox.showwarning("Champs manquant","Il y a un chammps manquant!")
        return

    remotePath = fileUpload(filePath)

    if remotePath:
        filePath = None

    m = hello.add(titre.get(), artiste.get(), album.get(), remotePath)
    musics.append(m)
    listView.insert(m.identifier, m.titre)

# Remove a Music from the database
def delete():
    print("Delete!")
    index = listView.curselection()[0]
    m = musics.pop(index)
    print(index)
    print(m.titre)
    listView.delete(index)
    hello.delete(m.identifier)

# Play Audio
def play():

    global currentMusic
    global mediaPlayer

    print("Play!")

    stop()

    index = listView.curselection()[0]
    print(index)
    m = musics[index]
    print(m.identifier)

    url = hello.start(m.identifier)
    print(url)

    currentMusic = url
    print(currentMusic)
    print(url)

    media = vlc.libvlc_media_new_location(myLibVlcInstance, bytes(url,'utf-8'))

    mediaPlayer = vlc.libvlc_media_player_new_from_media(media)

    ret = vlc.libvlc_media_player_play(mediaPlayer)

# Stop Audio
def stop():

    global currentMusic
    global mediaPlayer

    print("Stop!")
    print(currentMusic)

    if currentMusic:
        print("Stoped!")
        vlc.libvlc_media_player_stop(mediaPlayer)

# Pause Audio
def pause():

    global currentMusic
    global mediaPlayer

    print("Pause!")
    print(currentMusic)

    if currentMusic:
        print("Paused!")
        vlc.libvlc_media_player_pause(mediaPlayer)

def voice():
    resVoice = hello.startVoice("demarre D-Sturb & High Voltage")

def ssl():
    print(hello.demoSSL())

# Input Text
Label(f2, text = "titre").pack()
titre = Entry(f2)
titre.pack()

Label(f2, text = "artiste").pack()
artiste = Entry(f2)
artiste.pack()

Label(f2, text = "album").pack()
album = Entry(f2)
album.pack()

Label(f2, text = "path").pack()
path = Button(f2, text="File...", command=fileChoose).pack()

f3 = Frame(f2)
f3.pack(side = BOTTOM)

# Buttons
addBtn = Button(f3, text="Add", command=add).pack(side = LEFT)
deleteBtn = Button(f3, text="Delete", command=delete).pack(side = RIGHT)
stopBtn = Button(f3, text="Stop", command=lambda *args: stop()).pack(side = RIGHT)
pauseBtn = Button(f3, text="Pause", command=lambda *args: pause()).pack(side = RIGHT)
playBtn = Button(f3, text="Play", command=lambda *args: play()).pack(side = RIGHT)
voiceBtn = Button(f3, text="Voice", command=lambda *args: voice()).pack(side = RIGHT)
# sslBtn = Button(f3, text="SSL", command=lambda *args: ssl()).pack(side = RIGHT)

window.mainloop()