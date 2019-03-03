package net.merayen.elastic.ui.objects.top.views.arrangementview

class ArrangementData {
	class TrackData(type: Type) {
		enum class Type {
			MIDI, AUDIO
		}
		class EventData {
			class Region(var start: Float, var length: Float)

			val regions = ArrayList<Region>()
		}

		val events = ArrayList<EventData>()
	}

	val tracks = ArrayList<TrackData>()
}