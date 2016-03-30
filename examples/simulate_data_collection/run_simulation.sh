#!/usr/bin/env bash -l
#
# ./run_simulation.sh &> run_simulation.txt

python simulate_data_collection.py q55 --cutoff 12 --redundancy 1

# remove one image so that query RQ4 has something to return
rm -f run/data/DRT240/DRT240_12000eV_002.img
