package net.merayen.elastic.util

/**
 * Classes inheriting this inteface has a revision counter.
 * Other classes can then retrieve this number and compare it with its local revision of that remove class.
 * This allows classes to react on other classes when they change.
 */
interface Revision {
	val revision: Int
}