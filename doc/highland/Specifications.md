# Specifications

Highland is supposed to be a Python-similar language that is statically typed and that is meant for scripting instruments, but also creating instruments from scratch. And filters.
It is compiled to OpenCL/Java bytecode and is executed by whatever device supporting OpenCL/Java bytecode.

## Program structure
```
import some.other.module

def multiply(multiplier: fp32, data: fp32[])
    def calculate(sample)
        

    for i,x in iterate(data, 10, 100)
        data[i] *= multiplier
```