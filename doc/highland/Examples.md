# Examples

## Data structures


## Usage in nodes
### As a simple processor
```
import math

# This actually allocates memory here.
var ports = struct
    var in_port = struct
        var audio[2][512]: fp32
    var out_port = struct
        var audio[2][512]: fp32

# How do we insert data into a struct from Java-space?

def process_in_port(ports: port_struct)
    for f in ports.in_port
    #### UI allows user to edit below ####
    return 
    #### UI ends here ####
```

### As a more complex processor
```

```