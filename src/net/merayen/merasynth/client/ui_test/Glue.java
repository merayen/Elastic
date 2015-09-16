package net.merayen.merasynth.client.ui_test;

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
		return "net.merayen.merasynth.client.ui_test";
	}

	@Override
	public String getFriendlyName() {
		return "UI Test";
	}

	@Override
	public String getDescription() {
		return "For developer";
	}

	@Override
	public void onInit() {
		super.onInit();
		
	}

	private net.merayen.merasynth.client.ui_test.UI ui() {
		return ((net.merayen.merasynth.client.ui_test.UI)this.getUINode());
	}
}
