package net.merayen.merasynth.netlist;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;

import org.json.simple.JSONObject;
import org.json.simple.JSONArray;

/*
 * TODO Remove support of dump/restore?
 * It might be best to let the glue nodes restore everything.
 * Less complexity? Hmm... 
 */

public class Supervisor {
	/*
	 * Supervisor containing the whole net.
	 */
	
	ArrayList<Node> nodes = new ArrayList<Node>();
	
	public void addNode(Node node) {
		nodes.add(node);
	}
	
	public void update(double timeout) {
		/*
		 * Oppdaterer hele netlisten til den er ferdigprosessert,
		 * eller via timeout.
		 */
		
		// TODO distribute data between nodes. Already done ??
		
		for(Node n : getNodesNeedingUpdate())
			n.doUpdate();
	}
	
	private ArrayList<Node> getNodesNeedingUpdate() {
		ArrayList<Node> result = new ArrayList<>();
		for(Node n : nodes)
			if(n.needsUpdate())
				result.add(n);
		
		return result;
	}
	
	public boolean needsUpdate() {
		return getNodesNeedingUpdate().size() > 0;
	}
	
	public void connect(Port a, Port b) {
		/*
		 * Hjelpefunksjon.
		 * Kobler til to porter sammen.
		 */
		assert a != b : "Port kan ikke kobles til seg selv";
		Line line = new Line(this, a, b);
	}
	
	public JSONObject dump() {
		/*
		 * Dumps the nodes and the netlist.
		 * TODO Dump the lines the port are connected to, too!
		 */
		JSONObject result = new JSONObject();
		
		JSONArray nodes = new JSONArray();
		for(Node n : this.nodes)
			nodes.add(n.dump());
		
		result.put("nodes", nodes);
		
		return result;
	}
	
	public void restore(JSONObject obj) {
		/* 
		 * Restore the lines. If the other node doesn't exist yet,
		 * the other node will successfully restore the connection?
		 */
		JSONArray nodes = (JSONArray)obj.get("nodes");
		
		for(int i = 0; i < nodes.size(); i++ ) {
			JSONObject x = (JSONObject)nodes.get(i);
			String class_name = (String)x.get("class");
			Node node;
			
			try {
				node = (Node)Class.forName(class_name).getConstructor(Supervisor.class).newInstance(this);
			} catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException | NoSuchMethodException | SecurityException e) {
				e.printStackTrace();
				throw new RuntimeException("Could not instantiate class: " + e.toString());
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
				throw new RuntimeException("Could not find class: " + class_name);
			}
			
			node.restore((JSONObject)x);
			
			this.nodes.add(node);
		}
	}
}
