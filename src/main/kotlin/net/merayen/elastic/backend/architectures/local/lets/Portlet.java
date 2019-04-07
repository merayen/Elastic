package net.merayen.elastic.backend.architectures.local.lets;

public abstract class Portlet {
	public abstract void reset(int sample_offset);
	public abstract boolean satisfied();
}
