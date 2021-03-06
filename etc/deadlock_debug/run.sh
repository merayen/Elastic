diff -w -U 5 a.c b.c
clang-11 b.c -o elastic-debug-test -pthread -Wall -g && lldb-11 elastic-debug-test
