package net.merayen.elastic.util

import kotlin.reflect.KMutableProperty
import kotlin.reflect.KVisibility
import kotlin.reflect.full.memberProperties

/**
 * Merges one instance of a class into another one of the same type.
 */
class ClassInstanceMerger {
	class MustBeTheSameClass(sourceClass: String, destinationClass: String) : RuntimeException("sourceInstance is '$sourceClass' while destinationClass is '$destinationClass'")

	companion object {
		/**
		 * Merges sourceInstance into destinationInstance.
		 * Only members in sourceInstance that are not get to ignoreValue, will be set on destinationInstance.
		 */
		fun merge(sourceInstance: Any, destinationInstance: Any, ignoreValue: Any? = null) {
			if (sourceInstance::class !== destinationInstance::class)
				throw MustBeTheSameClass(sourceInstance::class.java.name, destinationInstance::class.java.name)

			for (member in sourceInstance::class.memberProperties) {
				if (member.visibility == KVisibility.PUBLIC && !member.isConst) {
					if (member is KMutableProperty<*>) {
						val sourceValue = member.call(sourceInstance)
						if (sourceValue !== ignoreValue)
							member.setter.call(destinationInstance, sourceValue)
					}
				}
			}
		}
	}
}