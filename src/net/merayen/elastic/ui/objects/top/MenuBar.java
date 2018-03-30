package net.merayen.elastic.ui.objects.top;

import net.merayen.elastic.ui.UIObject;
import net.merayen.elastic.ui.objects.dialogs.AboutDialog;
import net.merayen.elastic.ui.objects.top.menu.Bar;
import net.merayen.elastic.ui.objects.top.menu.MenuBarItem;
import net.merayen.elastic.ui.objects.top.menu.MenuListItem;

public class MenuBar extends UIObject {
	public static abstract class Handler {
		public void onOpenProject(String path) {}
		public void onMakeCheckpoint() {}
		public void onSaveProjectAs() {}
		public void onClose() {}
	}

	private Bar bar = new Bar(); // The bar on top
	private Handler handler;
	public float width = 500;
	public final float height = 20;

	@Override
	public void onInit() {
		add(bar);
		fillMenuBar();
	}

	@Override
	public void onUpdate() {
		bar.width = width;
	}

	private void fillMenuBar() {
		MenuBarItem file = new MenuBarItem();
		file.label = "File";
		bar.addMenuBarItem(file);

		MenuListItem new_project = new MenuListItem();
		new_project.label = "New project";
		file.menu_list.addMenuItem(new_project);

		MenuListItem open = new MenuListItem();
		open.label = "Open...";
		file.menu_list.addMenuItem(open);
		open.setHandler(new MenuListItem.Handler() {
			@Override
			public void onClick() {
				java.awt.FileDialog fd = new java.awt.FileDialog((java.awt.Frame) null);
				fd.setMode(java.awt.FileDialog.LOAD);
				fd.setTitle("Choose a project");
				fd.setVisible(true);
				if(fd.getFile() != null && handler != null) {
					String path = fd.getDirectory() + fd.getFile();
					handler.onOpenProject(path);
				}
			}
		});

		MenuListItem checkpoint = new MenuListItem();
		checkpoint.label = "Make checkpoint";
		file.menu_list.addMenuItem(checkpoint);
		checkpoint.setHandler(new MenuListItem.Handler() {
			@Override
			public void onClick() {
				if(handler != null)
					handler.onMakeCheckpoint();
			}
		});

		MenuListItem save_as = new MenuListItem();
		save_as.label = "Save as...";
		file.menu_list.addMenuItem(save_as);

		MenuListItem close = new MenuListItem();
		close.label = "Close project";
		close.setHandler(new MenuListItem.Handler() {
			@Override
			public void onClick() {
				if(handler != null)
					handler.onClose();
			}
		});
		file.menu_list.addMenuItem(close);


		MenuBarItem edit = new MenuBarItem();
		edit.label = "Edit";
		bar.addMenuBarItem(edit);

		MenuListItem undo = new MenuListItem();
		undo.label = "Undo";
		edit.menu_list.addMenuItem(undo);

		MenuBarItem hilfe = new MenuBarItem();
		hilfe.label = "Hilfe";
		bar.addMenuBarItem(hilfe);

		MenuListItem about = new MenuListItem();
		about.label = "About Elastic";
		hilfe.menu_list.addMenuItem(about);
		about.setHandler(new MenuListItem.Handler() {
			@Override
			public void onClick() {
				add(new AboutDialog());
			}
		});
	}

	public void setHandler(Handler handler) {
		this.handler = handler;
	}
}
