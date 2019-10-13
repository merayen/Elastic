package net.merayen.elastic.uinodes.list.sample_1

import net.merayen.elastic.ui.Draw
import net.merayen.elastic.ui.FlexibleDimension
import net.merayen.elastic.ui.UIObject
import net.merayen.elastic.ui.event.MouseEvent
import net.merayen.elastic.ui.event.UIEvent
import net.merayen.elastic.ui.util.MouseHandler
import net.merayen.elastic.util.MutablePoint

class SampleWaveBoxZones(private val handler: Handler) : UIObject(), FlexibleDimension {
	interface Handler {
		fun onZoneSelect(zone: SampleWaveZone?)
		fun onZoneChange(zone: SampleWaveZone)
	}

	override var layoutWidth = 0f
	override var layoutHeight = 0f

	private val mouse = MouseHandler(this, MouseEvent.Button.LEFT)

	private val zones = ArrayList<SampleWaveZone>()
	private var selectedZone: SampleWaveZone? = null

	private var pointX: Float? = null

	override fun onInit() {
		mouse.setHandler(object : MouseHandler.Handler() {
			override fun onMouseMove(position: MutablePoint?) {
				pointX = position!!.x / layoutWidth
			}

			override fun onMouseOut() {
				pointX = null
			}

			override fun onMouseDown(position: MutablePoint?) {
				val pointX = pointX
				if(pointX != null) {
					lateinit var sampleWaveZone: SampleWaveZone

					sampleWaveZone = SampleWaveZone(object : SampleWaveZone.Handler {
						override fun onChange() {
							handler.onZoneChange(sampleWaveZone)
						}

						override fun onSelect() = focusZone(sampleWaveZone)
					})

					sampleWaveZone.start = pointX
					zones.add(sampleWaveZone)
					add(sampleWaveZone)
					focusZone(sampleWaveZone)
				}
			}

			override fun onMouseDrag(position: MutablePoint?, offset: MutablePoint?) {
				zones[zones.size - 1].stop = zones[zones.size - 1].start + offset!!.x / layoutWidth
				handler.onZoneChange(zones[zones.size - 1])
			}
		})
	}

	override fun onUpdate() {
		for(zone in zones) {
			zone.layoutWidth = layoutWidth
			zone.layoutHeight = layoutHeight
		}
	}

	override fun onDraw(draw: Draw) {
		draw.empty(0f, 0f, layoutWidth, layoutHeight)

		val pointX = pointX
		if(pointX != null) {
			draw.setColor(200, 200, 200)
			draw.setStroke(1f)
			draw.line(pointX * layoutWidth, 0f, pointX * layoutWidth, layoutHeight)
		}
	}

	override fun onEvent(event: UIEvent) {
		mouse.handle(event)
	}

	fun removeZone(zone: SampleWaveZone) {
		remove(zone)
		zones.remove(zone)
	}

	private fun focusZone(zone: SampleWaveZone) {
		for(z in zones)
			z.focus = false

		zone.focus = true
		selectedZone = zone

		handler.onZoneSelect(zone)
	}
}