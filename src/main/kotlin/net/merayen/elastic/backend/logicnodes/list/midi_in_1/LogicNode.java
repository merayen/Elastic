package net.merayen.elastic.backend.logicnodes.list.midi_in_1;

import kotlin.NotImplementedError;
import net.merayen.elastic.backend.interfacing.AbstractDevice;
import net.merayen.elastic.backend.interfacing.devicetypes.MidiInputDevice;
import net.merayen.elastic.backend.interfacing.types.MidiPacket;
import net.merayen.elastic.backend.logicnodes.Format;
import net.merayen.elastic.backend.nodes.BaseLogicNode;
import net.merayen.elastic.system.intercom.InputFrameData;
import net.merayen.elastic.system.intercom.OutputFrameData;

public class LogicNode extends BaseLogicNode {
	MidiInputDevice device;

	@Override
	protected void onCreate() {
		createPort(new PortDefinition("output", Format.MIDI));
	}

	@Override
	protected void onInit() {
		for(AbstractDevice ad : getEnv().mixer.getAvailableDevices())
			if(ad instanceof MidiInputDevice)
				if(ad.getId().startsWith("KEYBOARD") || ad.getId().contains("microKEY2 Air") || ad.getId().contains("microKEY2-37") || ad.getId().contains("Code 61 USB MIDI")) // TODO send all devices to UI Node and let hte user decide
					device = (MidiInputDevice)ad;
				else
					System.out.println("Skipped MIDI device " + ad.getId());
	}

	@Override
	protected void onParameterChange(String key, Object value) {
		set(key, value);
	}

	@Override
	protected void onConnect(String port) {}

	@Override
	protected void onDisconnect(String port) {}

	@Override
	protected void onRemove() {}

	@Override
	protected InputFrameData onPrepareFrame() {
		/*if(device != null) {
			MidiPacket[] midi_packets = device.read(getEnv().buffer_size);

			short[][] midi = new short[midi_packets.length][];

			int i = 0;
			for(MidiPacket mp : midi_packets)
				midi[i++] = mp.midi; // TODO FIXME FUCKTHIS Nullpointer exception

			data.put("midi", midi);
		}*/
		throw new NotImplementedError("Fiks");
	}

	@Override
	protected void onFinishFrame(OutputFrameData data) {}

	@Override
	protected void onData(Object data) {}
}
