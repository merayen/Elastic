package net.merayen.elastic.backend.architectures.local.nodes.poly_1;

import net.merayen.elastic.backend.architectures.local.lets.Outlet;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

class PolySessions {
	static class Session {
		final int id;
		final short tangent;
		final Outlet input;
		final OutputInterfaceNode[] outnodes;
		boolean active = true;

		private Session(int session_id, short tangent, Outlet input, OutputInterfaceNode[] outnodes) {
			this.id = session_id;
			this.tangent = tangent;
			this.input = input;
			this.outnodes = outnodes;
		}
	}

	private final List<Session> sessions = new ArrayList<>();

	void push(int session_id, short tangent, Outlet input, OutputInterfaceNode[] outnodes) {
		sessions.add(new Session(session_id, tangent, input, outnodes));
	}

	/**
	 * Release a tangent.
	 * Removes the sessions and returns the sessions that must be killed.
	 */
	List<Session> release(short tangent) {
		List<Session> result = new ArrayList<>();

		Iterator<Session> iterator = sessions.iterator();
		while(iterator.hasNext()) {
			Session session = iterator.next();
			if(session.tangent == tangent) {
				iterator.remove();
				result.add(session);
			}
		}

		return result;
	}

	void removeSession(Session session) {
		if(!sessions.remove(session))
			throw new RuntimeException("Session not found");
	}

	/**
	 * Retrieves all the session_ids for the current tangent.
	 */
	List<Session> getTangentSessions(short tangent) {
		List<Session> result = new ArrayList<>();

		for(Session session : sessions)
			if(session.tangent == tangent)
				result.add(session);

		return result;
	}

	boolean isTangentDown(short tangent) {
		for(Session session : sessions)
			if(session.tangent == tangent)
				return true;

		return false;
	}

	List<Outlet> getInputOutlets() {
		List<Outlet> result = new ArrayList<>();

		for(Session session : sessions)
			result.add(session.input);

		return result;
	}

	List<Session> getSessions() {
		return java.util.Collections.unmodifiableList(sessions);
	}

	boolean isEmpty() {
		return sessions.isEmpty();
	}
}
