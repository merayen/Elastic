package net.merayen.elastic.backend.architectures.local;

import net.merayen.elastic.backend.architectures.IArchitecture;
import net.merayen.elastic.backend.architectures.ICompiler;

public class Info implements IArchitecture {
	@Override public String    getDescription() {return "Runs the processor locally inside the JVM";}
	@Override public String    getName()        {return "Local";}
	@Override public ICompiler getCompiler()    {return new Compiler();}
}
