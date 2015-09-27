package net.merayen.merasynth;

import net.merayen.merasynth.glue.nodes.GlueNode;

public class Main {
	public static void main(String jhgjhg[]) {
		WindowManager wm = new WindowManager();
		Window w = wm.create();

		GlueNode node;

		for(int i = 4; i < 5; i++ ) {
			node = w.node_system.addNode(net.merayen.merasynth.client.signalgenerator.Glue.class);
			node.getUINode().translation.x = 10f + i * 10f;
			node.getUINode().translation.y = 10f + i * 10f;
		}
		node = w.node_system.addNode(net.merayen.merasynth.client.output.Glue.class);
		node.getUINode().translation.x = 50f;

		node = w.node_system.addNode(net.merayen.merasynth.client.midi_input.Glue.class);
		node.getUINode().translation.x = 30f;
		node.getUINode().translation.y = 20f;

		node = w.node_system.addNode(net.merayen.merasynth.client.vu.Glue.class);
		node.getUINode().translation.x = 5f;
		node.getUINode().translation.y = 5f;

		/*node = w.node_system.addNode(net.merayen.merasynth.client.graph.Glue.class);
		node.getUINode().translation.x = 2f;
		node.getUINode().translation.y = 50f;*/

		node = w.node_system.addNode(net.merayen.merasynth.client.ui_test.Glue.class);
		node.getUINode().translation.x = 100f;
		node.getUINode().translation.y = 100f;
	}
}
