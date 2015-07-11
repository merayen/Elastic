package net.merayen.merasynth.glue.nodes;

import net.merayen.merasynth.glue.Context;

public class GlueNode extends GlueObject {
	private String net_node_id;
	private String ui_node_id;
	
	public GlueNode(Context context) {
		super(context);
	}
	
	public net.merayen.merasynth.netlist.Node getNetNode() {
		return context.supervisor.getNodeByID(net_node_id);
	}

	public net.merayen.merasynth.netlist.Node getUINode() {
		return null; // TODO
	}
	
	public void setNetNode(net.merayen.merasynth.netlist.Node net_node) {
		if(net_node_id != null)
			throw new RuntimeException("UI node is already set");
		
		net_node_id = net_node.getID();
	}
	
	public void setUINode(net.merayen.merasynth.ui.objects.node.Node ui_node) {
		if(ui_node_id != null)
			throw new RuntimeException("UI node is already set");
		
		ui_node_id = ui_node.getID();
	}
}
