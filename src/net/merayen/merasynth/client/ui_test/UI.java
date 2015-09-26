package net.merayen.merasynth.client.ui_test;

import net.merayen.merasynth.ui.objects.UIClip;
import net.merayen.merasynth.ui.objects.components.graph.Graph;
import net.merayen.merasynth.ui.objects.node.UINode;
import net.merayen.merasynth.ui.objects.node.UIPort;

public class UI extends UINode {
	private UIPort output_port;
	private ClipTest clip_test;

	@Override
	public void onInit() {
		super.onInit();

		width = 24f;
		height = 24f;

		titlebar.title = "UI Test";

		clip_test = new ClipTest();
		clip_test.translation.x = 2;
		clip_test.translation.y = 2;
		add(clip_test);
	}

	@Override
	protected void onDraw() {
		super.onDraw();
	}

	@Override
	public void onCreatePort(String name) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onRemovePort(String name) {
		// TODO Auto-generated method stub
		
	}
}
