import Filesystem

class MusicI(Filesystem.Music):

    def __init__(self, titre="", artiste="", album="", path=""):

        self.id = 1
        self.titre = titre
        self.artiste = artiste
        self.album = album
        self.path = path

        print(self.id)