package net.merayen.elastic.backend.logicnodes.list.midi_1;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import net.merayen.elastic.backend.interfacing.types.MidiPacket;
import net.merayen.elastic.backend.logicnodes.Format;
import net.merayen.elastic.backend.nodes.BaseLogicNode;

public class LogicNode extends BaseLogicNode {

	List<MidiPacket> buffer = new ArrayList<>();

	@Override
	protected void onCreate() {
		createPort(new PortDefinition() {{
			name = "in";
		}});

		createPort(new PortDefinition() {{
			name = "out";
			output = true;
			format = Format.MIDI;
		}});
	}

	@Override
	protected void onInit() {
		// TODO Auto-generated method stub

	}

	@Override
	protected void onParameterChange(String key, Object value) {
		set(key, value);
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
		short[][] midi = new short[buffer.size()][];

		int i = 0;
		for(MidiPacket mp : buffer)
			midi[i++] = mp.midi;

		buffer.clear();

		data.put("midi", midi);
	}

	@Override
	protected void onFinishFrame(Map<String, Object> data) {
		// TODO Auto-generated method stub

	}

	@Override
	protected void onData(Map<String, Object> data) {
		if(data.containsKey("tangent_down"))
			buffer.add(new MidiPacket(new short[]{(short)0b10010000, ((Number)data.get("tangent_down")).shortValue(), 64}, 0));
		if(data.containsKey("tangent_up"))
			buffer.add(new MidiPacket(new short[]{(short)0b10000000, ((Number)data.get("tangent_up")).shortValue(), 64}, 0));
	}

}
