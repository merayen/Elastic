# Data structures
We need to be able to read and write data from a huge blob defined by the host.
We could solve that by having a huge, deep struct of structs that is automatically aligned to address 0.

## Using uninited structs
This method assigns a nested struct to a variable that has none of its elements inited.
Summation node: 
```
# module: add-node (one instance of it)
var node_data = struct(123456)  # Pointer address in the struct argument that makes it map that address?  
    var my_variable: fp16
    var another_variable: fp32
    var uninited: fp64
    var child_struct = struct
        var my_variable: fp16


def my_func()
    var noe = my_struct()
    noe.my_variable = 3
    
    def helper(my_struct_arg: my_struct)  # my_struct gets passed as a reference, not a copy
        my_struct.another_variable = 1337
```

### Issues
* We need to consider that the underlaying architecture doesn't guarantee that e.g fp16 is fp16.
