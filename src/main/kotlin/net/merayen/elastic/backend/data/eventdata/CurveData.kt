package net.merayen.elastic.backend.data.eventdata

class CurveData : Cloneable {
	class Lane(
			val id: String,
			val type: Type
	) : Cloneable {

		enum class Type {
			/**
			 * Beats per minute
			 */
			BPM,

			/**
			 * Midi channel volume
			 */
			VOLUME

		}

		class Point(
				val leftPoint: FloatArray,
				val middlePoint: FloatArray,
				val rightPoint: FloatArray
		) : Cloneable {
			public override fun clone() = Point(leftPoint.clone(), middlePoint.clone(), rightPoint.clone())
		}

		val list = ArrayList<Point>()

		fun merge(curves: Point) {
			// TODO implement curve merging, when it becomes necessary
			TODO()
		}

		fun cleanUp() {
			list.sortBy { it.middlePoint[0] }
			TODO()
			// TODO clamp handles (left and right point) so that they don't reach over each other
		}

		private val points = ArrayList<Point>()

		public override fun clone(): Lane {
			val result = Lane(id, type)
			result.points.addAll(points.map { it.clone() })
			return result
		}

	}

	private val lanes = HashMap<Lane.Type, Lane>()

	public override fun clone(): CurveData {
		val result = CurveData()
		result.lanes.putAll(lanes.map { Pair(it.key, it.value.clone()) })
		return result
	}
}