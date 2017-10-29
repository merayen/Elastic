package net.merayen.elastic.uinodes.list.midi_spread_1;

import net.merayen.elastic.system.intercom.NodeDataMessage;
import net.merayen.elastic.system.intercom.NodeParameterMessage;
import net.merayen.elastic.ui.objects.components.ParameterSlider;
import net.merayen.elastic.ui.objects.node.UINode;
import net.merayen.elastic.ui.objects.node.UIPort;

public class UI extends UINode {
	private final ParameterSlider spread_width;

	public UI() {
		UI self = this;
		spread_width = new ParameterSlider();
		spread_width.translation.x = 10;
		spread_width.translation.y = 20;
		spread_width.setHandler(new ParameterSlider.IHandler() {
			@Override
			public String onLabelUpdate(double value) {
				return String.format("%.2f", value);
			}

			@Override
			public void onChange(double value, boolean programatic) {
				self.sendParameter("layoutWidth", (float)value);
			}

			@Override
			public void onButton(int offset) {
				spread_width.setValue(spread_width.getValue() + offset / 50.0);
			}
		});

		add(spread_width);
	}

	@Override
	protected void onInit() {
		super.onInit();
		width = 100;
		height = 50;
	}

	@Override
	protected void onCreatePort(UIPort port) {
		if(port.name.equals("input")) {
			port.translation.y = 20;
		}

		if(port.name.equals("output")) {
			port.translation.x = 100;
			port.translation.y = 20;
		}
	}

	@Override
	protected void onRemovePort(UIPort port) {}

	@Override
	protected void onMessage(NodeParameterMessage message) {}

	@Override
	protected void onData(NodeDataMessage message) {}

	@Override
	protected void onParameter(String key, Object value) {
		if(key.equals("layoutWidth"))
			spread_width.setValue(((Number)value).floatValue());
	}
}
