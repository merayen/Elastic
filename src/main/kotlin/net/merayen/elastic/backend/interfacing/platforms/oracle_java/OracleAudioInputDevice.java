package net.merayen.elastic.backend.interfacing.platforms.oracle_java;

import java.util.ArrayList;
import java.util.List;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.Mixer;
import javax.sound.sampled.TargetDataLine;

import net.merayen.elastic.backend.interfacing.AbstractDevice;
import net.merayen.elastic.backend.interfacing.devicetypes.AudioDevice;
import net.merayen.elastic.backend.interfacing.devicetypes.AudioInputDevice;
import net.merayen.elastic.backend.interfacing.devicetypes.AudioDevice.Configuration;

/**
 * Wrapper for Oracle Java implementation of an audio device.
 */
public class OracleAudioInputDevice extends AudioInputDevice {
	private final TargetDataLine line;
	private byte[] buffer = new byte[0];

	public OracleAudioInputDevice(Mixer mixer, TargetDataLine line) {
		super("oracle_java:" + mixer.getMixerInfo().getVendor() + "/" + mixer.getMixerInfo().getName(), "Lalala", mixer.getMixerInfo().getVendor());
		this.line = line;
	}

	@Override
	public int available() {
		Configuration c = (Configuration)configuration;
		return (line.available() - line.getBufferSize()) / c.channels / (c.depth / 8);
	}

	@Override
	public int getBufferSampleSize() {
		Configuration c = (Configuration)configuration;
		return line.getBufferSize() / c.channels / (c.depth / 8);
	}

	@Override
	public void spool(int samples) {
		Configuration c = (Configuration)configuration;
		int to_read = samples * (c.depth / 8) * c.channels;
		line.read(new byte[to_read], 0, to_read);
	}

	@Override
	protected void onStop() {
		line.stop();
	}

	@Override
	protected void onKill() {
		line.close();
	}

	@Override
	public void onReconfigure() {
		line.close();
	}

	@Override
	public List<AbstractDevice.Configuration> getAvailableConfigurations() {
		List<AbstractDevice.Configuration> result = new ArrayList<>();

		DataLine.Info omg = ((DataLine.Info)line.getLineInfo());
		for(AudioFormat af : omg.getFormats())
			if(af.getEncoding() == AudioFormat.Encoding.PCM_SIGNED && af.isBigEndian()) // Only support PCM_SIGNED and big-endianness for now
				result.add(new AudioDevice.Configuration((int)af.getSampleRate(), af.getChannels(), af.getSampleSizeInBits()));

		return result;
	}

	@Override
	public void onRead(float[] audio) {
		Configuration c = (Configuration)configuration;

		prepareLine();

		if(buffer.length * c.depth / 8 != audio.length) 
			buffer = new byte[audio.length * c.depth / 8];

		int read = line.read(buffer, 0, buffer.length);

		if(read != audio.length * c.channels * c.depth / 8)
			throw new RuntimeException("Nope");

		convertToFloat(buffer, audio);
	}

	public void directRead(byte[] audio) {
		prepareLine();

		line.read(audio, 0, audio.length);
	}

	private void convertToFloat(byte[] in, float[] out) {
		Configuration c = (Configuration)configuration;

		if(c.depth != 16)
			throw new RuntimeException("Does not support anything else than 16 bit input audio now");

		int bytes_depth = c.depth / 8;
		double div = Math.pow(2, c.depth);

		int length = out.length;
		long d;
		for(int i = 0; i < length; i++) {
			d = 0;
			//out[i] = (byte)in[bytes_depth * i + 1] << 8;
			//for(int j = 0; j < bytes_depth; j++)
			//	d += ((long)in[bytes_depth * i + j] & 0xFF) << (bytes_depth - j - 1);

			//for(int j = 0; j < bytes_depth; j++)
			//	d |= (in[bytes_depth * i + j]/* & 0xFF*/) << (bytes_depth * 8 - j * 8 - 8);

			//d |= (long)in[bytes_depth * i]) << 8;

			//out[i] = (float)(d / div);
			//out[i] = ((long)in[bytes_depth * i] & 0xFF) * 10000 + ((long)in[bytes_depth * i + 1] & 0xFF);//(float)(d / div/* - div / 2*/);

			out[i] = (float)(((in[bytes_depth * i] * Math.pow(2, 8)) + ((int)in[bytes_depth * i + 1] & 0xFF)) / (div / 2)); // This is wrong. has DC offset
		}
	}

	private void prepareLine() {
		if(!line.isOpen()) {
			Configuration c = (Configuration)configuration;

			try {
				line.open(new AudioFormat(c.sample_rate, c.depth, c.channels, true, true), 512 * (c.depth / 8) * c.channels); // Kind of fixed buffer size, we don't want to miss any samples, therefore huge
			} catch (LineUnavailableException e) {
				throw new RuntimeException(e);
			}
		}

		if(!line.isActive())
			line.start();
	}
}
