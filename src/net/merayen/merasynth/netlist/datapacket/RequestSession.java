package net.merayen.merasynth.netlist.datapacket;

/**
 * Left node requests a node on the right side to open a new session,
 * where the DataPacket.session_id decides the id for the session.
 * Only some generators, like random generator will open a new session,
 * otherwise ignored by other generators that creates their own session on their own,
 * (like MIDI input).
 */
public class RequestSession extends ControlRequest {}
