# Remove when contains special chars

# Dataset source: http://www.cp.jku.at/datasets/MMTD/
# Papers: https://arxiv.org/pdf/1904.00648.pdf

import re
import random

f = open("artists.txt", "r")
lines = f.read().split("\n")
artists = [a.split("\t")[2] for a in lines[:-1] if not re.match("^[a-zA-Z0-9_]*$", a.split("\t")[2])]
print(len(artists))

print("-"*50)

f = open("track.txt", "r")
lines = f.read().split("\n")
tracks = [a.split("\t")[1] for a in lines[:-1] if not re.match("^[a-zA-Z0-9_]*$", a.split("\t")[1])]
print(len(tracks))

start = ["lancer","lance","demarrer","demarre","débuter","commencer","débute","débuté","jouer","jouée","joue","joué","restart","redémaré","redémarré"]
stop = ["arrete","arete","arrête","arrêter","arrêt","stopper","stop","éteint","éteins","étein","étain","terminer","finish","terminate","end"]
pause = ["pause"]
events_tokens = start + stop + pause

prepositions = ["de","from","by","of","par"]

def addTags(track):

    nerTokens = []

    if len(track.split(" ")) > 0:
            
        for i,t in enumerate(track.split(" ")):

            if i == 0:
                nerTokens.append(t + "|NOM|B-TRACK")
            else:
                nerTokens.append(t + "|NOM|I-TRACK")
    else:
        nerTokens.append(t + "|NOM|I-TRACK")

    return " ".join(nerTokens)

def addTagsArtist(artist):
    
    nerTokens = []

    if len(artist.split(" ")) > 0:
            
        for i,t in enumerate(artist.split(" ")):

            if i == 0:
                nerTokens.append(t + "|NOM|B-ARTIST")
            else:
                nerTokens.append(t + "|NOM|I-ARTIST")
    else:
        nerTokens.append(t + "|NOM|I-ARTIST")

    return " ".join(nerTokens)

def generate():
    
    token = random.choice(events_tokens)
    track = addTags(random.choice(tracks))
    prep = random.choice(prepositions)
    artist = addTagsArtist(random.choice(artists))

    even = (random.randint(1, 9999) % 2 ) == 0

    # TODO: Lance la musique #music#
    # TODO: Lance la musique #music# de #artist#

    if token in start:    
        # Example: Lance #music# de/par #auteur#       
        if even:
            res = "{}|VER|START {} {}|PRP|O {}".format(token, track, prep, artist)
        # Example: Lance #music#
        else:
            res = "{}|VER|START {}".format(token, track)
    else:

        nerToken = "|VER|PAUSE" if token in pause else "|VER|STOP"

        # Example: Stop/Pause #music#
        if even:
            res = "{} {}".format(token + nerToken, track)
        # Example: Stop/Pause
        else:
            res = "{}".format(token + nerToken)
    
    res = "\n".join(res.split(" "))
    res = " ".join(res.split("|")) + "\n\n"

    return res

output_dir = "../processed/"
ext = ".txt"

TRAIN_SIZE = 1500
DEV_SIZE = 500
TEST_SIZE = 500

f = open(output_dir + "train" + ext, "w")
for i in range(TRAIN_SIZE):
    f.write(generate())
f.close()

f = open(output_dir + "dev" + ext, "w")
for i in range(DEV_SIZE):
    f.write(generate())
f.close()

f = open(output_dir + "test" + ext, "w")
for i in range(TEST_SIZE):
    f.write(generate())
f.close()
