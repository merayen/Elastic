time -f "Compile: %es" clang-10 main.c &&
time -f "Run: %es" ./a.out
#cp main.s main.s.old &&
#clang-10 -S -masm=intel main.c -o main.s &&
#diff -d -U 1000 main.s.old main.s > main.s.diff

