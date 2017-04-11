package net.merayen.elastic.backend.logicnodes.list.midi_in_1;

import net.merayen.elastic.backend.interfacing.AbstractDevice;
import net.merayen.elastic.backend.interfacing.devicetypes.MidiInputDevice;
import net.merayen.elastic.backend.interfacing.types.MidiPacket;
import net.merayen.elastic.backend.nodes.BaseLogicNode;
import net.merayen.elastic.util.pack.PackDict;

public class LogicNode extends BaseLogicNode {
	MidiInputDevice device;

	@Override
	protected void onCreate() {}

	@Override
	protected void onInit() {
		for(AbstractDevice ad : getEnv().mixer.getAvailableDevices())
			if(ad instanceof MidiInputDevice)
				if(ad.id.startsWith("KEYBOARD")) // TODO send all devices to UI Node and let hte user decide
					device = (MidiInputDevice)ad;
	}

	@Override
	protected void onParameterChange(String key, Object value) {
		// TODO Auto-generated method stub

	}

	@Override
	protected void onConnect(String port) {
		// TODO Auto-generated method stub

	}

	@Override
	protected void onDisconnect(String port) {
		// TODO Auto-generated method stub

	}

	@Override
	protected void onRemove() {
		// TODO Auto-generated method stub

	}

	@Override
	protected void onPrepareFrame(PackDict data) {
		if(device != null) {
			for(MidiPacket mp : device.read(getEnv().buffer_size)) {
				System.out.print(mp.sample_offset);
				for(short s : mp.midi)
					System.out.print(" " + s);
				System.out.println();
			}
		}
	}

	@Override
	protected void onFinishFrame(PackDict data) {
		// TODO Auto-generated method stub

	}

}
