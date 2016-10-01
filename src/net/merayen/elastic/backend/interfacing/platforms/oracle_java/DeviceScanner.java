package net.merayen.elastic.backend.interfacing.platforms.oracle_java;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.Control;
import javax.sound.sampled.Line;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.Mixer;
import javax.sound.sampled.Mixer.Info;
import javax.sound.sampled.SourceDataLine;
import javax.sound.sampled.TargetDataLine;

import net.merayen.elastic.backend.interfacing.AbstractDeviceScanner;
import net.merayen.elastic.backend.interfacing.types.AbstractAudioDevice;
import net.merayen.elastic.backend.interfacing.types.AudioOutputDevice;
import net.merayen.elastic.backend.interfacing.AbstractDevice;

/**
 * Scanner for the Oracle/OpenJDK JRE.
 */
public class DeviceScanner extends AbstractDeviceScanner {
	public DeviceScanner(Handler handler) {
		super(handler);
		scan();
	}

	private void scan() {
		addJavaAudio();
		// TODO scan for external sources too, like VST / AUs. Logic should be centralized, not live inside this oracle_java
	}

	private void addJavaAudio() {
		byte[] audio = new byte[44100*2*2*1];

		convertToBytes(makeSound(44100, 2, new float[]{1000}, 1f), audio, 1, 16);
		/*for(int i = 0; i < audio.length; i++)
			System.out.println(i + ": " + audio[i]);

		if(true) return;*/

		AudioFormat audioformat = new AudioFormat(44100, 16, 1, true, true);

		for(Info i : AudioSystem.getMixerInfo()) {
			Mixer m = AudioSystem.getMixer(i);
			/*Line.Info line_info = m.getLineInfo();
			try {
				m.open();
			} catch (LineUnavailableException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}*/

			System.out.println(i.getName());
			//System.out.println("Rompeballer: " + line_info);
			for(javax.sound.sampled.Line.Info line : m.getSourceLineInfo()) {
				javax.sound.sampled.Line source_line;
				try {
					source_line = m.getLine(line);
				} catch (LineUnavailableException e) {
					throw new RuntimeException(e);
				}

				if(source_line instanceof SourceDataLine) {
					System.out.println("\tSource: " + line + "(" + source_line.toString() + ")");
					SourceDataLine sdl = (SourceDataLine)source_line;
					try {
						sdl.open(audioformat);
						sdl.start();
					} catch (LineUnavailableException e) {
						throw new RuntimeException(e);
					}
					System.out.println("Writing...");
					sdl.write(audio, 0, audio.length);
					System.out.println("Waiting...");
					try {
						Thread.sleep(500);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					sdl.close();
				}

				/*try {
					//if(!(source_line instanceof Clip)) {
					if(source_line instanceof SourceDataLine) {
						source_line.open();
						for(Control c : source_line.getControls())
							System.out.println(c.getType());
						//vent();
						source_line.close();
					}
				} catch (LineUnavailableException e) {
					e.printStackTrace();
				}*/

				//AbstractDevice device = new AudioInputDevice("oracle_java:java_audio:" + line.toString(), "Oracle Java: " + i.getDescription()); // We just know that it is available
				//addDevice(device);
			}

			for(javax.sound.sampled.Line.Info line : m.getTargetLineInfo()) {
				//AbstractDevice device = new AudioOutputDevice("oracle_java:java_audio:" + line.toString(), "Oracle Java: " + i.getDescription()); // We just know that it is available
				//addDevice(device);
				javax.sound.sampled.Line target_line;
				try {
					target_line = m.getLine(line);
				} catch (LineUnavailableException e) {
					throw new RuntimeException(e);
				}
				if(target_line instanceof TargetDataLine) {
					System.out.println("\tTarget: " + line + "(" + target_line.toString() + ")");
				}

				/*try {
					//if(!(target_line instanceof Clip)) {
					if(target_line instanceof TargetDataLine) {
						target_line.open();
						for(Control c : target_line.getControls())
							System.out.println(c.getType());

						//vent();
						target_line.close();
					}
				} catch (LineUnavailableException e) {
					e.printStackTrace();
				}*/
			}
		}
	}

	private void vent() {
		try {
			Thread.sleep(5000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	/*private byte[] makeSound(int sampleRate, int seconds, int depth, float[] frequencies, float amplitude) {
		if(depth > 0 && depth % 8 != 0)
			throw new RuntimeException();

		int frame_size = frequencies.length * depth / 8;
		int bytes_depth = depth / 8;

		byte[] out = new byte[sampleRate * seconds * bytes_depth * frequencies.length];

		for(byte channel = 0; channel < frequencies.length; channel++) {
			for(int i = 0; i < sampleRate * seconds; i++) {
				double pos = Math.sin((i / (double)sampleRate) * frequencies[channel] * Math.PI * 2) / 2 * amplitude;
				long v = (long)(pos * Math.pow(2, depth));

				for(int j = 0; j < bytes_depth; j++)
					out[channel * bytes_depth + i * frame_size + j] = (byte)((v >> (bytes_depth - j - 1) * 8));
			}
		}

		return out;
	}*/

	private float[] makeSound(int sampleRate, int seconds, float[] frequencies, float amplitude) {
		float[] out = new float[sampleRate * seconds * frequencies.length];

		for(byte channel = 0; channel < frequencies.length; channel++) {
			for(int i = 0; i < sampleRate * seconds; i++)
				out[i * frequencies.length + channel] = (float)(Math.sin((i / (double)sampleRate) * frequencies[channel] * Math.PI * 2) / 2 * amplitude);
		}

		return out;
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
}
