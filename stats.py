#!/usr/bin/env python3
import subprocess
lines, words, characters = subprocess.check_output("find src -type f -exec cat {} \; | wc", shell=True).decode("utf-8").strip().split()
print(f"Lines:\t{lines}\nWords:\t{words}\nChars:\t{characters}")
