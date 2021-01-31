package net.merayen.elastic.ui.objects.top.viewbar

import net.merayen.elastic.ui.Draw
import net.merayen.elastic.ui.FlexibleDimension
import net.merayen.elastic.ui.UIObject
import net.merayen.elastic.ui.event.UIEvent
import net.merayen.elastic.ui.objects.components.autolayout.AutoLayout
import net.merayen.elastic.ui.objects.components.autolayout.LayoutMethods
import net.merayen.elastic.ui.objects.components.dragdrop.TargetItem
import net.merayen.elastic.ui.objects.node.EditNodeMouseCarryItem
import net.merayen.elastic.ui.objects.node.INodeEditable
import net.merayen.elastic.ui.objects.top.mouse.MouseCarryItem
import net.merayen.elastic.ui.objects.top.views.View
import net.merayen.elastic.ui.objects.top.views.editview.EditNodeView
import kotlin.reflect.KClass

open class ViewBar(private val viewClass: KClass<out View>) : AutoLayout<LayoutMethods.HorizontalBox>(LayoutMethods.HorizontalBox(2f, 100000f)), FlexibleDimension {
	override var layoutWidth = 0f
	override var layoutHeight = 40f

	private val menu = ViewSelector(object : ViewSelector.Handler {
		override fun onSelect(cls: KClass<out View>) {
			search.parentByType(View::class.java)!!.swap(cls)
		}
	})

	protected val content = UIObject()
	private var targetItem: TargetItem? = null
	private var interested: Boolean = false

	override fun onInit() {
		menu.setViewClass(viewClass)
		add(menu)
		add(content)

		targetItem = object : TargetItem(this) {
			override fun onDrop(item: MouseCarryItem) {
				if (item is EditNodeMouseCarryItem) {
					val node = item.node

					if (node is INodeEditable) {
						val editNodeView = search.parentByType(View::class.java)!!.swap(EditNodeView::class)
						editNodeView.editNode(node as INodeEditable)
					}
				}
			}

			override fun onHover(item: MouseCarryItem) {}

			override fun onBlurInterest() {
				interested = false
			}

			override fun onInterest(item: MouseCarryItem) {
				interested = true
			}

			override fun onBlur() {}
		}
	}

	override fun onDraw(draw: Draw) {
		draw.setColor(100, 100, 100)
		draw.fillRect(0f, 0f, layoutWidth, layoutHeight)
		draw.setColor(0, 0, 0)
		draw.setStroke(1f)
		draw.line(0f, layoutHeight, layoutWidth, layoutHeight)

		if (interested) {
			draw.setColor(255, 0, 255)
			draw.fillRect(0f, 0f, layoutWidth, layoutHeight)
		}
	}

	override fun onEvent(event: UIEvent) {
		super.onEvent(event)
		targetItem!!.handle(event)
	}
}