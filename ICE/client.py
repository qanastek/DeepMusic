#!/usr/bin/env python
#
# Copyright (c) ZeroC, Inc. All rights reserved.
#

from tkinter import * 
from tkinter import scrolledtext

import os
import sys
import Ice

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

communicator = Ice.initialize(sys.argv)

# #
# # Ice.initialize returns an initialized Ice communicator,
# # the communicator is destroyed once it goes out of scope.
# #
# with Ice.initialize(sys.argv) as communicator:

# ------------ CLIENT ------------
hello = Server.HelloPrx.checkedCast(communicator.stringToProxy("hello:default -h localhost -p 10000"))

# ------------ ADMIN ------------
admin = Server.AdministrationPrx.checkedCast(communicator.stringToProxy("administration:default -h localhost -p 10000"))

hello.sayHello()
hello.topGenres()
hello.topArtist()

resAll = hello.showAll()
print(resAll)

res2 = hello.searchBar("D-Sturb & High ")

print("----------------before")

# test = hello.test()
print("----------------hello.findAll()")
musics = hello.findAll()
print(musics)
print("----------------hello.findOne()")
music = hello.findOne()
print(music.titre)
print("----------------musics")
# print(musics)
# print(test)

print(res2)

# creating vlc media player object
# media = vlc.MediaPlayer("musics/Dee Yan-Key - Hold on.mp3")

# start playing video
# media.play()

hello.library()
hello.bookmarks()
hello.musicInfo(10)
hello.like(10)

# Input Text
txt = Entry(f1)
txt.pack()

# OnClick
def search():
    
    # Current text in the TextArea
    # D-Sturb & High 
    # Lobo Loc
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
    # D-Sturb & High 
    # Lobo Loc
    currentText = txt.get()
    print(currentText)

    # Fetch the music
    res = hello.searchVoice(currentText)

    listView.delete(0,'end')
    listView.insert(0, res.titre)

# Button
btn = Button(f1, text="Search", command=search)
btn.pack(side = LEFT, fill = BOTH)

# Button
btn1 = Button(f1, text="Voice", command=voice)
btn1.pack(side = RIGHT, fill = BOTH)


# # Fill Up the ListView
# listViewRes = Listbox(f2)
# listViewRes.pack(side = LEFT, fill = BOTH, expand = False)
# scrollbarRes = Scrollbar(f2)
# scrollbarRes.pack(side = LEFT, fill = BOTH, expand = False) 
# listViewRes.config(yscrollcommand = scrollbarRes.set)
# scrollbarRes.config(command = listViewRes.yview)


# Fill Up the ListView
listView = Listbox(f2)
listView.pack(side = RIGHT, fill = BOTH)
for i, m in enumerate(musics):
    listView.insert(i, m.titre)

scrollbar = Scrollbar(f2)
scrollbar.pack(side = RIGHT, fill = BOTH, expand = False) 
listView.config(yscrollcommand = scrollbar.set)
scrollbar.config(command = listView.yview)

# Add a Music to the database
def add():
    m = admin.add(titre.get(), artiste.get(), album.get(), path.get())
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
    admin.delete(m.identifier)

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
    
    # mediaPlayer = myLibVlcInstance.media_new()

    mediaPlayer = vlc.libvlc_media_player_new_from_media(media)

    ret = vlc.libvlc_media_player_play(mediaPlayer)

    # media.play()

# Stop Audio
def stop():

    global currentMusic
    global mediaPlayer

    print("Stop 1 !")
    print(currentMusic)

    if currentMusic:
        print("Stop!")
        # hello.stop(currentMusic)
        vlc.libvlc_media_player_stop(mediaPlayer)

# Pause Audio
def pause():

    global currentMusic
    global mediaPlayer

    print("Pause 1 !")
    print(currentMusic)

    if currentMusic:
        print("Pause!")
        vlc.libvlc_media_player_pause(mediaPlayer)
        # hello.pause(currentMusic)

def voice():
    
    hello.startVoice("demarre D-Sturb & High Voltage")

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
path = Entry(f2)
path.pack()

f3 = Frame(f2)
f3.pack(side = BOTTOM)

addBtn = Button(f3, text="Add", command=add).pack(side = LEFT)
deleteBtn = Button(f3, text="Delete", command=delete).pack(side = RIGHT)
stopBtn = Button(f3, text="Stop", command=lambda *args: stop()).pack(side = RIGHT)
stopBtn = Button(f3, text="Pause", command=lambda *args: pause()).pack(side = RIGHT)
playBtn = Button(f3, text="Play", command=lambda *args: play()).pack(side = RIGHT)
playBtn = Button(f3, text="Voice", command=lambda *args: voice()).pack(side = RIGHT)

window.mainloop()