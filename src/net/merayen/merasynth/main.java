package net.merayen.merasynth;

import net.merayen.merasynth.glue.nodes.GlueNode;

public class Main {
	public static void main(String jhgjhg[]) {
		WindowManager wm = new WindowManager();
		Window w = wm.create();
		for(int i = 0; i < 5; i++ ) {
			GlueNode node = w.node_system.addNode(net.merayen.merasynth.client.signalgenerator.Glue.class);
			node.getUINode().translation.x = 10f + i * 10f;
			node.getUINode().translation.y = 10f + i * 10f;
		}
		GlueNode node = w.node_system.addNode(net.merayen.merasynth.client.output.Glue.class);
		node.getUINode().translation.x = 50f;
	}
}
