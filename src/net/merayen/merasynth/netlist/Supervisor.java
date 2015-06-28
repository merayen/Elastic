package net.merayen.merasynth.netlist;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashSet;

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
	
	public Node getNodeByID(String id) {
		for(Node node : nodes)
			if(node.getID().equals(id))
				return node;
		
		return null;
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
		
		// Dump of nodes
		JSONArray nodes = new JSONArray();
		for(Node n : this.nodes)
			nodes.add(n.dump());
		
		
		// Dump of net
		HashSet lines_dumped = new HashSet<String>(); // To not store all lines twice
		JSONArray lines = new JSONArray();
		for(Node node : this.nodes)
			for(Port port : node.getPorts())
				for(Line line : port.getLines())
					if(!lines_dumped.contains(line.getID())) {
						lines.add(line.dump());
						lines_dumped.add(line.getID());
					}
		
		result.put("lines", lines);	
		result.put("nodes", nodes);
		
		return result;
	}
	
	public void restore(JSONObject obj) {
		/* 
		 * Restore the lines. If the other node doesn't exist yet,
		 * the other node will successfully restore the connection?
		 */
		JSONArray nodes = (JSONArray)obj.get("nodes");
		JSONArray lines = (JSONArray)obj.get("lines");
		
		// Creation of nodes and their ports
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
		
		// Connecting the ports with lines
		for(int i = 0; i < lines.size(); i++ ) {
			JSONObject line = (JSONObject)lines.get(i);
			JSONObject port_a = (JSONObject)line.get("port_a");
			JSONObject port_b = (JSONObject)line.get("port_b");
			
			Node node_a = getNodeByID((String)port_a.get("node"));
			Node node_b = getNodeByID((String)port_b.get("node"));
			assert node_a != null;
			assert node_b != null;
			
			Port node_port_a = node_a.getPort((String)port_a.get("name"));
			Port node_port_b = node_b.getPort((String)port_b.get("name"));
			
			//Line line = new Line(this, , null);
			
			//connect();
		}
	}
}
