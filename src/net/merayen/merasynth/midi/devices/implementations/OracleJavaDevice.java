package net.merayen.merasynth.midi.devices.implementations;

import java.util.ArrayList;
import java.util.List;

import javax.sound.midi.MidiDevice;
import javax.sound.midi.MidiMessage;
import javax.sound.midi.MidiSystem;
import javax.sound.midi.MidiUnavailableException;
import javax.sound.midi.Receiver;
import javax.sound.midi.Transmitter;

import net.merayen.merasynth.midi.devices.IMIDIDeviceAdapter;
import net.merayen.merasynth.midi.devices.IMIDIReceiver;

public class OracleJavaDevice implements IMIDIDeviceAdapter {
	private final String name;
	private final MidiDevice device;

	private boolean active;

	OracleJavaDevice(MidiDevice device, MidiDevice.Info info) {
		this.device = device;
		this.name = info.getName();
	}

	public void open(IMIDIReceiver handler) {
		if(active)
			throw new RuntimeException("MIDI device already open");

		OracleJavaDevice self = this;

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

				handler.onReceive(midi, timeStamp);
			}

			@Override
			public void close() {
				self.close();
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

	public void close() {
		if(device.isOpen())
			device.close();

		active = false;
	}

	public String getName() {
		return name;
	}

	public static List<IMIDIDeviceAdapter> scan() {
		ArrayList<IMIDIDeviceAdapter> devices = new ArrayList<>();

		for(MidiDevice.Info info : MidiSystem.getMidiDeviceInfo()) {
			MidiDevice device;

			try {
				device = MidiSystem.getMidiDevice(info);
			} catch (MidiUnavailableException e) {
				e.printStackTrace();
				throw new RuntimeException();
			}

			if(device.getMaxTransmitters() == 0)
				continue;

			//System.out.printf("Device found: %s: %s, %s, %s, %s, %d\n", info, info.getName(), info.getDescription(), info.getVendor(), info.getVersion(), device.getMaxTransmitters());

			devices.add(new OracleJavaDevice(device, info));
		}

		return devices;
	}
}
