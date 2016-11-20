package net.merayen.elastic.backend.architectures.local.nodes.output_1;

import net.merayen.elastic.backend.architectures.local.LocalNode;

public class LNode extends LocalNode {
	public LNode() {
		super(LProcessor.class);
	}

	@Override
	protected void onInit() {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected void onProcess() {
		System.out.println("Output " + getID() + " is processing");
	}

	@Override
	protected void onParameter(String key, Object value) {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		
	}
}
