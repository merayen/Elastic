package net.merayen.merasynth.netlist.datapacket;

/**
 * Sent by left node telling all other nodes to stop processing and that this session is now gone.
 * This should be sent when the left node has received a EndSessionHint, but not required to do immediately,
 * like if there is other nodes connected to the nodes output that still wants data.
 */
public class EndSessionResponse extends ControlResponse {}
