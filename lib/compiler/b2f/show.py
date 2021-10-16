import pylab as pl
from math import *
import numpy as np
import sys

data = []
with open(sys.argv[1]) as f:
	for x in [float(x.strip()) for x in f.read().strip().splitlines()]:
		if repr(x) == 'nan':
			data.append([])
		else:
			data[-1].append(x)

for x in data:
	pl.plot(x)

pl.waitforbuttonpress()
pl.close()
