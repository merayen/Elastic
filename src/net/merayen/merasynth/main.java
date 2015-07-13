package net.merayen.merasynth;

public class Main {
	public static void main(String jhgjhg[]) {
		WindowManager wm = new WindowManager();
		Window w = wm.create();
		w.node_system.addNode(net.merayen.merasynth.client.signalgenerator.Glue.class);
	}
}
