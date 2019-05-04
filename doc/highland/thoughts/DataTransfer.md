# Data transfering
This document tries to figure out how to transfer data from Java-space to OpenCL, Java ASM, and native x86 space.

## Questions
* Do we want to send all data is a one, huge chunk from Java to OpenCL?
    * Should have just 1 kernel?
    * Is the OpenCL compiler smart enough to understand to parallelize that huge kernel?
        * Or can the kernel call another kernels?
* One node instance (one single signal_generator_1 for example) should probably be 1 kernel / module in OpenCL-space
    * Each node should generate Highlang code based on its configuration
    

## JVM-space to Highlang
```
class SignalGenerator_1 : HighLangNode {

    fun compile(): HighLangCode {
        val code = """
            def process(ports, frequency_port, output_port)
                
        """
    }
//byte[] data = new data[120];
//bridge.process(data)
```

### Generated OpenCL-code (somewhat)
```
struct DataAB34DF_port_input {
    struct channels {
    
    } channels;
};

struct DataAB34DF_port_output {
    struct channels {
    
    } channels;
};

struct DataAB34DF {
    
};

void SignalGenerator_1_AB34DF(__global struct Data_AB34DF data) {
    // ...process here
}
```