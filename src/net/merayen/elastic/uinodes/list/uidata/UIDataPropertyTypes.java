package net.merayen.elastic.uinodes.list.uidata;

import java.util.List;
import java.util.Map;

/**
 * Represents properties for the UI-windows, like dimension.
 */
public class UIDataPropertyTypes {
	private UIDataPropertyTypes() {}

	public static List<Map<String, Object>> getWindows(Object value) {
		return (List<Map<String, Object>>)value;
	}
}
