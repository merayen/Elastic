package net.merayen.merasynth.ui.objects.top;

import net.merayen.merasynth.ui.objects.Group;
import net.merayen.merasynth.ui.objects.components.Label;
import net.merayen.merasynth.ui.objects.dialogs.AboutDialog;
import net.merayen.merasynth.ui.objects.top.menu.Bar;
import net.merayen.merasynth.ui.objects.top.menu.MenuBarItem;
import net.merayen.merasynth.ui.objects.top.menu.MenuListItem;

public class TopMenuBar extends Group {
	private Bar menu = new Bar(); // The menu always displayed at top

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

		MenuListItem save = new MenuListItem();
		save.label = "Save";
		file.menu_list.addMenuItem(save);

		MenuListItem save_as = new MenuListItem();
		save_as.label = "Save as...";
		file.menu_list.addMenuItem(save_as);

		MenuListItem quit = new MenuListItem();
		quit.label = "Quit";
		quit.setHandler(new MenuListItem.Handler() {
			@Override
			public void onClick() {
				System.out.println("Rage quitting");
			}
		});
		file.menu_list.addMenuItem(quit);


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
		logo.font_size = 1f;
		logo.translation.x = 1f;
		logo.translation.y = 98f;
		add(logo);
	}
}
