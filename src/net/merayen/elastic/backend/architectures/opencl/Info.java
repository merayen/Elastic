package net.merayen.elastic.backend.architectures.opencl;

import net.merayen.elastic.backend.architectures.IArchitecture;

public class Info implements IArchitecture {
	public String getDescription() {return "Run processor on your graphics card or similar. Not implemented";}
	public String getName() {return "OpenCL";}
}
