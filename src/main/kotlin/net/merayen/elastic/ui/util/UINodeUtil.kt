package net.merayen.elastic.ui.util

import net.merayen.elastic.ui.UIObject
import net.merayen.elastic.ui.objects.top.Top
import net.merayen.elastic.ui.objects.top.Window

object UINodeUtil {

	/**
	 * Retrieves the Window() UIObject the UIObject is a child of.
	 */
	fun getWindow(uiobject: UIObject): Window? {
		return uiobject.search.parentByType(Window::class.java)
	}

	fun getTop(uiobject: UIObject): Top? {
		val result = uiobject.search.top
		return if (result is Top)
			result
		else
			null
	}
}
