package net.merayen.elastic.backend.architectures.opencl;

import net.merayen.elastic.backend.architectures.AbstractExecutor;
import net.merayen.elastic.backend.architectures.IArchitecture;
import net.merayen.elastic.system.intercom.backend.InitBackendMessage;

public class Info implements IArchitecture {
	public String getDescription() {return "Run the audio engine on your graphics card or similar. Not implemented";}
	public String getName() {return "OpenCL";}

	@Override
	public AbstractExecutor getExecutor(InitBackendMessage message) {
		return null;
	}
}
