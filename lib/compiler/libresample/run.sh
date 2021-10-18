time clang-12 main.c -Wall -lm -g &&
./a.out > data.txt &&
python3 show.py data.txt
