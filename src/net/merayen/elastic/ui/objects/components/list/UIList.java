package net.merayen.elastic.ui.objects.components.list;

import java.util.ArrayList;
import java.util.List;

import net.merayen.elastic.ui.UIObject;
import net.merayen.elastic.ui.objects.UIClip;

public class UIList extends UIObject {
	public float width = 100;
	public float height = 100;

	private class Content extends UIClip {
		private UIList uilist;

		private Content(UIList uilist) {
			this.uilist = uilist;
		}

		@Override
		protected void onUpdate() {
			width = uilist.width;
			height = uilist.height;
		}
	}

	@SuppressWarnings("serial")
	private static class Row extends ArrayList<UIListItem> {}

	private final List<Row> items = new ArrayList<>();

	private final Content content = new Content(this);

	@Override
	protected void onInit() {
		add(content);
	}

	@Override
	protected void onUpdate() {
		placeItems();
	}

	/**
	 * Adds an item to the list. Will add a new row if row == count of rows.
	 * Will add item horizontally if row already exists.
	 */
	public void addItem(int row, UIListItem item) {
		if(row > items.size())
			throw new RuntimeException("Row " + row + " does not exist and there are no items between this one and the last one");

		if(row < 0)
			throw new RuntimeException("Negative row index");

		if(row == items.size())
			items.add(new Row());

		items.get(row).add(item);
		content.add(item); // TODO add and remove automatically
	}

	public int getRowCount() {
		return items.size();
	}

	public int getColumnCount(int row) {
		return items.get(row).size();
	}

	public List<UIListItem> getItems() {
		List<UIListItem> result = new ArrayList<>();

		for(Row row : items)
			for(UIListItem item : row)
				result.add(item);

		return result;
	}

	/**
	 * Reflows the items in the list vertically.
	 * @param max maximum item count allowed horizontally
	 */
	public void reflowVertically(int max) {
		List<UIListItem> to_reflow = getItems();
		items.clear();
		items.add(new Row());

		int count = max + 1;
		for(UIListItem item : to_reflow) {
			if(count++ >= max) {
				count = 0;
				items.add(new Row());
			}

			items.get(items.size() - 1).add(item);
		}
	}

	private void placeItems() {
		float y = 0;
		for(Row row : items) {

			float x = 0, height = 0;
			for(UIListItem item : row) {
				item.translation.x = x;
				item.translation.y = y;

				x += item.width;
				height = Math.max(item.height, height);
			}

			y += height;
		}
	}
}
