package net.merayen.merasynth.netlist;

import java.util.ArrayList;
import org.json.simple.JSONObject;
import org.json.simple.JSONArray;

/*
 * TODO Remove support of dumpp/restore
 * It might be best to let the glue nodes restore everything.
 * Less complexity? 
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
		
		// TODO distribute data between nodes
		
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
		 * TODO
		 */
		JSONObject result = new JSONObject();
		
		JSONArray nodes = new JSONArray();
		for(Node n : this.nodes)
			nodes.add(n.dump());
		
		result.put("nodes", nodes);
		
		return result;
	}
	
	public void restore() {
		/*
		 * Restores the nodes and the netlist.
		 * TODO
		 */
	}
}
