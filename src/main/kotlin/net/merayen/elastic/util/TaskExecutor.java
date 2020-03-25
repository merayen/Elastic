package net.merayen.elastic.util;

import java.util.ArrayList;
import java.util.List;

/**
 * Reduces the amount a task executed by delaying and queuing. By adding your
 * task with add() you will queue it to be executed, though your task is not
 * guaranteed to run at all; it might be overwritten by a task with the same
 * *key*.
 * <p>
 * This is helpful when there are several Objects that wants to do the same
 * task, but where you do want to limit the frequency it is executed.
 */
public class TaskExecutor {
	public interface RunnableTask {
		/**
		 * @return true if tasks is done and should not run again, otherwise it will run for every interval set
		 */
		boolean run();
	}

	public static class Task {
		private double interval;
		private RunnableTask func;
		private long lastRun = System.currentTimeMillis();

		public Task(double interval, RunnableTask func) {
			this.interval = interval;
			this.func = func;
		}

		public Task(RunnableTask func) {
			this.interval = 0;
			this.func = func;
		}

		private boolean update() {
			boolean result;

			if (lastRun + interval < System.currentTimeMillis()) {
				result = func.run();
				lastRun = System.currentTimeMillis();
				return result;
			}

			return false; // Not ready to run yet
		}
	}

	private List<Task> tasks = new ArrayList<>();

	public synchronized void add(Task task) {
		if (tasks.contains(task))
			return;

		tasks.add(task);
	}

	/**
	 * Call this often to actually execute tasks.
	 */
	public synchronized void update() {
		double time = System.currentTimeMillis() / 1000.0;

		for (int i = tasks.size() - 1; i > -1; i--)
			if (tasks.get(i).interval <= time)
				if (tasks.get(i).update())
					tasks.remove(i);
	}

	/**
	 * Removes a task if it exists.
	 */
	private synchronized boolean remove(Task task) {
		return tasks.remove(task);
	}
}
