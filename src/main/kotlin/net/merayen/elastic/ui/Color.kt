package net.merayen.elastic.ui

class Color {
	var red: Float = 0f
	var green: Float = 0f
	var blue: Float = 0f
	var alpha: Float = 1f

	constructor(red: Int, green: Int, blue: Int) {
		this.red = red / 255f
		this.green = green / 255f
		this.blue = blue / 255f
	}

	constructor(red: Int, green: Int, blue: Int, alpha: Int) {
		this.red = red / 255f
		this.green = green / 255f
		this.blue = blue / 255f
		this.alpha = alpha / 255f
	}

	constructor(red: Float, green: Float, blue: Float) {
		this.red = red
		this.green = green
		this.blue = blue
	}

	constructor(red: Float, green: Float, blue: Float, alpha: Float) {
		this.red = red
		this.green = green
		this.blue = blue
		this.alpha = alpha
	}

	constructor()
}