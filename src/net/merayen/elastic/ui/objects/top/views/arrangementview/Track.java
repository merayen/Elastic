package net.merayen.elastic.ui.objects.top.views.arrangementview;

import net.merayen.elastic.ui.UIObject;

class Track extends UIObject {
	interface Handler {
		public void onRemove();
	}

	float width, height = 100;

	private TrackHeader header = new TrackHeader();
	private TrackBody body = new TrackBody();
	private Handler handler;

	@Override
	protected void onInit() {
		add(header);
		add(body);
		body.translation.x = 100;

		header.setHandler(new TrackHeader.Handler() {
			@Override
			public void onRemove() {
				if(handler != null)
					handler.onRemove();
			}
		});
	}

	@Override
	protected void onDraw() {
		draw.setColor(0, 255, 0);
		draw.setStroke(1);
		draw.rect(0, 0, width, height);
	}

	@Override
	protected void onUpdate() {
		header.width = 100;
		header.height = height;
		body.width = width - 100;
		body.height = height;
	}

	@Override
	public float getWidth() {
		return width;
	}

	public float getHeight() {
		return height;
	}

	void setHandler(Handler handler) {
		this.handler = handler;
	}
}
