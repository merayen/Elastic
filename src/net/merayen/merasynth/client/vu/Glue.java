package net.merayen.merasynth.client.vu;

import org.json.simple.JSONObject;

import net.merayen.merasynth.glue.Context;
import net.merayen.merasynth.glue.nodes.GlueNode;

public class Glue extends GlueNode {
	private float frequency;

	public Glue(Context context) {
		super(context);
	}

	@Override
	public String getClassPath() { // XXX Da wut, don't we already know this?
		return "net.merayen.merasynth.client.vu";
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

	private net.merayen.merasynth.client.vu.UI ui() {
		return ((net.merayen.merasynth.client.vu.UI)this.getUINode());
	}

	public float[] getChannelLevels() {
		return ((Net)getNetNode()).getChannelLevels(); // Thread safe...?
	}
}
