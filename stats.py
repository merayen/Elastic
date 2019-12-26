#!/usr/bin/env python3
import subprocess
lines, words, characters = map(int, subprocess.check_output("find src -type f -exec cat {} \; | awk NF | wc", shell=True).decode("utf-8").strip().split())
print(f"Lines:\t{lines} ({round(lines/1027104 * 100)}% of Workshop)\nWords:\t{words} ({round(words/3044593 * 100)}% of Workshop)\nChars:\t{characters} ({round(characters/43208736 * 100)}% of Workshop)")
