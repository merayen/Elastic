package net.merayen.elastic.backend.architectures.llvm.transpilercode

import net.merayen.elastic.backend.architectures.llvm.templating.CodeWriter

/**
 * C code for receiving and sending data to the parent process via pipes.
 */
class PipeComponent(
	private val allocComponent: AllocComponent,
	log: LogComponent,
	debug: Boolean
) : TranspilerComponent(log, debug) {
	fun writeMethods(codeWriter: CodeWriter) {
		with(codeWriter) {
			Method("void", "send", "int length, void* data") {
				Call("fwrite", "&length, 1, 4, stdout")
				If("length > 0") {
					Call("fwrite", "data, 1, length, stdout")
				}
				Call("fflush", "stdout")
			}

			Method("void", "send_text", "char* text") {
				Statement("int length = strlen(text)")
				Call("fwrite", "&length, 1, 4, stdout")
				Call("fwrite", "text, 1, length, stdout")
				Call("fflush", "stdout")
			}

			Method("bool", "consume_text", "char* what, char* data, int *offset, int length") {
				Statement("bool matches = false")
				For("int i = *offset, j = 0", "i < length", "i++, j++") {
					If("what[j] == '\\x00'") {
						Statement("*offset = i")
						Return("true") // We found the text
					}
					ElseIf("what[j] != data[i]") {
						Return("false") // Does not match
					}

					Statement("matches = true")
				}

				Return("matches")
			}

			Method("int", "init_stdinout") { // Initializes communication using stdin and stdout
				If("freopen(NULL, \"rb\", stdin) == NULL") {
					panic(codeWriter, "Could not open stdin")
				}

				If("freopen(NULL, \"wb\", stdout) == NULL") {
					panic(codeWriter, "Could not open stdout")
				}

				Statement("char hello[] = {'H','E','L','L','O'}")
				Call("fwrite", "hello, 1, sizeof hello, stdout")

				Call("fflush", "stdout") // Not needed?

				Statement("char buf[3]")

				Call("fread", "buf, 1, 3, stdin")

				Statement("char* ut = malloc(4)")

				If("buf[0] == 10 && buf[1] == 11 && buf[2] == 12") {
					Statement("ut[0] = 'G'")
					Statement("ut[1] = 'O'")
					Statement("ut[2] = 'O'")
					Statement("ut[3] = 'D'")
				}
				Else {
					Statement("ut[0] = 'F'")
					Statement("ut[1] = 'A'")
					Statement("ut[2] = 'I'")
					Statement("ut[3] = 'L'")
				}

				Call("fwrite", "ut, 1, 4, stdout")
				Call("fflush", "stdout")

				allocComponent.writeFree(codeWriter, "ut")

				Return(0)
			}

			/**
			 * Communicates with the mother process.
			 * Waits for input data. Should only run when not processing a frame.
			 * Will stay inside this method as long as a frame request has not happened.
			 */
			Method("void", "process_communication") {
				Statement("int message_size") // FIXME This portable...? No?

				While("true") {
					writeLog(this, "Waiting for message!", "")

					Call("fread", "&message_size, 1, 4, stdin")

					If("feof(stdin)") {
						writeLog(this, "Host closed the stream", "")
						Call("exit_failure")
					}

					If("message_size > 1073741824") {
						writeLog(this, "Packet is too big", "")
						Call("exit_failure")
					}

					//${log.writeLog("Got a message at %d bytes!", "message_size").prependIndent("\t\t")}

					If("message_size < 1") {
						writeLog(this, "Message size is less than 1", "")
						Call("exit_failure")
					}

					Statement("char* data = malloc(message_size)")

					Call("fread", "data, message_size, 1, stdin")

					If("feof(stdin)") {
						writeLog(this, "Host closed the stream", "")
						allocComponent.writeFree(codeWriter, "data")
						Call("exit_failure")
					}

					Statement("int offset = 0")

					If("""consume_text("PING", data, &offset, message_size)""") {
						Call("send_text", "\"PONG\"")
					}
					ElseIf("""consume_text("PARAMETER", data, &offset, message_size)""") {
						// TODO
					}
					ElseIf("""consume_text("PROCESS", data, &offset, message_size)""") {
						writeLog(this, "Asked to process", "")
						allocComponent.writeFree(codeWriter, "data")
						Return() // Return from this method. Caller will process a frame
					}
					ElseIf("""consume_text("QUIT", data, &offset, message_size)""") {
						writeLog(this, "Quitting", "")
						allocComponent.writeFree(codeWriter, "data")
						Call("send_text", "\"QUIT\"")
						Call("exit", "0")
					}
					ElseIf("""consume_text("NODEDATA", data, &offset, message_size)""") {
						Call("handle_ingoing_nodedata", "message_size - 8, (void *)(data + 8)")
					}
					Else {
						allocComponent.writeCalloc(codeWriter, "char*", "invalid_message", "1", "10")
						For("int i = 0", "i < 9", "i++") {
							If("data[i] < 65 || data[i] > 90") {
								Break()
							}

							Statement("invalid_message[i] = data[i]")
						}
						panic(this, "Unknown message: %s", "invalid_message")
					}

					allocComponent.writeFree(codeWriter, "data")
				}
			}
		}
	}
}
