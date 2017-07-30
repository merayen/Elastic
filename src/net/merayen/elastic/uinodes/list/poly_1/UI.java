package net.merayen.elastic.uinodes.list.poly_1;

import net.merayen.elastic.system.intercom.NodeDataMessage;
import net.merayen.elastic.system.intercom.NodeParameterMessage;
import net.merayen.elastic.ui.objects.components.Button;
import net.merayen.elastic.ui.objects.components.ParameterSlider;
import net.merayen.elastic.ui.objects.node.UINode;
import net.merayen.elastic.ui.objects.node.UIPort;
import net.merayen.elastic.ui.objects.top.views.nodeview.NodeView;

public class UI extends UINode {
	private Button button;
	private ParameterSlider unison;

	public UI() {
		width = 100;
		height = 100;

		UI self = this;

		button = new Button();
		button.label = "Open";
		button.translation.x = 10;
		button.translation.y = 20;
		button.setHandler(new Button.IHandler() {
			@Override
			public void onClick() {
				self.search.parentByType(NodeView.class).swapView(node_id);
			}
		});
		add(button);

		unison = new ParameterSlider();
		unison.translation.x = 5;
		unison.translation.y = 40;
		unison.setHandler(new ParameterSlider.IHandler() {
			@Override
			public String onLabelUpdate(double value) {
				return String.format("%d", Math.round(value * 31) + 1);
			}

			@Override
			public void onChange(double value, boolean programatic) {
				self.sendParameter("unison", Math.round(value * 31) + 1);
			}
			
			@Override
			public void onButton(int offset) {
				unison.setValue(unison.getValue() + offset / 31.0);
			}
		});
		add(unison);
	}

	@Override
	protected void onDraw() {
		super.onDraw();

		/*draw.setStroke(1);
		draw.setColor(0, 0, 0);
		draw.rect(15, 20, 50, 50);
		draw.rect(35, 40, 50, 50);*/
	}

	@Override
	protected void onCreatePort(UIPort port) {
		/*if(port.name.startsWith("input_")) {
			if(port.output)
				throw new RuntimeException("Port should have been an input");
		}

		if(port.name.startsWith("output_")) {
			if(!port.output)
				throw new RuntimeException("Port should have been an output");
		}

		rearrangePorts();

		System.out.println("UI Poly, creating port: " + port.name);*/

		if(port.name.equals("input")) {
			port.translation.y = 20;
		}
	}

	/*private void rearrangePorts() {
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
	}*/

	@Override
	protected void onRemovePort(UIPort port) {
		/*System.out.println("UI poly, deleting port: " + port.name + ". Got " + this.getPorts().size() + " ports");
		rearrangePorts();*/
	}

	@Override
	protected void onMessage(NodeParameterMessage message) {
		if(message instanceof NodeParameterMessage) {
			NodeParameterMessage m = (NodeParameterMessage)message;
			if(m.key.equals("unison"))
				unison.setValue((((Number)m.value).intValue() - 1) / 31.0);
		}
	}

	@Override
	protected void onData(NodeDataMessage message) {}

	@Override
	protected void onParameter(String key, Object value) {}
}
