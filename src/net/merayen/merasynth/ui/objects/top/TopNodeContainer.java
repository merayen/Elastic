package net.merayen.merasynth.ui.objects.top;

import net.merayen.merasynth.ui.objects.Group;
import net.merayen.merasynth.ui.objects.Net;

public class TopNodeContainer extends Group {
	private Net net;
	
	protected void onInit() {
		net = new Net();
		add(net, true); // Add the net first (also, drawn behind everything)
	}
}
