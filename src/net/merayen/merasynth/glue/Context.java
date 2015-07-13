package net.merayen.merasynth.glue;

import net.merayen.merasynth.netlist.Supervisor;

public class Context {
	public final net.merayen.merasynth.glue.nodes.Top glue_top;
	public final Supervisor net_supervisor;
	public final net.merayen.merasynth.ui.objects.top.Top top_ui_object; // Topmost object containing everything
	
	public Context() {
		glue_top = new net.merayen.merasynth.glue.nodes.Top(this);
		net_supervisor = new Supervisor();
		top_ui_object = new net.merayen.merasynth.ui.objects.top.Top();
	}
}
