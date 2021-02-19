import signal
import sys
import Ice

import Filesystem
 
class MusicI(Filesystem.Node):

    def __init__(self, id=0, titre="", artiste="", album="", path=""):

        self.id = id
        self.titre = titre
        self.artiste = artiste
        self.album = album
        self.path = path

        print(self.id)