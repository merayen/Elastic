package net.merayen.elastic.ui

/**
 * Adds layoutWidth- and layoutHeight-properties on UIObject, allowing owner of the UIObject to change its dimensions.
 */
interface FlexibleDimension {
	var layoutWidth: Float
	var layoutHeight: Float
}
