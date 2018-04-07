package net.merayen.elastic.ui.objects.top.views.filebrowserview;

import java.io.File;
import java.util.ArrayList;

import net.merayen.elastic.ui.UIObject;
import net.merayen.elastic.ui.objects.components.Scroll;
import net.merayen.elastic.ui.objects.components.autolayout.AutoLayout;
import net.merayen.elastic.ui.objects.components.autolayout.LayoutMethods;

class FileList extends UIObject {
	interface Handler {
		public void onSelect(File file);
	}

	float width = 100;
	float height = 100;

	private class FList extends AutoLayout {
		private File[] current_files;

		FList() {
			super(new LayoutMethods.HorizontalBox(10));
		}

		@Override
		public void onUpdate() {
			((LayoutMethods.HorizontalBox) placement).setMaxWidth(width);

			if(files != current_files) {
				for(UIObject obj : new ArrayList<>(getSearch().getChildren()))
					remove(obj);

				add(new FileListItem() {{
					label = ".. (go back)";
					setHandler(() -> {
						if(handler != null)
							handler.onSelect(null);
					});
				}});

				if(files != null) {
					for(File f : files) {
						add(new FileListItem() {{
							label = f.getName();
							setHandler(() -> {
								if(handler != null)
									handler.onSelect(f);
							});
						}});
					}
				}

				current_files = files;
			}

			super.onUpdate();
		}
	}

	private final FList flist = new FList();
	private final Scroll scroll = new Scroll(flist);
	private File[] files;
	private String path;
	private Handler handler;

	@Override
	public void onInit() {
		add(scroll);
	}

	@Override
	public void onUpdate() {
		scroll.setLayoutHeight(height);
		scroll.setLayoutWidth(width);
	}

	void browse(String path) {
		this.path = path;
		files = new File(path).listFiles();
	}

	void setHandler(Handler handler) {
		this.handler = handler;
	}

	void up() {
		File parent = new File(path).getParentFile();
		if(parent != null)
			browse(parent.getAbsolutePath());
	}
}
