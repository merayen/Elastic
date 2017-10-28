package net.merayen.elastic.ui.objects.top.views.filebrowserview;

import java.io.File;

import net.merayen.elastic.ui.objects.top.views.View;

public class FileBrowserView extends View {
	private FileBrowserViewBar bar = new FileBrowserViewBar();
	private FileList file_list = new FileList();

	@Override
	protected void onInit() {
		super.onInit();
		add(file_list);
		file_list.browse("/");
		file_list.translation.x = 0;
		file_list.translation.y = 20;
		file_list.setHandler(new FileList.Handler() {
			
			@Override
			public void onSelect(File file) {
				if(file == null)
					file_list.up();
				else if(file.isDirectory())
					file_list.browse(file.getAbsolutePath());
			}
		});

		add(bar);
	}

	@Override
	protected void onUpdate() {
		file_list.width = width - 20;
		file_list.height = height;
	}

	@Override
	public View cloneView() {
		return new FileBrowserView();
	}

}
