clear
clang-12 main.c -Wall -lm -g && time ./a.out > data.txt &&
python3 show.py data.txt
