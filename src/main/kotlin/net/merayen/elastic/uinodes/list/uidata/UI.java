package net.merayen.elastic.uinodes.list.uidata;

import java.util.List;
import java.util.Map;

import net.merayen.elastic.system.intercom.NodeDataMessage;
import net.merayen.elastic.system.intercom.NodeParameterMessage;
import net.merayen.elastic.ui.objects.node.UINode;
import net.merayen.elastic.ui.objects.node.UIPort;

/**
 * Only purpose of this node is to store data that is related to the UI.
 * It is by no means used by the audio processing itself.
 * For each group, this node is present. It is an error if not.
 * 
 * Every UI has one UIData where it takes all the windows and sizes from.
 */
public class UI extends UINode {

	@Override
	protected void onCreatePort(UIPort port) {}

	@Override
	protected void onRemovePort(UIPort port) {}

	@Override
	protected void onMessage(NodeParameterMessage message) {
		if(message instanceof NodeParameterMessage && isRepresentingUI()) {
			String key = ((NodeParameterMessage)message).key;
			Object value = ((NodeParameterMessage)message).value;

			if(key.equals("ui.default.windows")) {
				List<Map<String, Object>> data = UIDataPropertyTypes.getWindows(value);

				// Only support for single window at the moment
				Map<String, Object> window_properties = data.get(0);
				System.out.println(window_properties);
			};
		}
	}

	@Override
	protected void onData(NodeDataMessage message) {}

	/**
	 * Figures out if this UIData-node represents the UI.
	 */
	private boolean isRepresentingUI() {
		// TODO
		return false;
	}

	@Override
	protected void onParameter(String key, Object value) {}
}
