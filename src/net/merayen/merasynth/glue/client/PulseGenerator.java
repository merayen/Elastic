package net.merayen.merasynth.glue.client;

import org.json.simple.JSONObject;

import net.merayen.merasynth.glue.Context;
import net.merayen.merasynth.glue.nodes.GlueNode;

public class PulseGenerator extends GlueNode {

	public PulseGenerator(Context context) {
		super(context);
	}

	@Override
	public String getUINodePath() {
		return "net.merayen.merasynth.ui.objects.client.PulseGenerator";
	}

	@Override
	public String getNetNodePath() {
		return "net.merayen.merasynth.netlist.nodes.PulseGenerator";
	}

	@Override
	public String getFriendlyName() {
		return "Pulse Generator 1.0";
	}

	@Override
	public String getDescription() {
		return "Simple wave generator with several different wave types.";
	}

	@Override
	public void onInit() {
		System.out.println("(1) Pulse generator initializing");
	}

	protected void onDump(JSONObject state) {
		state.put("frequency", 440.0f);
	}

	protected void onRestore(JSONObject state) {
		System.out.printf("PulseGenerator is getting restored! My freq: %f\n", ((Double)state.get("frequency")).floatValue());
	}
}
