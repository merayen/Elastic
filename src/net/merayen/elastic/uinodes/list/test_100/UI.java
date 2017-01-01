package net.merayen.elastic.uinodes.list.test_100;

import net.merayen.elastic.system.intercom.NodeDataMessage;
import net.merayen.elastic.system.intercom.NodeParameterMessage;
import net.merayen.elastic.ui.objects.components.InputSignalParameters;
import net.merayen.elastic.ui.objects.components.curvebox.SignalBezierCurveBox;
import net.merayen.elastic.ui.objects.node.UINode;
import net.merayen.elastic.ui.objects.node.UIPort;

public class UI extends UINode {
	public UI() {
		super();
		width = 200;
		height = 200;
	}

	@Override
	protected void onCreatePort(UIPort port) {
		if(port.name.equals("input")) {
			port.translation.y = 20;
		}
	}

	@Override
	protected void onInit() {
		super.onInit();
		SignalBezierCurveBox bwb = new SignalBezierCurveBox();
		bwb.translation.x = 20;
		bwb.translation.y = 30;
		add(bwb);
		bwb.insertPoint(1);

		InputSignalParameters isa = new InputSignalParameters(this, "test");
		isa.translation.x = 10;
		isa.translation.y = 180;
		add(isa);
	}

	@Override
	protected void onRemovePort(UIPort port) {}

	@Override
	protected void onMessage(NodeParameterMessage message) {}

	@Override
	protected void onData(NodeDataMessage message) {}
}
