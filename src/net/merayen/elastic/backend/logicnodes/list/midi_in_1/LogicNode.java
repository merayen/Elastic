package net.merayen.elastic.backend.logicnodes.list.midi_in_1;

import java.util.Map;

import net.merayen.elastic.backend.interfacing.AbstractDevice;
import net.merayen.elastic.backend.interfacing.devicetypes.MidiInputDevice;
import net.merayen.elastic.backend.interfacing.types.MidiPacket;
import net.merayen.elastic.backend.logicnodes.Format;
import net.merayen.elastic.backend.nodes.BaseLogicNode;

public class LogicNode extends BaseLogicNode {
	MidiInputDevice device;

	@Override
	protected void onCreate() {
		createPort(new PortDefinition() {{
			name = "output";
			output = true;
			format = Format.MIDI;
		}});
	}

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
	protected void onPrepareFrame(Map<String, Object> data) {
		if(device != null) {
			MidiPacket[] midi_packets = device.read(getEnv().buffer_size);

			short[][] midi = new short[midi_packets.length][];

			int i = 0;
			for(MidiPacket mp : midi_packets)
				midi[i++] = mp.midi;

			data.put("midi", midi);
		}
	}

	@Override
	protected void onFinishFrame(Map<String, Object> data) {}

	@Override
	protected void onData(Map<String, Object> data) {
		// TODO Auto-generated method stub
		
	}

}
