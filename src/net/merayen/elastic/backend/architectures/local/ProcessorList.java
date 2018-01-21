package net.merayen.elastic.backend.architectures.local;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.Iterator;

/**
 * Contains a list of several LocalProcessors that is active in a LocalNode.
 */
public class ProcessorList implements Iterable<LocalProcessor> {
	/**
	 * Key: session_id
	 * Value: [LocalProcessor(), ...]
	 */
	private final Map<Integer, List<LocalProcessor>> sessions = new HashMap<>();

	ProcessorList() {}

	void addSession(int session_id) {
		if(!sessions.containsKey(session_id))
			sessions.put(session_id, new ArrayList<>());
	}

	void add(LocalProcessor localprocessor) {
		sessions.get(localprocessor.session_id).add(localprocessor);
	}

	public List<LocalProcessor> getAllProcessors() {
		List<LocalProcessor> result = new ArrayList<>();

		for(List<LocalProcessor> list : sessions.values())
			result.addAll(list);

		return result;
	}

	/**
	 * Get a processor for a certain session on a certain LocalNode.
	 */
	public LocalProcessor getProcessor(LocalNode localnode, int session_id) {
		List<LocalProcessor> processors = sessions.get(session_id);

		if(processors == null)
			throw new RuntimeException("No such session");

		for(LocalProcessor lp : processors)
			if(lp.localnode == localnode)
				return lp;

		throw new RuntimeException("LocalNode does not have a LocalProcessor for this session");
	}

	/**
	 * Retrieves all processors living in a session.
	 */
	public List<LocalProcessor> getProcessors(int session_id) {
		List<LocalProcessor> result = sessions.get(session_id);
		if(result == null)
			throw new RuntimeException("No such session");

		return result;
	}

	public Set<Integer> getSessions() {
		return sessions.keySet();
	}

	public List<Integer> getSessions(LocalNode localnode) {
		List<Integer> result = new ArrayList<>();

		for(Entry<Integer, List<LocalProcessor>> e : sessions.entrySet()) {
			for(LocalProcessor lp : e.getValue()) {
				if(lp.localnode == localnode) {
					result.add(e.getKey());
					break;
				}
			}
		}

		return result;
	}

	void removeSession(int session_id) {
		if(sessions.remove(session_id) == null)
			throw new RuntimeException("Session does not exist");
	}

	public Iterator<LocalProcessor> iterator() { // Bad performance? Check getAllProcessors()
		return getAllProcessors().iterator(); 
	}

	void clear() {
		sessions.clear();
	}
}
