package net.merayen.elastic.backend.nodes.list.test_100;

import net.merayen.elastic.backend.nodes.BaseLogicNode;

public class LogicNode extends BaseLogicNode {

	@Override
	protected void onCreate() {
		definePort("input", false);
		definePort("output", true);
	}
}
