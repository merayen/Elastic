package net.merayen.elastic.backend.architectures.llvm

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import java.nio.ByteOrder

internal class LLVMCommunicatorTest {
	val endian = ByteOrder.nativeOrder()

	@Test
	fun `spawn and communicate with process`() {
		val backend = LLVMBackend("""
			#include <stdio.h>
			#include <stdlib.h>
			#include <string.h>

			int main() {
				FILE *result = freopen(NULL, "rb", stdin);
				if (result == NULL)
					return 1;

				result = freopen(NULL, "wb", stdout);
				if (result == NULL)
					return 1;

				char hello[] = {'H','E','L','L','O'};
				fwrite(hello, 1, sizeof hello, stdout);

				fflush(stdout); // Not needed?

				char buf[4 + 3];

				fread(buf, 1, 4 + 3, stdin);

				char* ut = malloc(4 + 4);

				(*ut) = (int)4;

				if (buf[4] == 10 && buf[5] == 11 && buf[6] == 12) {
					ut[4] = 'H';
					ut[5] = 'e';
					ut[6] = 'i';
					ut[7] = '!';
				} else {
					ut[4] = 'N';
					ut[5] = 'e';
					ut[6] = 'i';
					ut[7] = '!';
				}
					
				fwrite(ut, 1, 8, stdout);

				return 0;
			}
		""".trimIndent())

		val com = LLVMCommunicator(backend)

		com.send(byteArrayOf(10, 11, 12))

		val result = com.poll()

		assertEquals('H'.toByte(), result.get())
		assertEquals('e'.toByte(), result.get())
		assertEquals('i'.toByte(), result.get())
		assertEquals('!'.toByte(), result.get())
	}
}