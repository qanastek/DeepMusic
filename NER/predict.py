import os
import argparse

from tkinter import *
from tkinter import scrolledtext

from flair.data import Sentence
from flair.models import SequenceTagger

parser = argparse.ArgumentParser(description='Predict MMTD')
parser.add_argument('-model_path', type=str, default="out/models/_FLAIR_NER_MMTD_PLUS_FR_100_2021-05-06-173653/final-model.pt", help='The model.pt path')
args = parser.parse_args()

# Window instance
window = Tk()

# Window settings
window.title("MMTD NER")
window.geometry('1280x720')

# TextArea
txt = scrolledtext.ScrolledText(window, width=65, height=30)
txt.grid(column=0,row=2)

text = Text(window, width=65, height=30)
text.grid(column=1, row=2)

# load the model you trained
model = SequenceTagger.load(args.model_path)

# lance Trip to Valhalla
def getInfo(res):

    title = []
    tokensAction = []

    tokens = res.split(" ")

    for i, token in enumerate(tokens):

        if "-TRACK" in token:

            prev = tokens[i-1]
            print(prev)

            title.append(prev)

        # Else other token
        elif '-' not in token and '<' in token:

            print("----------b")
            print(token)
            print('-' not in token)
            print('<' not in token)
            print("----------e")

            # past = tokens[i-1]
            # print(past)

            tokensAction.append(token)

    return tokensAction, " ".join(title)

# OnClick
def clicked():

    text.delete('1.0', END)
    
    # Current text in the TextArea
    currentText = txt.get('1.0', END)
    print(currentText)
    
    sentence = Sentence(currentText)

    # predict tags and print
    model.predict(sentence)

    res = sentence.to_tagged_string()

    print(res)
    print(getInfo(res))

    # Update the label
    # lbl['text'] = res
    text.insert(INSERT, res)

# Button
btn = Button(window, text="Complete", command=clicked)
btn.grid(column=0, row=3)

# Keep the window sync
window.mainloop()
