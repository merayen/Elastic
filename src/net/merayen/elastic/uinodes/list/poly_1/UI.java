package net.merayen.elastic.uinodes.list.poly_1;

import java.util.List;

import net.merayen.elastic.system.intercom.NodeDataMessage;
import net.merayen.elastic.system.intercom.NodeParameterMessage;
import net.merayen.elastic.ui.objects.node.UINode;
import net.merayen.elastic.ui.objects.node.UIPort;

public class UI extends UINode {
	@Override
	protected void onInit() {
		super.onInit();
		width = 100;
		height = 200;
	}

	@Override
	protected void onCreatePort(UIPort port) {
		if(port.name.startsWith("input_")) {
			if(port.output)
				throw new RuntimeException("Port should have been an input");
		}

		if(port.name.startsWith("output_")) {
			if(!port.output)
				throw new RuntimeException("Port should have been an output");
		}

		rearrangePorts();

		System.out.println("UI Poly, creating port: " + port.name);
	}

	private void rearrangePorts() {
		List<UIPort> ports = getPorts();
		ports.sort((a,b) -> a.name.compareTo(b.name));

		UIPort[] outputs = ports.stream()
			.filter(x -> x.output)
			.sorted((a,b) -> Integer.parseInt(a.name.split("_")[1]) - Integer.parseInt(b.name.split("_")[1]))
			.toArray(size -> new UIPort[size]);

		UIPort[] inputs = ports.stream()
				.filter(x -> !x.output)
				.sorted((a,b) -> Integer.parseInt(a.name.split("_")[1]) - Integer.parseInt(b.name.split("_")[1]))
				.toArray(size -> new UIPort[size]);

		int i = 0;
		for(UIPort port : inputs) {
			port.translation.y = i += 20;
		}

		i = 0;
		for(UIPort port : outputs) {
			port.translation.x = 100;
			port.translation.y = i+= 20;
		}
	}

	@Override
	protected void onRemovePort(UIPort port) {
		System.out.println("UI poly, deleting port: " + port.name + ". Got " + this.getPorts().size() + " ports");
		rearrangePorts();
	}

	@Override
	protected void onMessage(NodeParameterMessage message) {}

	@Override
	protected void onData(NodeDataMessage message) {}
}
