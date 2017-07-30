package net.merayen.elastic.uinodes.list.output_1;

import net.merayen.elastic.system.intercom.NodeDataMessage;
import net.merayen.elastic.system.intercom.NodeParameterMessage;
import net.merayen.elastic.ui.objects.node.UINode;
import net.merayen.elastic.ui.objects.node.UIPort;

public class UI extends UINode {

	private VU vu;

	public UI() {
		super();
		width = 120;
		height = 100;

		titlebar.title = "Speaker";
	}

	@Override
	protected void onInit() {
		super.onInit();

		vu = new VU();
		vu.translation.x = 10;
		vu.translation.y = 20;
		add(vu);
	}

	@Override
	protected void onUpdate() {
		super.onUpdate();

		height = 40 + vu.getHeight();
	}

	@Override
	protected void onCreatePort(UIPort port) {
		if(port.name.equals("input"))
			port.translation.y = 20;
	}

	@Override
	protected void onRemovePort(UIPort port) {}

	@Override
	protected void onMessage(NodeParameterMessage message) {}

	@Override
	protected void onData(NodeDataMessage message) {
		if(message.value.containsKey("vu") && vu != null) {
			vu.updateVU((float[])message.value.get("vu"));
		} else if(message.value.containsKey("offset") && vu != null)
			vu.updateOffset((float[])message.value.get("offset"));
	}

	@Override
	protected void onParameter(String key, Object value) {}
}
