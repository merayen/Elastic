package net.merayen.merasynth.netlist;

import net.merayen.merasynth.netlist.Port;

public class Line extends NetListObject {
	/*
	 * A connection between two ports
	 */
	private Port a;
	private Port b;
	
	public Line(Supervisor supervisor, Port a, Port b) {
		super(supervisor);
		this.a = a;
		this.b = b;
		
		// Tell the ports that we have been connected
		a.connectLine(this);
		b.connectLine(this);
	}
	
	public void send(Port source, DataPacket data) {
		if(source == a)
			b.push(data);
		else if(source == b)
			a.push(data);
		else
			throw new RuntimeException("This line is not connected to this port");
	}
}
