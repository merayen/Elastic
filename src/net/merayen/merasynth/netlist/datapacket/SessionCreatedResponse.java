package net.merayen.merasynth.netlist.datapacket;

/*
 * Sent from a left node when it has created a session.
 * TODO Do we need this? Yes, we must, as we need to inform right nodes that they need to request on this session from now on! 
 */
public class SessionCreatedResponse extends ResponsePacket {}
