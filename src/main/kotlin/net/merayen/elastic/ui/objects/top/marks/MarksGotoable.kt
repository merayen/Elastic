package net.merayen.elastic.ui.objects.top.marks

interface MarksGotoable {
	/**
	 * Try to go to this markString.
	 * Return false if not possible.
	 */
	fun isGotoAble(markString: String): Boolean
}