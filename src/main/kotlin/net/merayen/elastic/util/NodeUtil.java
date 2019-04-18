package net.merayen.elastic.util;

import java.util.Arrays;
import java.util.UUID;

public class NodeUtil {
	private NodeUtil() {}

	public static String getNodeName(String text) {
		String[] parts = text.split("_");
		return String.join("_", Arrays.copyOf(parts, parts.length - 1));
	}

	public static int getNodeVersion(String text) {
		String[] parts = text.split("_");
		return Integer.valueOf(parts[parts.length - 1]);
	}

	public static String createID() {
		return Integer.toString(UUID.randomUUID().hashCode());
	}
}
