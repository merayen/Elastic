package net.merayen.elastic.backend.architectures.llvm.ctools

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

internal class ParserKtTest {
	@Test
	fun `parse c code`() {
		val parse = parse("""
			#include <somefile.h>
			#include "anotherfile.h"
			
			#define something this
			
			struct something {
				int yay;
				int *noes;
			}
			
			void func(int arg, char* wow) {
				if (arg == 2) do_something();
				if (arg == 1)
					do_other();

				if (arg == 0) { should_be_fixed();
			should_be_indented(); this_too();
			}
			}
		""")
	}
}