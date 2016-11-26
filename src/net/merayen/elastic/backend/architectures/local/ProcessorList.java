package net.merayen.elastic.backend.architectures.local;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.HashSet;
import java.util.Iterator;

/**
 * Contains a list of several LocalProcessors that is active in a LocalNode.
 */
public class ProcessorList implements Iterable<LocalProcessor> {

	// chains -> sessions -> LocalProcessor()

	/**
	 * Key: chain_id
	 * Value: [session_id, ...]
	 */
	private final Map<Integer, Set<Integer>> chains = new HashMap<>();

	/**
	 * Key: session_id
	 * Value: [LocalProcessor(), ...]
	 */
	private final Map<Integer, List<LocalProcessor>> sessions = new HashMap<>();

	ProcessorList() {}

	public void add(LocalProcessor localprocessor) {
		if(!sessions.containsKey(localprocessor.session_id))
			sessions.put(localprocessor.session_id, new ArrayList<>());

		if(!chains.containsKey(localprocessor.chain_id))
			chains.put(localprocessor.chain_id, new HashSet<>());

		chains.get(localprocessor.chain_id).add(localprocessor.session_id);
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

	public Set<Integer> getChainSessions(int chain_id) {
		Set<Integer> session_ids = chains.get(chain_id);
		if(session_ids == null)
			return new HashSet<>(0);

		return new HashSet<>(session_ids);
	}

	public void removeSession(int session_id) {
		if(sessions.remove(session_id) == null)
			throw new RuntimeException("Session does not exist");

		for(Set<Integer> sessions : chains.values())
			sessions.remove(session_id);
	}

	public Iterator<LocalProcessor> iterator() { // Bad performance? Check getAllProcessors()
		return getAllProcessors().iterator(); 
	}

	void clear() {
		chains.clear();
		sessions.clear();
	}
}
