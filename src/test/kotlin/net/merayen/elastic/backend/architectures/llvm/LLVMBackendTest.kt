package net.merayen.elastic.backend.architectures.llvm

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

internal class LLVMBackendTest {
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

				char buf[3];

				fread(buf, 1, 3, stdin);

				char ut[4];

				if (buf[0] == 10 && buf[1] == 11 && buf[2] == 12) {
					ut[0] = 'G';
					ut[1] = 'O';
					ut[2] = 'O';
					ut[3] = 'D';
				} else {
					ut[0] = 'N';
					ut[1] = 'O';
					ut[2] = 'P';
					ut[3] = 'E';
				}

				fwrite(ut, 1, 4, stdout);
				fflush(stdout); // Not needed?

				// Just wait for single character
				char nothing[1];
				fread(nothing, 1, 1, stdin);

				return 0;
			}
		""".trimIndent())

		val com = LLVMCommunicator(backend)
		com.send(byteArrayOf(0.toByte()))
		Thread.sleep(100)
		assertThrows(LLVMBackend.ProcessDead::class.java) {
			backend.ensureAlive()
		}
	}
}