package net.merayen.elastic.uinodes.list.midi_in_1;

import net.merayen.elastic.system.intercom.NodeDataMessage;
import net.merayen.elastic.system.intercom.NodeParameterMessage;
import net.merayen.elastic.ui.objects.components.DropDown;
import net.merayen.elastic.ui.objects.components.Label;
import net.merayen.elastic.ui.objects.node.UINode;
import net.merayen.elastic.ui.objects.node.UIPort;
import net.merayen.elastic.ui.objects.top.menu.MenuListItem;

public class UI extends UINode {
	private Label midi_device;
	private DropDown which;

	public void onInit() {
		super.onInit();

		width = 150f;
		height = 100f;

		titlebar.title = "MIDI Input";

		midi_device = new Label();
		midi_device.translation.x = 10f;
		midi_device.translation.y = 20f;
		add(midi_device);

		which = new DropDown();
		which.translation.x = 10;
		which.translation.y = 50;
		add(which);

		MenuListItem mli = new MenuListItem();
		mli.label = "Hei!";
		mli.setHandler(new MenuListItem.Handler() {
			@Override
			public void onClick() {
				System.out.println("Yoho!");
			}
		});
		which.menu_list.addMenuItem(mli);

		mli = new MenuListItem();
		mli.label = "Du!";
		which.menu_list.addMenuItem(mli);
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
