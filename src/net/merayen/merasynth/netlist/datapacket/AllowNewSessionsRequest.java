package net.merayen.merasynth.netlist.datapacket;

/**
 * This is a somewhat weird request.
 * Its only purpose is to tell the left nodes that they can create new sessions as they please.
 * Left nodes are only permitted to create new session (processors) under this request.
 * This should probably be sent before any RequestPacket(), each time.
 * 
 * Left-most node can reply with a SessionCreatedResponse() on either MAIN_SESSION or custom
 * session.
 */
public class AllowNewSessionsRequest extends ControlRequest {
	public AllowNewSessionsRequest() {
		session_id = DataPacket.CONTROL_SESSION; // This packet is not meant for the processors,  but the Net-class
	}
}
