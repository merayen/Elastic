package net.merayen.merasynth.glue.client;

import org.json.simple.JSONObject;

import net.merayen.merasynth.glue.Context;
import net.merayen.merasynth.glue.nodes.GlueNode;

public class PulseGenerator extends GlueNode {

	public PulseGenerator(Context context) {
		super(context);
	}

	public void onCreate() {
		
	}
	
	protected void onDump(JSONObject state) {
		state.put("frequency", 440.0f);
	}
	
	protected void onRestore(JSONObject state) {
		System.out.printf("PulseGenerator is getting restored! My freq: %f\n", ((Double)state.get("frequency")).floatValue());
		
	}
}
