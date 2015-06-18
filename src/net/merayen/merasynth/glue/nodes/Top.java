package net.merayen.merasynth.glue.nodes;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import net.merayen.merasynth.glue.Context;

public class Top extends GlueNode {
	/*
	 * Contains all other GlueNodes.
	 */

	ArrayList<GlueNode> children = new ArrayList<GlueNode>();
	
	public Top(Context context) {
		super(context);
	}
	
	public void addNode(GlueNode node) {
		children.add(node);
	}
	
	protected void onRestore(JSONObject state) {
		assert children.size() == 0;
		
		JSONArray a = (JSONArray)state.get("children");
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
		}
	}
	
	protected void onDump(JSONObject state) {
		
		JSONArray result = new JSONArray();
		for(GlueNode c : children)
			result.add(c.dump());
		
		state.put("children", result);
	}
}
