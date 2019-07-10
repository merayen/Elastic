package net.merayen.elastic.ui

object Test {
    fun test() {
        val supervisor = Supervisor(object : Supervisor.Handler {

            override fun onMessageToBackend(message: Any) {}

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
