package net.merayen.elastic.backend.architectures.llvm.ctools

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

internal class IndentCTest {
	@Test
	fun `code block`() {
		val result = indentC("""
			#define something
			
			struct {
			int noe;
			} *outlets[VOICE_COUNT] ;
			struct {
			int noe;
			} *outlets[VOICE_COUNT];
			struct {
			int noe;
			}*outlets[VOICE_COUNT];
			struct {
			  			 	 	 int noe;
			};
			struct {
			 int noe;
			} ;

			char hello[] = {'H','E','L','L','O'};

			 void main(int something) { // Some comment
			woah;
			  if ("// {something}") {	
			break;
			}
			// {
			// Should not be indented
			// }
			}
		""".trimIndent())

		assertEquals("""
			#define something
			
			struct {
				int noe;
			} *outlets[VOICE_COUNT] ;
			struct {
				int noe;
			} *outlets[VOICE_COUNT];
			struct {
				int noe;
			}*outlets[VOICE_COUNT];
			struct {
				int noe;
			};
			struct {
				int noe;
			} ;
			
			char hello[] = {'H','E','L','L','O'};

			void main(int something) { // Some comment
				woah;
				if ("// {something}") {
					break;
				}
				// {
				// Should not be indented
				// }
			}
		""".trimIndent(),
			result)
	}
}