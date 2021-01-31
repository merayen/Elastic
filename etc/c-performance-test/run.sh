clang-10 -O3 -lm main.c &&
# time -f "Compile: %e seconds" gcc -O3 main.c -lm &&
# clang-10 -O3 -S main.c &&
# gcc -O3 -S main.c &&
#ls -l a.out &&
time ./a.out
