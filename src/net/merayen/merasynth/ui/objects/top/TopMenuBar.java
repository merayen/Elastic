package net.merayen.merasynth.ui.objects.top;

import net.merayen.merasynth.exceptions.Quit;
import net.merayen.merasynth.ui.objects.UIGroup;
import net.merayen.merasynth.ui.objects.components.Label;
import net.merayen.merasynth.ui.objects.dialogs.AboutDialog;
import net.merayen.merasynth.ui.objects.top.menu.Bar;
import net.merayen.merasynth.ui.objects.top.menu.MenuBarItem;
import net.merayen.merasynth.ui.objects.top.menu.MenuListItem;

public class TopMenuBar extends UIGroup {
	public static abstract class Handler {
		public void onOpenProject(String path) {}
		public void onSaveProject() {}
		public void onSaveProjectAs() {}
		public void onClose() {}
	}

	private Bar menu = new Bar(); // The menu always displayed at top
	private Handler handler;

	protected void onInit() {
		add(menu);
		menu.width = 100f; // Since our scale_x is 100, this one is 100
		fillMenuBar();
	}

	private void fillMenuBar() {
		MenuBarItem file = new MenuBarItem();
		file.label = "File";
		menu.addMenuBarItem(file);

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

		MenuListItem save = new MenuListItem();
		save.label = "Save";
		file.menu_list.addMenuItem(save);
		save.setHandler(new MenuListItem.Handler() {
			@Override
			public void onClick() {
				if(handler != null)
					handler.onSaveProject();
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
		menu.addMenuBarItem(edit);

		MenuListItem undo = new MenuListItem();
		undo.label = "Undo";
		edit.menu_list.addMenuItem(undo);

		MenuBarItem hilfe = new MenuBarItem();
		hilfe.label = "Hilfe";
		menu.addMenuBarItem(hilfe);

		MenuListItem about = new MenuListItem();
		about.label = "About MeraSynth";
		hilfe.menu_list.addMenuItem(about);
		about.setHandler(new MenuListItem.Handler() {
			@Override
			public void onClick() {
				add(new AboutDialog());
			}
		});

		Label logo = new Label();
		logo.label = "MeraSynth";
		logo.font_size = 1.3f;
		logo.translation.x = 0.5f;
		logo.translation.y = 96f;
		add(logo);
	}

	public void setHandler(Handler handler) {
		this.handler = handler;
	}
}
