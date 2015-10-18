package net.merayen.merasynth.client.ui_test;

import net.merayen.merasynth.ui.objects.node.UINode;

public class UI extends UINode {
	private ClipTest clip_test;

	@Override
	public void onInit() {
		super.onInit();

		width = 240f;
		height = 240f;

		titlebar.title = "UI Test";

		clip_test = new ClipTest();
		clip_test.translation.x = 20;
		clip_test.translation.y = 20;
		add(clip_test);
	}

	@Override
	protected void onDraw() {
		translation.scale_x = translation.scale_y = (float)Math.sin(System.currentTimeMillis()/1000.0) / .2f + 7f;
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
