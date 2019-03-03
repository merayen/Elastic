package net.merayen.elastic.ui.objects.top.views.arrangementview

import net.merayen.elastic.system.intercom.CreateNodeMessage
import net.merayen.elastic.ui.UIObject
import net.merayen.elastic.ui.objects.components.Button
import net.merayen.elastic.ui.objects.components.Scroll
import net.merayen.elastic.ui.objects.components.autolayout.AutoLayout
import net.merayen.elastic.ui.objects.components.autolayout.LayoutMethods
import net.merayen.elastic.util.Postmaster

internal class Arrangement : UIObject() {
	var layoutWidth: Float = 0f
	var layoutHeight: Float = 0f

	private val arrangementData = ArrangementData()

	private val trackList = TrackList()
	private val arrangementEventView = ArrangementEventView()
	private val arrangementListScroll = Scroll(arrangementEventView)
	private val buttonBar = AutoLayout(LayoutMethods.HorizontalBox(5f, 100000f))

	override fun onInit() {
		add(arrangementListScroll)
		add(buttonBar)
		add(trackList)

		trackList.translation.y = 20f

		arrangementListScroll.translation.x = 100f
		arrangementListScroll.translation.y = 20f

		buttonBar.add(object : Button() {
			init {
				label = "New track"
				handler = object : Button.IHandler {
					override fun onClick() {
						val track = Track()
						trackList.add(track)
						track.handler = object : Track.Handler {
							override fun onRemove() = trackList.remove(track)
						}
					}
				}
			}
		})
	}

	override fun onUpdate() {
		trackList.layoutWidth = layoutWidth
		arrangementListScroll.layoutWidth = layoutWidth
		arrangementListScroll.layoutHeight = layoutHeight - 20

		arrangementEventView.layoutWidth = layoutWidth - 100
		arrangementEventView.layoutHeight = layoutHeight - 20
	}

	fun handleMessage(message: Postmaster.Message) {
		when (message) {
			is CreateNodeMessage -> {
				if (message.name == "midi") {
					arrangementEventView.handleMessage(message)
				}
			}
		}
	}
}