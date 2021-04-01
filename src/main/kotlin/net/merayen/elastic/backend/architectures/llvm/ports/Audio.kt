package net.merayen.elastic.backend.architectures.llvm.ports

import net.merayen.elastic.backend.architectures.llvm.templating.CClass
import net.merayen.elastic.backend.architectures.llvm.templating.CodeWriter
import net.merayen.elastic.backend.architectures.llvm.transpilercode.AllocComponent

internal class Audio(frameSize: Int) : PortStruct(frameSize) {
	override val clsName = "PortDataAudio"

	override val cClass = object : CClass(clsName) {
		override fun onWriteDestroy(codeWriter: CodeWriter, allocComponent: AllocComponent?) {
			super.onWriteDestroy(codeWriter, allocComponent)
			with(codeWriter) {
				If("this->audio != NULL") {
					if (allocComponent != null)
						allocComponent.writeFree(codeWriter, "this->audio")
					else
						Call("free", "this->audio")
				}
			}
		}

		override fun onWriteMethods(codeWriter: CodeWriter, allocComponent: AllocComponent?) {
			//addInstanceMethod(codeWriter, "void", "prepare", "int channels") {
			//	codeWriter.If("this->channels != channels") {
			//		codeWriter.If("this->audio != NULL") {
			//			if (allocComponent != null)
			//				allocComponent.writeFree(codeWriter, "this->audio")
			//			else
			//				codeWriter.Call("free", "this->audio")
			//		}

			//		if (allocComponent != null)
			//			allocComponent.writeMalloc(codeWriter, "", "this->audio", "$frameSize * channels * sizeof(float)")
			//		else
			//			codeWriter.Statement("this->audio = malloc($frameSize * channels * sizeof(float))")
			//	}
			//}
		}

		override fun onWriteInit(codeWriter: CodeWriter, allocComponent: AllocComponent?) {
			super.onWriteInit(codeWriter, allocComponent)

			// TODO retrieve channel count from owning node. Hardcoded to 2 for now
			allocComponent!!.writeMalloc(codeWriter, "", "this->audio", "$frameSize * 2 * sizeof(float)")
		}

		override fun onWriteMembers(codeWriter: CodeWriter) {
			codeWriter.Member("int", "channels")
			codeWriter.Member("float*", "audio") // Format: channel 0, channel 1, ...
		}
	}
}
