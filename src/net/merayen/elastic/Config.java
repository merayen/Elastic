package net.merayen.elastic;

public class Config {
	public static class ui {
		public static class debug {
			public final static boolean messages = false;
		}
	}

	public static class processor {
		public static class debug {
			public static boolean messages;
			public static boolean verbose;
		}
	}
}
