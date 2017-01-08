package net.merayen.elastic.util;

public class NodeUtil {
	private NodeUtil() {}

	public static String getNodeName(String text) {
		return text.split("_")[0];
	}

	public static int getNodeVersion(String text) {
		return Integer.valueOf(text.split("_")[1]);
	}
}
