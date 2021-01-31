public class Main {
	static int contextSwitches = 0;
	static int last = 0;

	static class A extends Thread {
		static int counter = 0;
		private int num;
		public boolean running = true;
		public A other;
		private boolean queued;

		{
			num = counter++;
		}

		@Override
		public void run() {
			while (running) {
				other.wakeUp();

				try {
					synchronized (this) {
						if (!queued) {
							wait();
						}
						if (queued)
							contextSwitches++;

						queued = false;
					}
				} catch (InterruptedException e) {
					throw new RuntimeException(e);
				}
			}
		}

		public void wakeUp() {
			synchronized (this) {
				queued = true;
				notifyAll();
			}
		}
	}

	public static void main(String[] args) {
		A a = new A();
		A b = new A();
		a.other = b;
		b.other = a;
		a.start();
		b.start();
		a.setPriority(Thread.MAX_PRIORITY);
		b.setPriority(Thread.MAX_PRIORITY);
		try {
			for (int i = 0; i < 100000; i++) {
				Thread.sleep(1000);
				System.out.println(contextSwitches + " " + (contextSwitches - last));
				last = contextSwitches;
			}
			a.running = false;
			b.running = false;
			a.join();
			b.join();
		} catch (InterruptedException e) {
			throw new RuntimeException(e);
		}
	}
}
