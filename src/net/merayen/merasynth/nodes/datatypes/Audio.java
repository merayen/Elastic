package net.merayen.merasynth.nodes.datatypes;

import net.merayen.merasynth.netlist.DataPacket;
import net.merayen.merasynth.types.AudioClip;

public class Audio extends DataPacket {
	/*
	 * Datapacket that contains audio
	 */
	
	public Audio(AudioClip audio_clip) {
		super(audio_clip);
	}
	
	public AudioClip getData() {
		return (AudioClip)this.data;
	}
}
