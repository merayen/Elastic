package net.merayen.elastic.backend.architectures.local;

import org.jetbrains.annotations.NotNull;

import java.util.*;

/**
 * Contains a list of several LocalProcessors that is active in a LocalNode.
 */
public class ProcessorList implements Iterable<LocalProcessor> {
	/**
	 * Key: session_id
	 * Value: [LocalProcessor(), ...]
	 */
	private final List<LocalProcessor> processors = new ArrayList<>();

	ProcessorList() {
	}

	void add(LocalProcessor localprocessor) {
		processors.add(localprocessor);
	}

	public List<LocalProcessor> getAllProcessors() {
		return Collections.unmodifiableList(processors);
	}

	/**
	 * Get a processor for a certain session on a certain LocalNode.
	 */
	public LocalProcessor getProcessor(LocalNode localnode, int session_id) {
		for (LocalProcessor lp : processors) {
			if (lp.localnode == localnode && lp.session_id == session_id)
				return lp;
		}

		throw new RuntimeException("LocalNode does not have a LocalProcessor for this session");
	}

	/**
	 * Retrieves all processors living in a session.
	 */
	public List<LocalProcessor> getProcessors(int session_id) {
		List<LocalProcessor> result = new ArrayList<>();

		for (LocalProcessor lp : processors)
			if (lp.session_id == session_id)
				result.add(lp);

		return result;
	}

	public Set<Integer> getSessionIds() {
		Set<Integer> result = new HashSet<>();

		for (LocalProcessor lp : processors)
			result.add(lp.session_id);

		return result;
	}

	public Set<Integer> getSessions(LocalNode localnode) {
		Set<Integer> result = new HashSet<>();

		for (LocalProcessor lp : processors)
			if (lp.localnode == localnode)
				result.add(lp.session_id);

		return result;
	}

	void removeSession(int session_id) {
		processors.removeIf(x -> x.session_id == session_id);
	}

	@Override
	@NotNull
	public Iterator<LocalProcessor> iterator() { // Bad performance? Check getAllProcessors()
		return getAllProcessors().iterator();
	}

	void clear() {
		processors.clear();
	}
}
