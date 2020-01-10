#!/usr/bin/env python3
import subprocess
lines, words, characters = map(int, subprocess.check_output("find src -type f -exec cat {} \; | awk NF | awk '{ sub(/^[ \t]+/, \"\"); print }' | wc", shell=True).decode("utf-8").strip().split())
print(f"Lines:\t{lines//1000}K ({round(lines/1027104 * 100)}% of Workshop)\nWords:\t{words//1000}K ({round(words/3044593 * 100)}% of Workshop)\nChars:\t{characters//1000}K ({round(characters/34968832 * 100)}% of Workshop)")
