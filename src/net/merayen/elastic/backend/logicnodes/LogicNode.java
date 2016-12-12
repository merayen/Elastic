package net.merayen.elastic.backend.logicnodes;

import net.merayen.elastic.backend.nodes.BaseLogicNode;

public abstract class LogicNode extends BaseLogicNode {
	protected Environment getEnvironment() {
		return (Environment)env;
	}
}
