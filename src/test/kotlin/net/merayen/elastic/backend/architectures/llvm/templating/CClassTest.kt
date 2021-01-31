package net.merayen.elastic.backend.architectures.llvm.templating

import net.merayen.elastic.backend.architectures.llvm.transpilercode.AllocComponent
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

internal class CClassTest {
	@Test
	fun `simple class`() {
		val cClass = object : CClass("MyClass") {
			override fun onWriteMethods(codeWriter: CodeWriter, allocComponent: AllocComponent?) {
				addInstanceMethod(codeWriter, "int*", "my_method", "int session_id") {
					codeWriter.Call("printf", """"I am a method!"""")
					codeWriter.Return("NULL")
				}
				addInstanceMethod(codeWriter, "void", "other_method") {
					codeWriter.Call("printf", """"I am another method"""")
				}
			}

			override fun onWriteMembers(codeWriter: CodeWriter) {
				codeWriter.Member("char*", "name")
				codeWriter.Member("int", "age")
			}

			override fun onWriteInit(codeWriter: CodeWriter, allocComponent: AllocComponent?) {
				codeWriter.Statement("""this->name = "Arne"""")
				codeWriter.Statement("this->age = 42")
			}

			override fun onWriteDestroy(codeWriter: CodeWriter, allocComponent: AllocComponent?) {
				codeWriter.Call("printf", """"Supposed to delete here!"""")
			}
		}

		val codeWriter = CodeWriter()
		cClass.writeStruct(codeWriter)
		cClass.writeMethods(codeWriter)

		assertEquals("""
			struct MyClass {
				char* name;
				int age;
			};
			struct MyClass* MyClass_create() {
				struct MyClass* this = calloc(1, sizeof(struct MyClass));
				this->name = "Arne";
				this->age = 42;
				return this;
			}
			void MyClass_init(struct MyClass* this) {
				this->name = "Arne";
				this->age = 42;
			}
			void MyClass_destroy(struct MyClass* this) {
				printf("Supposed to delete here!");
				free(this);
			}
			int* MyClass_my_method(struct MyClass* this, int session_id) {
				printf("I am a method!");
				return NULL;
			}
			void MyClass_other_method(struct MyClass* this) {
				printf("I am another method");
			}
		""".trimIndent(),
			codeWriter.toString()
		)
	}
}