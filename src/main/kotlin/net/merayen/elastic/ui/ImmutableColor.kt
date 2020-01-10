package net.merayen.elastic.ui

class ImmutableColor(
	private val _red: Float,
	private val _green: Float,
	private val _blue: Float,
	private val _alpha: Float
) : Color {

	override var red: Float
		get() = _red
		set(_) = throw UnsupportedOperationException()

	override var green: Float
		get() = _green
		set(_) = throw UnsupportedOperationException()
	override var blue: Float
		get() = _blue
		set(_) = throw UnsupportedOperationException()

	override var alpha: Float
		get() = _alpha
		set(_) = throw UnsupportedOperationException()
}