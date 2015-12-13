package net.merayen.merasynth.netlist.datapacket;

/*
 * This is a somewhat weird request.
 * Its only purpose is to tell the left nodes that they can create new sessions as they please.
 * Left nodes are only permitted to create new session (processors) under this request. 
 */
public class AllowNewSessionsRequest extends RequestPacket {
	public AllowNewSessionsRequest() {
		session_id = DataPacket.MAIN_SESSION; // This packet is not meant for the processors,  but the Net-class
	}
}
