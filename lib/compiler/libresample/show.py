import pylab as pl
from math import *
import numpy as np
import sys

with open(sys.argv[1]) as f:
	data = [tuple(map(float, x.split(" "))) for x in f.read().strip().splitlines()]

max_diff = max(abs(a-b) for a,b in data)

pl.plot([a for a,b in data])
pl.plot([b for a,b in data])
pl.plot([abs(a-b)/max_diff for a,b in data])
pl.waitforbuttonpress()
pl.close()
