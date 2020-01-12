package net.merayen.elastic.ui.objects.top.easymotion

/**
 * The object that has the EasyMotion class instance.
 * Used by Control instances to detect the nearest EasyMotion-object and register itself.
 */
interface EasyMotionMaster {
	val easyMotion: EasyMotion
}