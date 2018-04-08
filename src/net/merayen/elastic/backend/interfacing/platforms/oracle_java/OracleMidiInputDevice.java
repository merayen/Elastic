package net.merayen.elastic.backend.interfacing.platforms.oracle_java;

import java.util.ArrayList;
import java.util.List;

import javax.sound.midi.MidiDevice;
import javax.sound.midi.MidiMessage;
import javax.sound.midi.MidiUnavailableException;
import javax.sound.midi.Receiver;
import javax.sound.midi.Transmitter;

import net.merayen.elastic.backend.interfacing.AbstractDevice;
import net.merayen.elastic.backend.interfacing.devicetypes.MidiInputDevice;
import net.merayen.elastic.backend.interfacing.types.MidiPacket;

/**
 * XXX Doesn't care about MIDI-timestamps for now
 */
public class OracleMidiInputDevice extends MidiInputDevice {
	private boolean active;
	private final List<MidiPacket> buffer = new ArrayList<>();
	private final MidiDevice device;

	public OracleMidiInputDevice(MidiDevice device) {
		super(device.getDeviceInfo().getName() + " - " + device.getDeviceInfo().getDescription(), device.getDeviceInfo().getDescription(), device.getDeviceInfo().getVendor());
		this.device = device;
	}

	@Override
	public void onReconfigure() {}

	@Override
	protected void onStop() {
		
	}
 
	@Override
	protected void onKill() {
		
	}

	@Override
	public void spool(int samples) {}

	@SuppressWarnings("serial")
	@Override
	public List<AbstractDevice.Configuration> getAvailableConfigurations() {
		return new ArrayList<AbstractDevice.Configuration>() {{
			add(new Configuration(0) {}); // MIDI doesn't care about sample rate, use what you want
		}};
	}

	@Override
	public int available() {
		return Integer.MAX_VALUE; // Lol, we have no idea so, yeah
	}

	@Override
	public boolean isOutput() {
		return false;
	}

	@Override
	public synchronized MidiPacket[] onRead(int sample_count) {
		ensureRunning();

		// Returns everything for now
		// TODO respect timestamps and sample_count
		MidiPacket[] output = buffer.toArray(new MidiPacket[buffer.size()]);
		buffer.clear();
		return output;
	}

	private void ensureRunning() {
		if(active)
			return;

		OracleMidiInputDevice self = this;

		Transmitter trans = null;
		try {
			trans = device.getTransmitter();
		} catch (MidiUnavailableException e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		}

		trans.setReceiver(new Receiver() {
			@Override
			public void send(MidiMessage message, long timeStamp) {
				byte[] mess = message.getMessage();
				short[] midi = new short[mess.length];

				for(int i = 0; i < mess.length; i++)
					midi[i] = (short)(mess[i] & 0xFF);

				self.buffer.add(new MidiPacket(midi, 0)); // TODO fix timestamping, so stuff is correct
			}

			@Override
			public void close() {
				self.active = false; // ???
			}
		});

		try {
			device.open();
		} catch (MidiUnavailableException e) {
			e.printStackTrace();
			throw new RuntimeException();
		}

		active = true;
	}
}
