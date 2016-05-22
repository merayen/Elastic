package net.merayen.elastic.client.voices;

import org.json.simple.JSONObject;

import net.merayen.elastic.glue.Context;
import net.merayen.elastic.glue.nodes.GlueNode;

public class Glue extends GlueNode {
	private int voices = 1;

	public Glue(Context context) {
		super(context);
	}

	@Override
	public String getFriendlyName() {
		return "Voices";
	}

	@Override
	public String getDescription() {
		return "Makes several instances of the right nodes, forming voices.";
	}

	@Override
	public void onInit() {
		super.onInit();
		createPort("input");
		createPort("output");
	}

	protected void onDump(JSONObject state) {

	}

	protected void onRestore(JSONObject state) {

	}

	public void changeVoiceCount(int voices) {
		this.voices = voices;
		((Node)this.getNetNode()).changeVoiceCount(voices);
	}

	private net.merayen.elastic.client.voices.UI ui() {
		return ((net.merayen.elastic.client.voices.UI)this.getUINode());
	}
}
