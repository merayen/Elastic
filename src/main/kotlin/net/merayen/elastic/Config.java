package net.merayen.elastic;

public class Config {
	public static class ui {
		public static class debug {
			public static boolean messages = false;
			public static boolean overlay = false;
		}
	}

	public static class processor {
		public static class debug {
			public static boolean messages;
			public static boolean verbose;
			public static boolean performance = false;
		}
	}
}
