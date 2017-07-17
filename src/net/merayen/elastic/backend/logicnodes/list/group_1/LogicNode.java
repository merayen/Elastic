package net.merayen.elastic.backend.logicnodes.list.group_1;

import java.util.Map;

import net.merayen.elastic.backend.nodes.BaseLogicNode;

/**
 * Doesn't do anything, other than having children.
 * @author merayen
 *
 */
public class LogicNode extends BaseLogicNode {
	@Override
	protected void onCreate() {}

	@Override
	protected void onInit() {}

	@Override
	protected void onParameterChange(String key, Object value) {}

	@Override
	protected void onConnect(String port) {}

	@Override
	protected void onDisconnect(String port) {}

	@Override
	protected void onRemove() {}

	@Override
	protected void onPrepareFrame(Map<String, Object> data) {}

	@Override
	protected void onFinishFrame(Map<String, Object> data) {}

	@Override
	protected void onData(Map<String, Object> data) {}

}
