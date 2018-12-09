// OpenCL Kernel Function for element by element vector addition
kernel void VectorAdd(global const char* a, global float* c, int numElements) {

    // get index into global data array
    int iGID = get_global_id(0);

    // bound check, equivalent to the limit on a 'for' loop
    if (iGID >= numElements)  {
        return;
    }

    // add the vector elements
    //c[iGID] = 0;
    if(iGID == 0)
        c[iGID] = 1;
    else
        c[iGID] = c[iGID-1] * a[iGID];
}