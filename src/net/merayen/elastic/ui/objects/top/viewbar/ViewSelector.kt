package net.merayen.elastic.ui.objects.top.viewbar

import net.merayen.elastic.ui.UIObject
import net.merayen.elastic.ui.objects.components.Tabs
import net.merayen.elastic.ui.objects.components.Tabs.Tab
import net.merayen.elastic.ui.objects.components.autolayout.LayoutMethods
import net.merayen.elastic.ui.objects.top.views.View
import net.merayen.elastic.ui.objects.top.views.arrangementview.ArrangementView
import net.merayen.elastic.ui.objects.top.views.editview.EditView
import net.merayen.elastic.ui.objects.top.views.midiview.MidiView
import net.merayen.elastic.ui.objects.top.views.nodeview.NodeView
import net.merayen.elastic.ui.objects.top.views.splashview.SplashView
import net.merayen.elastic.ui.objects.top.views.transportview.TransportView

class ViewSelector(handler: Handler) : UIObject() {
	private val tabs: Tabs
	private val NODE_VIEW = SelectorButton("Nodes", NodeView::class.java)
	private val EDIT_VIEW = SelectorButton("Editor", EditView::class.java)
	private val MIDI_VIEW = SelectorButton("Midi", MidiView::class.java)
	private val TRANSPORT_VIEW = SelectorButton("Transport", TransportView::class.java)
	private val ARRANGEMENT_VIEW = SelectorButton("Arrangement", ArrangementView::class.java)
	private val TEST_VIEW = SelectorButton("Splash", SplashView::class.java)

	interface Handler {
		fun onSelect(cls: Class<out View>)
	}

	private class SelectorButton internal constructor(text: String, internal var view: Class<out View>) : Tabs.TextButton() {
		init {
			setText(text, 10f)
		}
	}

	init {
		tabs = Tabs(LayoutMethods.HorizontalBox(2f, 100000f), Tabs.Handler { tab -> handler.onSelect((tab as SelectorButton).view) })
	}

	override fun onInit() {
		tabs.translation.x = 2f
		tabs.translation.y = 2f
		add(tabs)

		tabs.add(NODE_VIEW)
		tabs.add(EDIT_VIEW)
		tabs.add(MIDI_VIEW)
		tabs.add(TRANSPORT_VIEW)
		tabs.add(ARRANGEMENT_VIEW)
		tabs.add(TEST_VIEW)
	}
}
