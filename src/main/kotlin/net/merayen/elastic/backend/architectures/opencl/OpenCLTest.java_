package net.merayen.elastic.backend.architectures.opencl;

import com.jogamp.opencl.CLBuffer;
import com.jogamp.opencl.CLCommandQueue;
import com.jogamp.opencl.CLContext;
import com.jogamp.opencl.CLDevice;
import com.jogamp.opencl.CLKernel;
import com.jogamp.opencl.CLProgram;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;

import static java.lang.System.*;
import static com.jogamp.opencl.CLMemory.Mem.*;
import static java.lang.Math.*;

/**
 * Hello Java OpenCL example. Adds all elements of buffer A to buffer B
 * and stores the result in buffer C.<br/>
 * Sample was inspired by the Nvidia VectorAdd example written in C/C++
 * which is bundled in the Nvidia OpenCL SDK.
 *
 * @author Michael Bien
 */
public class OpenCLTest {

	public static void main(String[] args) throws IOException {

		// set up (uses default CLPlatform and creates context for all devices)
		CLContext context = CLContext.create();
		out.println("created " + context);

		// always make sure to release the context under all circumstances
		// not needed for this particular sample but recommented
		try {

			// select fastest device
			CLDevice device = context.getMaxFlopsDevice();
			for(CLDevice d : context.getDevices())
				out.println(d);

			out.println("using " + device);

			// create command queue on device.
			CLCommandQueue queue = device.createCommandQueue();

			int elementCount = 2;//1444477;                                  // Length of arrays to process
			int localWorkSize = min(device.getMaxWorkGroupSize(), 256);  // Local work size dimensions
			int globalWorkSize = roundUp(localWorkSize, elementCount);   // rounded up to the nearest multiple of the localWorkSize

			// load sources, create and build program
			CLProgram program = context.createProgram(OpenCLTest.class.getResourceAsStream("Test.cl")).build();

			/*out.println("used device memory: "
					+ (clBufferA.getCLSize() + clBufferB.getCLSize() + clBufferC.getCLSize()) / 1000000 + "MB");*/

			CLKernel kernel = program.createCLKernel("VectorAdd");

			// A, B are input buffers, C is for the result
			CLBuffer<ByteBuffer> clBufferA = context.createByteBuffer(globalWorkSize, READ_ONLY);
			CLBuffer<FloatBuffer> clBufferC = context.createFloatBuffer(globalWorkSize, WRITE_ONLY);
			kernel.putArgs(clBufferA, clBufferC).putArg(elementCount);

			// asynchronous write of data to GPU device,
			// followed by blocking read to get the computed results back.
			long time = nanoTime();
			long c = 0;
			for (int j = 0; j < 10; j++) {
				long lol = nanoTime();
				// fill input buffers with random numbers
				// (just to have test data; seed is fixed -> results will not change between runs).
				c += fillBuffer(clBufferA.getBuffer());
				time += (nanoTime() - lol);

				// get a reference to the kernel function with the name 'VectorAdd'
				// and map the buffers to its input parameters.
				queue.putWriteBuffer(clBufferA, false)
						.put1DRangeKernel(kernel, 0, globalWorkSize, localWorkSize)
						.putReadBuffer(clBufferC, true);

				/*try {
					Thread.sleep(0);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}*/
				//clBufferC.getBuffer().get();

				// print first few elements of the resulting buffer to the console.
				out.println("a+b=c results snapshot: ");
				for (int i = 0; i < 10; i++)
					out.print(clBufferC.getBuffer().get() + ", ");
				out.println("...; " + clBufferC.getBuffer().remaining() + " more");
				clBufferC.getBuffer().rewind();
			}
			clBufferA.release();
			clBufferC.release();
			//clBufferC.release();
			out.println("computation took: " + ((nanoTime() - time) / 1000000) + "ms");
			out.println("floats processed: " + c);
		} finally {
			// cleanup all resources associated with this context.
			context.release();
		}
	}

	private static int fillBuffer(ByteBuffer buffer) {
		int i = 0;
		while (buffer.remaining() != 0)
			buffer.put((byte)i++);
		buffer.rewind();
		return i;
	}

	private static int roundUp(int groupSize, int globalSize) {
		int r = globalSize % groupSize;
		if (r == 0)
			return globalSize;
		else
			return globalSize + groupSize - r;
	}
}