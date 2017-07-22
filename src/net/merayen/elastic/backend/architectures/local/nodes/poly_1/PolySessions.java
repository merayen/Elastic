package net.merayen.elastic.backend.architectures.local.nodes.poly_1;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import net.merayen.elastic.backend.architectures.local.LocalProcessor;
import net.merayen.elastic.backend.architectures.local.lets.Outlet;

class PolySessions {
	static class Session {
		final int session_id;
		final short tangent;
		final Outlet outlet;
		boolean active = true;

		private Session(int session_id, short tangent, Outlet outlet) {
			this.session_id = session_id;
			this.tangent = tangent;
			this.outlet = outlet;
		}
	}

	private final List<Session> sessions = new ArrayList<>();

	void push(int session_id, short tangent, Outlet outlet) {
		sessions.add(new Session(session_id, tangent, outlet));
		System.out.println(sessions.size());
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

		System.out.println(sessions.size());
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

	List<Outlet> getOutlets() {
		List<Outlet> result = new ArrayList<>();

		for(Session session : sessions)
			result.add(session.outlet);

		return result;
	}

	List<Session> getSessions() {
		return java.util.Collections.unmodifiableList(sessions);
	}	
}
