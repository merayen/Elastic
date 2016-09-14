package net.merayen.elastic.backend.architectures.local;

import net.merayen.elastic.backend.architectures.AbstractExecutor;
import net.merayen.elastic.backend.architectures.IArchitecture;

public class Info implements IArchitecture {
	@Override public String    			getDescription() {return "Runs the processor locally inside the JVM";}
	@Override public String    			getName()        {return "Local";}
	@Override public AbstractExecutor	getExecutor()    {return new Executor();}
}
