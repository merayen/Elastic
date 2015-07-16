package net.merayen.merasynth.client.output;

import net.merayen.merasynth.ui.objects.components.Button;
import net.merayen.merasynth.ui.objects.node.UINode;
import net.merayen.merasynth.ui.objects.node.UIPort;

public class UI extends UINode {
	private UIPort input_port;

	public void onInit() {
		super.onInit();
		UI self = this;

		width = 10f;
		height = 10f;

		titlebar.title = "Output";

		// Test button
		Button testbutton = new Button();
		testbutton.label = "Request";
		testbutton.translation.x = 1f;
		testbutton.translation.y = 5f;
		add(testbutton);

		testbutton.setHandler(new Button.IHandler() {
			@Override
			public void onClick() {
				((Glue)self.getGlueNode()).testbuttonClicked();
			}
		});
	}

	@Override
	public void onCreatePort(String name) {
		if(name.equals("input")) {
			input_port = new UIPort("input", false);
			input_port.translation.x = 0f;
			input_port.translation.y = 2f;
			addPort(input_port);
		}
	}

	@Override
	public void onRemovePort(String name) {
		// Never removes any port anyway, so not implemented
	}
}
