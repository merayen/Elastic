package net.merayen.merasynth;

public class Window {
	public static abstract class Handler {
		public void onClose() {}
	}
	public final NodeSystem node_system = new NodeSystem();
	private Handler handler;

	public Window() {
		node_system.setHandler(new NodeSystem.Handler() {
			@Override
			public void onClose() {
				node_system.end();
				if(handler != null)
					handler.onClose();
			}
		});
	}

	public void setHandler(Handler handler) {
		this.handler = handler;
	}
}
