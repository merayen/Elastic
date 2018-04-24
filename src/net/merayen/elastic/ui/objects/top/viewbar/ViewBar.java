package net.merayen.elastic.ui.objects.top.viewbar;

import net.merayen.elastic.ui.Draw;
import net.merayen.elastic.ui.UIObject;
import net.merayen.elastic.ui.event.UIEvent;
import net.merayen.elastic.ui.intercom.EditNodeMessage;
import net.merayen.elastic.ui.objects.components.autolayout.AutoLayout;
import net.merayen.elastic.ui.objects.components.autolayout.LayoutMethods;
import net.merayen.elastic.ui.objects.components.dragdrop.TargetItem;
import net.merayen.elastic.ui.objects.node.EditNodeMouseCarryItem;
import net.merayen.elastic.ui.objects.node.INodeEditable;
import net.merayen.elastic.ui.objects.node.UINode;
import net.merayen.elastic.ui.objects.top.megamenu.MegaMenu;
import net.merayen.elastic.ui.objects.top.mouse.InterestedObjectDecoration;
import net.merayen.elastic.ui.objects.top.mouse.MouseCarryItem;
import net.merayen.elastic.ui.objects.top.views.View;
import net.merayen.elastic.ui.objects.top.views.editview.EditNodeView;
import org.jetbrains.annotations.NotNull;

public class ViewBar extends AutoLayout {
	public ViewBar() {
		super(new LayoutMethods.HorizontalBox(2, 100000));
	}

	public float width;
	private final float height = 20;
	private final MegaMenu menu = new MegaMenu();
	protected final UIObject content = new UIObject();
	private TargetItem targetItem;
	private boolean interested;

	@Override
	public void onInit() {
		add(menu);
		add(content);

		targetItem = new TargetItem(this) {
			@Override
			public void onDrop(@NotNull MouseCarryItem item) {
				if(item instanceof EditNodeMouseCarryItem) {
					UINode node = ((EditNodeMouseCarryItem)item).getNode();

					if(node instanceof INodeEditable) {
						EditNodeView editNodeView = getSearch().parentByType(View.class).swap(EditNodeView.class);
						editNodeView.editNode((INodeEditable) node);
					}
				}
			}

			@Override
			public void onHover(@NotNull MouseCarryItem item) {
				System.out.println("Oh, come here!");
			}

			@Override
			public void onBlurInterest() {
				//System.out.println("Oh, oh well");
				interested = false;
			}

			@Override
			public void onInterest(@NotNull MouseCarryItem item) {
				System.out.println("Interested!!!");
				interested = true;
			}

			@Override
			public void onBlur() {
				//System.out.println("Come on! You just hovered me!");
			}
		};
	}

	@Override
	public void onDraw(Draw draw) {
		draw.setColor(100, 100, 100);
		draw.fillRect(0, 0, width, height);
		draw.setColor(0, 0, 0);
		draw.setStroke(1);
		draw.line(0, height, width, height);

		if(interested) {
			draw.setColor(255,0,255);
			draw.fillRect(0, 0, width, height);
		}
	}

	@Override
	public void onEvent(@NotNull UIEvent e) {
		super.onEvent(e);
		targetItem.handle(e);
	}
}