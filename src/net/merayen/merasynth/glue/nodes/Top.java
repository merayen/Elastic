package net.merayen.merasynth.glue.nodes;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import net.merayen.merasynth.glue.Context;

public class Top extends GlueObject {
	/*
	 * Contains all other GlueNodes.
	 */

	ArrayList<GlueNode> nodes = new ArrayList<GlueNode>();
	
	public Top(Context context) {
		super(context);
	}
	
	public void addObject(GlueNode object) {
		nodes.add(object);
	}
	
	protected void onRestore(JSONObject state) {
		assert nodes.size() == 0;
		
		JSONArray a = (JSONArray)state.get("nodes");
		for(int i = 0; i < a.size(); i++) {
			JSONObject obj = (JSONObject)a.get(i);
			
			GlueNode node;
			
			try {
				node = (GlueNode)(Class.forName((String)obj.get("class")).getConstructor(Context.class).newInstance(context));
			} catch (
				InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException | NoSuchMethodException | SecurityException e
			) {
				e.printStackTrace();
				throw new RuntimeException(String.format("Error instantiating class when restoring: %s", (String)obj.get("class")));
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
				throw new RuntimeException(String.format("Could not find class: %s", (String)obj.get("class")));
			}
			
			node.restore(obj);
			
			nodes.add(node);
		}
	}
	
	protected void onDump(JSONObject state) {
		
		JSONArray result = new JSONArray();
		for(GlueNode c : nodes)
			result.add(c.dump());
		
		state.put("nodes", result);
	}
}
