package net.merayen.elastic.backend.logicnodes.list.uidata;

import java.util.Map;

import net.merayen.elastic.backend.nodes.BaseLogicNode;

/**
 * Generic data storage for the user interface. Really? It will be stored in the revision history, which is something we do not want?
 * Stores stuff like window size, panel type etc.
 * Can contain data from different type of UIs at once.
 * All data is stored in node.parameters, so any updates are done via NodeParameterMessage().
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
	protected void onData(Object data) {}

}
