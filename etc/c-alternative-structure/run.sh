time gcc $1 -Wall -lm -lpthread && (printf '\x03\x00\x00\x00\x0A\x0B\x0C' | ./a.out)
ls -l a.out
