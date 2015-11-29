package net.merayen.merasynth.client.output;

import java.util.HashMap;

import org.json.simple.JSONObject;

import net.merayen.merasynth.glue.Context;
import net.merayen.merasynth.glue.nodes.GlueNode;

public class Glue extends GlueNode {
	public Glue(Context context) {
		super(context);
	}

	@Override
	public String getFriendlyName() {
		return "Output";
	}

	@Override
	public String getDescription() {
		return "Outputs audio";
	}

	@Override
	public void onInit() {
		super.onInit();
		createPort("input");
	}

	protected void onDump(JSONObject state) {
		
	}

	protected void onRestore(JSONObject state) {

	}

	private net.merayen.merasynth.client.signalgenerator.UI ui() {
		return ((net.merayen.merasynth.client.signalgenerator.UI)this.getUINode());
	}

	// Functions called from UI
	public void testbuttonClicked() {
		((Net)getNetNode()).requestAudio();
	}

	public HashMap<String, Number> getStatistics() {
		return ((Net)getNetNode()).getStatistics();
	}
}
