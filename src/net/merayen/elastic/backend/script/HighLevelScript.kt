package net.merayen.elastic.backend.script

val script = """
var in_array[512]
var sample_rate
var a, b=4+5, c, out_array[256]

function my_function(c, d)                  # Input variables are scoped, and are initialized every time the function is run. c is not the same as the global c-variable
	sin(π * i / sample_rate)                # Note that variables are global and static, therefore "i" is completely fine to use here
											# Functions can not call other functions (?), we don't allow recursive calling, I think

for i in range(out_array.size)
	var z = 0
	out_array[i] = in_array[i] * my_function(a, b)

"""