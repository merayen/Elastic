package net.merayen.merasynth.glue.nodes;

import net.merayen.merasynth.glue.Context;
import net.merayen.merasynth.netlist.Supervisor;

import org.json.simple.JSONObject;

public abstract class GlueObject {
	/*
	 * Glue nodes that contains a netlist Node and an UI Node. These nodes are
	 * responsible for controlling both types and glue them together. Currently
	 * we want those two different node systems (UI and netlist) completely
	 * separated.
	 */
	
	protected Context context;
	
	private int id = java.util.UUID.randomUUID().hashCode();
	
	public GlueObject(Context context) {
		this.context = context;
		onCreate();
	}
	
	public void doInit() {
		onInit();
	}
	
	protected void onCreate() {
		/*
		 * Called when created for the first time. Nope
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
	
	public void restore(JSONObject dump) {
		this.id = ((Long)dump.get("id")).intValue();
		onRestore((JSONObject)dump.get("state"));
	}
	
	public JSONObject dump() {
		JSONObject result = new JSONObject();
		result.put("id", id);
		result.put("class", this.getClass().getName());
		result.put("state", new JSONObject());
		
		
		onDump((JSONObject)result.get("state"));
		
		return result;
	}
}
