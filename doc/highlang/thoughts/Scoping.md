# Scoping

## Practical example
```
use module_name

# This is the global scope for this module. It is special as its scope is available for other modules

var global_variable = 5

def my_func()
    # This scope is only available for this func and any children
    global_variable += 1  # Directly modifies parent variable. Synchronization is not guaranteed at all.
    var inner_variable = 7

    if inner_variable == 7:
        def inner_func()
            inner_variable += 1  # Directly modifies parent variable
        inner_func()
    else
        def inner_func()  # Yup. Allowed.
            inner_variable += 2
        inner_func()
```