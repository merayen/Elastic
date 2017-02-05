package net.merayen.elastic.util;

import java.util.UUID;

public class UniqueID {
	public static String create() {
		return new Integer(UUID.randomUUID().hashCode()).toString();
	}
}
