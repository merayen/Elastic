package net.merayen.elastic.backend.interfacing.platforms.oracle_java;

import java.util.ArrayList;
import java.util.List;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.LineEvent;
import javax.sound.sampled.LineListener;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.Mixer;
import javax.sound.sampled.SourceDataLine;

import net.merayen.elastic.backend.interfacing.AbstractDevice;
import net.merayen.elastic.backend.interfacing.devicetypes.AudioOutputDevice;
import net.merayen.elastic.backend.util.AudioUtil;

/**
 * Wrapper for Oracle Java implementation of an audio device.
 */
public class OracleAudioOutputDevice extends AudioOutputDevice {
	private final SourceDataLine line;

	private byte[] buffer = new byte[0];

	public OracleAudioOutputDevice(Mixer mixer, SourceDataLine line) {
		super("oracle_java:" + /*mixer.getMixerInfo().getVendor() + "/" + */mixer.getMixerInfo().getName(), "Lalala", mixer.getMixerInfo().getVendor());
		this.line = line;
		line.addLineListener(new LineListener() {

			@Override
			public void update(LineEvent event) {
				// TODO try to detect if device gets disconnected
			}
			
		});
	}

	@Override
	protected void onBegin() {
		Configuration c = (Configuration)configuration;

		try {
			line.open(new AudioFormat(c.sample_rate, c.depth, c.channels, true, true), 1024);
		} catch (LineUnavailableException e) {
			throw new RuntimeException("Can not open audio output line with current configuration");
		}
	}

	@Override
	public int getBalance() {
		Configuration c = (Configuration)configuration;

		//return (line.getBufferSize() - line.available() - line.) / (c.depth / 8) / c.channels;
		return 0;
	}

	private void convertToBytes(float[] audio, byte[] out, int channels, int depth) {
		int frame_size = channels * depth / 8;
		int bytes_depth = depth / 8;
		int sample_count = audio.length / channels;

		if(out.length / bytes_depth != audio.length)
			throw new RuntimeException("Invalid length of output byte-buffer. Got " + out.length + " but expected " + audio.length * bytes_depth);

		for(byte channel = 0; channel < channels; channel++) {
			for(int i = 0; i < sample_count; i++) {
				long v = (long)(audio[i * channels + channel] * Math.pow(2, depth));

				for(int j = 0; j < bytes_depth; j++)
					out[channel * bytes_depth + i * frame_size + j] = (byte)((v >> (bytes_depth - j - 1) * 8));
			}
		}
	}

	@Override
	public void onWrite(float[] audio) {
		Configuration c = (Configuration)configuration;

		if(!line.isActive())
			line.start();

		if(buffer.length * c.depth / 8 != audio.length) 
			buffer = new byte[audio.length * c.depth / 8];

		convertToBytes(audio, buffer, c.channels, c.depth);

		line.write(buffer, 0, buffer.length);
	}

	@Override
	public void onWrite(float[][] audio) {
		float[] output = new float[audio[0].length * ((Configuration)configuration).channels];
		AudioUtil.mergeChannels(audio, output, audio[0].length, ((Configuration)configuration).channels);
		onWrite(output);
	}

	public void directWrite(byte[] audio) {
		if(!line.isActive())
			line.start();

		line.write(audio, 0, audio.length);
	}

	@Override
	protected void onStop() {
		line.drain();
		if(line.isActive())
			line.stop();
	}

	@Override
	protected void onKill() {
		line.close();
	}

	@Override
	public List<AbstractDevice.Configuration> getAvailableConfigurations() {
		List<AbstractDevice.Configuration> result = new ArrayList<>();

		DataLine.Info omg = ((DataLine.Info)line.getLineInfo());
		for(AudioFormat af : omg.getFormats())
			if(af.getEncoding() == AudioFormat.Encoding.PCM_SIGNED && af.isBigEndian()) // Only support PCM_SIGNED and big-endianness for now
			result.add(new Configuration((int)af.getSampleRate(), af.getChannels(), af.getSampleSizeInBits()));

		return result;
	}
}
