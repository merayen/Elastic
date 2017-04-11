package net.merayen.elastic.uinodes.list.midi_in_1;

import net.merayen.elastic.system.intercom.NodeDataMessage;
import net.merayen.elastic.system.intercom.NodeParameterMessage;
import net.merayen.elastic.ui.objects.components.Label;
import net.merayen.elastic.ui.objects.node.UINode;
import net.merayen.elastic.ui.objects.node.UIPort;

public class UI extends UINode {
	private Label midi_device;

	public void onInit() {
		super.onInit();

		width = 100f;
		height = 50f;

		titlebar.title = "MIDI Input";

		// Statistics
		midi_device = new Label();
		midi_device.translation.x = 10f;
		midi_device.translation.y = 20f;
		add(midi_device);
	}

	@Override
	protected void onDraw() {
		midi_device.label = "Device: ";
		super.onDraw();
	}

	public void onRemovePort(String name) {
		// Never removes any port anyway, so not implemented
	}

	@Override
	protected void onCreatePort(UIPort port) {
		if(port.name.equals("output")) {
			port.translation.x = width;
			port.translation.y = 20f;
			port.color = UIPort.MIDI_PORT;
		}
	}

	@Override
	protected void onRemovePort(UIPort port) {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected void onMessage(NodeParameterMessage message) {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected void onData(NodeDataMessage message) {
		// TODO Auto-generated method stub
		
	}
}
