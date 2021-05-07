# NER-MMTD
Named-entity recognition corpora for french / english voice recognition in the music industry based on the Million Musical Tweets dataset

## Steps

- Clone the project
- Download the MMTD corpora [here](http://www.cp.jku.at/datasets/MMTD/)
- Extract the files `artists.txt` and `track.txt` into `data/raw`
- If you want to generate the corpora again run `python getInfos.py`

## State-of-the-art

Using Flair and CRF on 100 epochs (1.5 hours on a E5-2690 v1):

|                  | precision | recall | f1-core |
|------------------|-----------|--------|---------|
| ARTIST           | 98.10%    | 100%   | 99.06%  |
| PAUSE            | 100%      | 100%   | 100%    |
| START            | 100%      | 100%   | 100%    |
| STOP             | 100%      | 100%   | 100%    |
| TRACK            | 99.42%    | 99.42% | 99.42%  |
| F1-score (macro) |           |        | 99.70%  |

## Sources

- http://www.cp.jku.at/datasets/MMTD/
- http://www.diva-portal.se/smash/get/diva2:1010104/FULLTEXT01.pdf
