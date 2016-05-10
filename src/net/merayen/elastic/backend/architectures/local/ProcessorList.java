package net.merayen.elastic.backend.architectures.local;

import java.util.ArrayList;
import java.util.List;

/**
 * Contains a list of several LocalProcessors that is active in this voice.
 */
public class ProcessorList {
	public final int voice_id;

	private List<LocalProcessor> processors = new ArrayList<>(); 

	ProcessorList() {
		
	}

	public void add(LocalProcessor localprocessor) {
		processors.add(localprocessor);
	}
}
