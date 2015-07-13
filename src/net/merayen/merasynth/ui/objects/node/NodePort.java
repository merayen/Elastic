package net.merayen.merasynth.ui.objects.node;

class NodePort {
	public final boolean output; // true == port outputs data. false == port receives data
	public final Port port;

	public NodePort(Port port, boolean output) {
		this.port = port;
		this.output = output;
	}
}
