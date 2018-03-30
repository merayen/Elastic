package net.merayen.elastic.ui.objects.top.views.filebrowserview;

import java.io.File;

import net.merayen.elastic.ui.objects.top.views.View;

public class FileBrowserView extends View {
	private FileBrowserViewBar bar = new FileBrowserViewBar();
	private FileList file_list = new FileList();

	@Override
	public void onInit() {
		super.onInit();
		add(file_list);
		file_list.browse("/");
		file_list.getTranslation().x = 0;
		file_list.getTranslation().y = 20;
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
	public void onUpdate() {
		file_list.width = width - 20;
		file_list.height = height;
	}

	@Override
	public View cloneView() {
		return new FileBrowserView();
	}

}
