package net.merayen.elastic.backend.architectures.local.nodes.mix_1;

import net.merayen.elastic.backend.architectures.local.LocalProcessor;
import net.merayen.elastic.backend.architectures.local.lets.AudioInlet;
import net.merayen.elastic.backend.architectures.local.lets.AudioOutlet;
import net.merayen.elastic.backend.architectures.local.lets.Inlet;
import net.merayen.elastic.backend.architectures.local.lets.Outlet;
import net.merayen.elastic.util.Postmaster.Message;

public class LProcessor extends LocalProcessor {

	@Override
	protected void onInit() {}

	@Override
	protected void onPrepare() {}

	@Override
	protected void onProcess() {
		Inlet a = getInlet("a");
		Inlet b = getInlet("b");
		Inlet fac = getInlet("fac");
		Outlet out = getOutlet("out");

		int available = available();

		if(out != null) {
			if (a instanceof AudioInlet && b instanceof AudioInlet) {
				float[][] a_buffer = ((AudioOutlet) a.outlet).audio;
				float[][] b_buffer = ((AudioOutlet) b.outlet).audio;

				int channel_count = Math.max(((AudioOutlet) a.outlet).getChannelCount(), ((AudioOutlet) b.outlet).getChannelCount());

				((AudioOutlet) out).setChannelCount(channel_count);

				float[][] out_buffer = ((AudioOutlet) out).audio;

				int stop = out.written + available;

				if (fac instanceof AudioInlet) {
					float[][] fac_buffer = ((AudioOutlet) fac.outlet).audio;

					for (int channel_no = 0; channel_no < channel_count; channel_no++) {
						for (int i = out.written; i < stop; i++) {
							float af = (a_buffer.length > channel_no ? a_buffer[channel_no][i] : 0);
							float bf = (b_buffer.length > channel_no ? b_buffer[channel_no][i] : 0);

							float volume_a = 1 - Math.max(0, Math.min(1, fac_buffer[0][i]));
							float volume_b = 1 - Math.max(0, Math.min(1, fac_buffer[0][i] * -1));

							out_buffer[channel_no][i] = (af * volume_a) + (bf * volume_b);
						}
					}

					fac.read = stop;

				} else {
					float mix = ((LNode) getLocalNode()).mix;
					for (int channel_no = 0; channel_no < channel_count; channel_no++) {
						float[] a_channel = (a_buffer.length > channel_no ? a_buffer[channel_no] : null);
						float[] b_channel = (b_buffer.length > channel_no ? b_buffer[channel_no] : null);

						for (int i = out.written; i < stop; i++)
							out_buffer[channel_no][i] = (a_channel != null ? a_channel[i] : 0) * (1 - mix) + (b_channel != null ? b_channel[i] : 0) * mix;
					}
				}

				out.written = stop;
				a.read = stop;
				b.read = stop;
				out.push();

			} else if(a instanceof AudioInlet) {
				float[][] a_buffer = ((AudioOutlet) a.outlet).audio;

				int channel_count = ((AudioOutlet) a.outlet).getChannelCount();

				((AudioOutlet) out).setChannelCount(channel_count);

				float[][] out_buffer = ((AudioOutlet) out).audio;

				int stop = out.written + available;

				if (fac instanceof AudioInlet) {
					float[][] fac_buffer = ((AudioOutlet) fac.outlet).audio;

					for (int channel_no = 0; channel_no < channel_count; channel_no++) {
						for (int i = out.written; i < stop; i++) {
							float volume_a = 1 - Math.max(0, Math.min(1, fac_buffer[0][i]));
							out_buffer[channel_no][i] = a_buffer[channel_no][i] * volume_a;
						}
					}

					fac.read = stop;
				} else {
					mixNoFac(a, out, a_buffer, channel_count, out_buffer, stop, Math.max(0, Math.min(1, 1 - ((LNode) getLocalNode()).mix)));
				}

				a.read = stop;
				out.written = stop;
				out.push();

			} else if(b instanceof AudioInlet) {
				float[][] b_buffer = ((AudioOutlet) b.outlet).audio;

				int channel_count = ((AudioOutlet) b.outlet).getChannelCount();

				((AudioOutlet) out).setChannelCount(channel_count);

				float[][] out_buffer = ((AudioOutlet) out).audio;

				int stop = out.written + available;

				if (fac instanceof AudioInlet && fac.outlet instanceof AudioOutlet) {
					float[][] fac_buffer = ((AudioOutlet) fac.outlet).audio;

					for (int channel_no = 0; channel_no < channel_count; channel_no++) {
						for (int i = out.written; i < stop; i++) {
							float volume_b = 1 - Math.max(0, Math.min(1, fac_buffer[0][i] * -1));
							out_buffer[channel_no][i] = b_buffer[channel_no][i] * volume_b;
						}
					}

					fac.read = stop;

				} else {
					mixNoFac(b, out, b_buffer, channel_count, out_buffer, stop, Math.max(0, Math.min(1, ((LNode) getLocalNode()).mix)));
				}

				b.read = stop;
				out.written = stop;
				out.push();

			} else {
				if(fac != null)
					fac.read = fac.outlet.written;

				out.written = buffer_size;
				out.push();
			}
		} else { // No output connected. No processing is done. We just forward input ports
			if(fac != null)
				fac.read = fac.outlet.written;
			if(a != null)
				a.read = a.outlet.written;
			if(b != null)
				b.read = b.outlet.written;
		}
	}

	private void mixNoFac(Inlet a, Outlet out, float[][] buffer, int channel_count, float[][] out_buffer, int stop, float mix) {
		for (int channel_no = 0; channel_no < channel_count; channel_no++)
			for (int i = out.written; i < stop; i++)
				out_buffer[channel_no][i] = buffer[channel_no][i] * mix;
	}

	@Override
	protected void onMessage(Message message) {}

	@Override
	protected void onDestroy() {}
}
