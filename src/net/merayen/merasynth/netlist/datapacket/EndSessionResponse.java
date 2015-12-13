package net.merayen.merasynth.netlist.datapacket;

/*
 * Sent by left node telling all other nodes to stop processing and that this session is now gone.
 * Only the node creating the session should end sessions
 */
public class EndSessionResponse extends ResponsePacket {}
