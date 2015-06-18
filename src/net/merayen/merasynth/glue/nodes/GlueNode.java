package net.merayen.merasynth.glue.nodes;

import net.merayen.merasynth.glue.Context;
import net.merayen.merasynth.netlist.Supervisor;

import org.json.simple.JSONObject;

public abstract class GlueNode {
	/*
	 * Glue nodes that contains a netlist Node and an UI Node. These nodes are
	 * responsible for controlling both types and glue them together. Currently
	 * we want those two different node systems (UI and netlist) completely
	 * separated.
	 */
	
	protected Context context;
	
	private int id = java.util.UUID.randomUUID().hashCode();
	
	protected net.merayen.merasynth.netlist.Node net_node;
	protected net.merayen.merasynth.ui.objects.node.Node ui_node;
	
	public GlueNode(
		Context context/*,
		Class<? extends net.merayen.merasynth.netlist.Node> net_node,
		Class<? extends net.merayen.merasynth.ui.objects.node.Node> ui_node*/
	) {
		try {
			/*this.net_node = net_node.getClass().getConstructor(Supervisor.class).newInstance(context.supervisor);
			this.ui_node = ui_node.getClass().getConstructor().newInstance();*/
		} catch (Exception e) {
			throw new RuntimeException("Failed creating GlueNode: " + e.toString());
		}
		this.context = context;
		onCreate();
	}
	
	public void doInit() {
		onInit();
	}
	
	protected void onCreate() {
		/*
		 * Called when created for the first time
		 */
	}
	
	protected void onInit() {
		/*
		 * Called when initialized (after onCreate or before onRestore)
		 */
	}
	
	protected void onRestore(JSONObject state) {
		/*
		 * Called when restored from JSON
		 */
	}
	
	protected void onDump(JSONObject state) { // TODO return JSON object. Really?
		/*
		 * Called when dumping to JSON
		 */
	}
	
	public void restore(JSONObject state) {
		this.id = ((Long)state.get("id")).intValue();
		onRestore(state);
	}
	
	public JSONObject dump() {
		JSONObject state = new JSONObject();
		state.put("id", id);
		state.put("class", this.getClass().getName());
		
		onDump(state);
		
		return state;
	}
}
