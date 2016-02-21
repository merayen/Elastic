package net.merayen.merasynth.netlist.datapacket;

/**
 * Sent by right node.
 * Informs any isolated left-nodes that no more data is necessary as we are shutting down.
 * When a node receives EndSessionResponse on one of its ports, that port is then "dead",
 * meaning that no more requests can be sent, or data received.
 * If node is latching a session (e.g ADSR), then node should only request this on its input ports when ready to do so.
 * If node is not latching, request all ports immediately with this packet.
 * Only left-most nodes (with no connections on the left) should actually kill their session when receiving this packet.
 * When left-most node receiving this  packet, and that node allows to kill the session, send the EndSessionResponse as usual,
 * which actually ends the session on port for the right node.
 * XXX What if multiple nodes are connected to one left-most node? How should the left-most node react? Hmm...
 */
public class EndSessionHint extends ControlRequest {}
