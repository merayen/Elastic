package net.merayen.elastic.backend.interfacing.platforms.oracle_java;

import net.merayen.elastic.backend.interfacing.AbstractDevice;
import net.merayen.elastic.backend.interfacing.devicetypes.AudioOutputDevice;
import net.merayen.elastic.backend.util.AudioUtil;

import javax.sound.sampled.*;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

/**
 * Wrapper for Oracle Java implementation of an audio device.
 */
public class OracleAudioOutputDevice extends AudioOutputDevice {
	public final SourceDataLine line;

	private byte[] buffer = new byte[0];
	private ByteBuffer byteBuffer = ByteBuffer.allocate(0);

	public OracleAudioOutputDevice(Mixer mixer, SourceDataLine line) {
		super("oracle_java:" + /*mixer.getMixerInfo().getVendor() + "/" + */mixer.getMixerInfo().getName(), "Lalala", mixer.getMixerInfo().getVendor());
		this.line = line;
		line.addLineListener(event -> {
			// TODO try to detect if device gets disconnected
		});
	}

	@Override
	public void onReconfigure() {
		line.close();
	}

	@Override
	public int available() {
		if (line.isActive()) {
			Configuration c = (Configuration) getConfiguration();
			return (line.getBufferSize() - line.available()) / c.channels / (c.depth / 8);
		} else {
			return 0;
		}
	}

	@Override
	public int getBufferSampleSize() {
		Configuration c = (Configuration) getConfiguration();
		return line.getBufferSize() / c.channels / (c.depth / 8);
	}

	@Override
	public void spool(int samples) {
		System.out.println("Spooling");
		Configuration c = (Configuration) getConfiguration();
		int to_write = samples * (c.depth / 8) * c.channels;
		line.write(new byte[to_write], 0, to_write);
	}

	private void convertToBytes(float[] audio, byte[] out, int channels, int depth) {
		int bytes_depth = depth / 8;
		int sample_count = audio.length / channels;

		if (out.length / bytes_depth != audio.length)
			throw new RuntimeException("Invalid length of output byte-buffer. Got " + out.length + " but expected " + audio.length * bytes_depth);

		byteBuffer.rewind();
		for (int i = 0; i < sample_count; i++) {
			for (byte channel = 0; channel < channels; channel++) {
				float u = audio[i * channels + channel];

				// Clipping
				if (u > 1f) u = 1f;
				else if (u < -1f) u = -1f;

				byteBuffer.putShort((short) (u * 65535 / 2)); // Too costly?
			}
		}

		byteBuffer.rewind();
		byteBuffer.get(out, 0, channels * bytes_depth * sample_count);
	}

	private long last_write = System.currentTimeMillis();

	@Override
	public void onWrite(float[] audio) {
		Configuration c = (Configuration) getConfiguration();

		if (buffer.length / (c.depth / 8) != audio.length) {
			buffer = new byte[audio.length * c.depth / 8];
			byteBuffer = ByteBuffer.allocate(audio.length * (c.depth / 8));
		}

		convertToBytes(audio, buffer, c.channels, c.depth);

		int available = available();
		if (available == 0)
			getStatistics().setHunger(getStatistics().getHunger() + 1);

		getStatistics().getAvailable_before().add(available);

		prepareLine(buffer.length);

		getStatistics().getOutside_buffer_time().add((System.currentTimeMillis() - last_write) / 1000f);
		long t = System.currentTimeMillis();

		line.write(buffer, 0, buffer.length);
		//if (available == 0)
		//	line.write(buffer, 0, buffer.length);

		last_write = System.currentTimeMillis();
		getStatistics().getBuffer_time().add((System.currentTimeMillis() - t) / 1000f);

		getStatistics().getSamples_processed().add(audio.length / (c.depth / 8) / c.channels);

		available = available();
		getStatistics().getAvailable_after().add(available);

		if(available == 0)
			getStatistics().setHunger(getStatistics().getHunger() + 1);
	}

	@Override
	public void onWrite(float[][] audio) {
		float[] output = new float[audio[0].length * ((Configuration) getConfiguration()).channels];
		AudioUtil.mergeChannels(audio, output, audio[0].length, ((Configuration) getConfiguration()).channels);
		onWrite(output);
	}

	public void directWrite(byte[] audio) {
		prepareLine(audio.length);
		line.write(audio, 0, audio.length);
	}

	@Override
	protected void onStop() {
		//line.drain();
		//if(line.isActive())
		//	line.close();
	}

	@Override
	protected void onKill() {
		line.close();
	}

	@Override
	public List<AbstractDevice.Configuration> getAvailableConfigurations() {
		List<AbstractDevice.Configuration> result = new ArrayList<>();

		DataLine.Info omg = ((DataLine.Info) line.getLineInfo());
		for (AudioFormat af : omg.getFormats())
			if (af.getEncoding() == AudioFormat.Encoding.PCM_SIGNED && af.isBigEndian()) // Only support PCM_SIGNED and big-endianness for now
				result.add(new Configuration((int) af.getSampleRate(), af.getChannels(), af.getSampleSizeInBits()));

		return result;
	}

	private void prepareLine(int buffer_size) {
		Configuration c = (Configuration) getConfiguration();

		if (!line.isOpen() || line.getBufferSize() != buffer_size * 1) {
			try {
				System.out.printf("Reconfiguring Oracle audio output device %s, buffer_size=%d (current buffer size: %d)\n", c.getDescription(), buffer_size, line.getBufferSize());

				if (line.isOpen())
					line.close();

				line.open(new AudioFormat(c.sample_rate, c.depth, c.channels, true, true), buffer.length * 1);
			} catch (LineUnavailableException e) {
				throw new RuntimeException("Can not open audio output line with current configuration");
			}
		}

		if (!line.isActive())
			line.start();
	}
}
