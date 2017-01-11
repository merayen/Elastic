package net.merayen.elastic.util;

import java.util.UUID;

public class NodeUtil {
	private NodeUtil() {}

	public static String getNodeName(String text) {
		return text.split("_")[0];
	}

	public static int getNodeVersion(String text) {
		return Integer.valueOf(text.split("_")[1]);
	}

	public static String createID() {
		return new Integer(UUID.randomUUID().hashCode()).toString();
	}
}
