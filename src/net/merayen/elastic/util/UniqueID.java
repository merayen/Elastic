package net.merayen.elastic.util;

import java.util.Random;

public class UniqueID {
	private final static byte[] characters = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789".getBytes();

	public static String create() {
		byte[] bytes = new byte[16];

		new Random().nextBytes(bytes);

		for(int i = 0; i < bytes.length; i++)
			bytes[i] = characters[(bytes[i] + 128) % characters.length];

		return new String(bytes);
	}
}
