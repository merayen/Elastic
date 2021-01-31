package net.merayen.elastic.backend.logicnodes.list.midi_in_1;

import net.merayen.elastic.backend.interfacing.AbstractDevice;
import net.merayen.elastic.backend.interfacing.devicetypes.MidiInputDevice;
import net.merayen.elastic.backend.interfacing.types.MidiPacket;
import net.merayen.elastic.backend.logicnodes.Format;
import net.merayen.elastic.backend.nodes.BaseLogicNode;
import net.merayen.elastic.backend.nodes.BaseNodeProperties;
import net.merayen.elastic.system.intercom.InputFrameData;
import net.merayen.elastic.system.intercom.NodeDataMessage;
import net.merayen.elastic.system.intercom.OutputFrameData;

public class LogicNode extends BaseLogicNode {
	MidiInputDevice device;

	@Override
	protected void onInit() {
		createOutputPort("output", Format.MIDI);

		for(AbstractDevice ad : getEnv().getMixer().getAvailableDevices())
			if(ad instanceof MidiInputDevice) {
				if (ad.getId().startsWith("KEYBOARD") || ad.getId().contains("microKEY2 Air") || ad.getId().contains("microKEY2-37") || ad.getId().contains("Code 61 USB MIDI") || ad.getId().contains("Launchkey 25")) {// TODO send all devices to UI Node and let hte user decide
					device = (MidiInputDevice) ad;
					break;
				} else {
					System.out.println("Skipped MIDI device " + ad.getId());
				}
			}
	}

	@Override
	protected void onParameterChange(BaseNodeProperties instance) {
		updateProperties(instance);
	}

	@Override
	protected void onConnect(String port) {}

	@Override
	protected void onDisconnect(String port) {}

	@Override
	protected void onRemove() {}

	@Override
	protected InputFrameData onPrepareFrame() {
		short[][] midi = null;

		if(device != null) {
			MidiPacket[] midi_packets = device.read(getEnv().getConfiguration().getBufferSize());  // TODO soon: Don't retrieve buffer size here? Should be set by a Group-node

			midi = new short[midi_packets.length][];

			int i = 0;
			for(MidiPacket mp : midi_packets)
				midi[i++] = mp.midi; // TODO FIXME FUCKTHIS Nullpointer exception
		}

		return new MidiIn1InputFrameData(getID(), midi);
	}

	@Override
	protected void onFinishFrame(OutputFrameData data) {}

	@Override
	protected void onData(NodeDataMessage data) {}
}
