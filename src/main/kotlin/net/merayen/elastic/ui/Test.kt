package net.merayen.elastic.ui

import net.merayen.elastic.util.Postmaster.Message

object Test {
    fun test() {
        val supervisor = Supervisor(object : Supervisor.Handler {

            override fun onMessageToBackend(message: Message) {}

            override fun onReadyForMessages() {}
        })
        try {
            Thread.sleep(3000)
        } catch (e: InterruptedException) {
            e.printStackTrace()
        }

        supervisor.end()
    }
}
