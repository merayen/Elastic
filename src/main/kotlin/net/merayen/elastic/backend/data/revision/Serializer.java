package net.merayen.elastic.backend.data.revision;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

public class Serializer {
	private Serializer() {}

	public static RevisionTree load(JSONObject data) {
		RevisionTree rt = new RevisionTree();

		JSONArray list = (JSONArray)data.get("list");

		for(Object x : list) {
			JSONObject rev = (JSONObject)x;
			Revision revision = new Revision((String)rev.get("id"));
			rt.list.put((String)rev.get("id"), revision);
		}

		for(Object x : list) {
			JSONObject rev = (JSONObject)x;
			if(rev.containsKey("parent"))
				rt.list.get((String)rev.get("id")).parent = rt.list.get(rev.get("parent"));
		}

		rt.current = rt.list.get(data.get("current"));

		return rt;
	}

	public static JSONObject dump(RevisionTree rt) {
		JSONObject r = new JSONObject();

		JSONArray list = new JSONArray();
		r.put("list", list);

		r.put("current", rt.current != null ? rt.current.id: null);

		for(Revision x : rt.list.values()) {
			JSONObject rev = new JSONObject();
			rev.put("id", x.id);
			if(x.parent != null)
				rev.put("parent", x.parent.id);

			list.add(rev);
		}

		return r;
	}
}
