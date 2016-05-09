package net.merayen.elastic.backend.architectures.local;

import java.util.ArrayList;
import java.util.List;

/**
 * 
 * @author merayen
 *
 */
public class ProcessorList {
	public final long voice_id;

	private List<LocalProcessor> processors = new ArrayList<>(); 

	ProcessorList() {
		
	}

	public void add(LocalProcessor localprocessor) {
		processors.add(localprocessor);
	}
}
