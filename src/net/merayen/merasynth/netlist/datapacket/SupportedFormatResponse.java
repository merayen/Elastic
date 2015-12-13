package net.merayen.merasynth.netlist.datapacket;

import java.util.ArrayList;
import java.util.List;

/*
 * Left node send to right nodes which formats it supports.
 * This packet is automatically sent when node is rewired or initialized.
 */
public class SupportedFormatResponse extends ResponsePacket {
	public final List<Class> supported_formats = new ArrayList<>();

	public SupportedFormatResponse() {
		super();
		session_id = DataPacket.MAIN_SESSION; // Should not be handled by Processors.
	}
}
