package net.merayen.elastic.backend.logicnodes.list.output_1;

import java.io.ObjectOutputStream.PutField;
import java.util.HashMap;
import java.util.Map;

import net.merayen.elastic.backend.interfacing.AbstractDevice;
import net.merayen.elastic.backend.interfacing.devicetypes.AudioDevice;
import net.merayen.elastic.backend.logicnodes.Environment;
import net.merayen.elastic.backend.mix.datatypes.Audio;
import net.merayen.elastic.backend.nodes.BaseLogicNode;

public class LogicNode extends BaseLogicNode {
	private String output_device;

	@Override
	protected void onCreate() {
		createPort(new BaseLogicNode.PortDefinition() {{
			name = "input";
		}});
	}

	@Override
	protected void onInit() {
		Environment env = (Environment)getEnv();
		for(AbstractDevice ad : env.mixer.getAvailableDevices())
			if(ad instanceof AudioDevice)
				if(((AudioDevice)ad).isOutput())
					if(
						ad.id.equals("oracle_java:Default Audio Device") ||// Mac OS X 10.9
						ad.id.equals("oracle_java:PulseAudio Mixer") // Ubuntu 16.04
					)
						output_device = ad.id;
	}

	@Override
	protected void onConnect(String port) {}

	@Override
	protected void onDisconnect(String port) {}

	@Override
	protected void onParameterChange(String key, Object value) {
		set(key, value);
	}

	@Override
	protected void onPrepareFrame(Map<String, Object> data) {}

	@SuppressWarnings("serial")
	@Override
	protected void onFinishFrame(Map<String, Object> data) {
		float[][] fa = (float[][])data.get("audio");

		// Count max channels
		int channel_count = fa.length;

		if(channel_count == 0)
			return; // Don't bother

		int sample_count = this.getEnv().buffer_size;

		float[/* channel no */][/* sample no */] out = new float[channel_count][];

		int i = 0;
		for(int channel_no = 0; channel_no < channel_count; channel_no++) {
			float[] channel = fa[channel_no];

			if(channel != null)
				out[i] = channel;
			else
				out[i] = new float[sample_count];

			i++;
		}

		//System.out.println("Amplitude: " + ((FloatArray)data.data.get("amplitude")).data[0]);
		if(data.containsKey("vu"))
			sendDataToUI(new HashMap<String, Object>() {{put("vu", data.get("vu"));}});
		if(data.containsKey("offset"))
			sendDataToUI(new HashMap<String, Object>() {{put("offset", data.get("offset"));}});

		((Environment)getEnv()).mixer.send(output_device, new Audio(out));
	}

	@Override
	protected void onRemove() {}

	@Override
	protected void onData(Map<String, Object> data) {
		// TODO Auto-generated method stub
		
	}
}
