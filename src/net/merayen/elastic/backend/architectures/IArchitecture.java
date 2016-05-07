package net.merayen.elastic.backend.architectures;

import net.merayen.elastic.util.Postmaster;

public interface IArchitecture {
	public String getDescription();
	public String getName();
	public ICompiler getCompiler();
}
