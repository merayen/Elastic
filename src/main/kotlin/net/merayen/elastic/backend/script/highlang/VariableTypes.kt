package net.merayen.elastic.backend.script.highlang

enum class VariableTypes(val value: String) {
	FP16("fp16"),
	FP32("fp32"),
	FP64("fp64"),
	INT8("int8"),
	INT16("int16"),
	INT32("int32"),
	INT64("int64"),
	FUNCTION("function"),
	MODULE("module"),
	YIELD("yield"); // Only used for functions used in for-loop

	companion object {
		fun getByValue(value: String): VariableTypes? {
			for (x in enumValues<VariableTypes>())
				if (x.value == value)
					return x

			return null
		}
	}
}