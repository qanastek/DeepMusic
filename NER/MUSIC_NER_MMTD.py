import os
import argparse

from datetime import datetime

from flair.embeddings import *
from flair.models import SequenceTagger
from flair.trainers import ModelTrainer
from flair.data import Corpus, MultiCorpus
from flair.datasets import ColumnCorpus

from pathlib import Path

parser = argparse.ArgumentParser(description='Music Named Entity Recognition')
parser.add_argument('-output', type=str, default="out/models/", help='The output root directory for the model')
parser.add_argument('-epochs', type=int, default=1, help='Number of Epochs')
args = parser.parse_args()

output = os.path.join(args.output, "flair_ner".upper() + "_MMTD_PLUS_" + "fr".upper() + "_" + str(args.epochs) + "_" + datetime.today().strftime("%Y-%m-%d-%H:%M:%S"))
print(output)

# define columns
columns = {0: 'text', 1: 'pos', 2: 'ner'}

# this is the folder in which train, test and dev files reside
data_folder = 'data/processed'

# init a corpus using column format, data folder and the names of the train, dev and test files
corpus: Corpus = ColumnCorpus(data_folder, columns,
                              train_file='train.txt',
                              test_file='test.txt',
                              dev_file='dev.txt',
                            #   column_delimiter="\|",
                            #   document_separator_token="\n",
                              in_memory=False)

# print(len(corpus.train))
# print(corpus.train[2])
# print(corpus.train[2].to_tagged_string('ner'))
# print(corpus.train[2].to_tagged_string('pos'))
# exit(0)

tag_type = 'ner'

tag_dictionary = corpus.make_tag_dictionary(tag_type=tag_type)

embedding_types = [

    # GloVe embeddings
    WordEmbeddings('fr'),

    # contextual string embeddings, forward
    FlairEmbeddings('fr-forward'),

    # contextual string embeddings, backward
    FlairEmbeddings('fr-backward'),
]

embeddings = StackedEmbeddings(embeddings=embedding_types)

tagger = SequenceTagger(hidden_size=256,
                        embeddings=embeddings,
                        tag_dictionary=tag_dictionary,
                        tag_type=tag_type,
                        use_crf=True)

trainer = ModelTrainer(tagger, corpus)

trainer.train(output,
              train_with_dev=True,
              checkpoint=True,
              max_epochs=args.epochs)