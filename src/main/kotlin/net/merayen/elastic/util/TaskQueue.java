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
public class TaskQueue {
	public interface RunsTasks {
		TaskQueue getTaskQueue();
	}

	public interface RunnableTask {
		/**
		 * @return true if tasks is done and should not run again, otherwise it will run for every interval set
		 */
		boolean run();
	}

	public static class Task {
		private double interval;
		private RunnableTask func;
		private double lastRun = System.currentTimeMillis() / 1000.0;

		private Task(double interval, RunnableTask func) {
			this.interval = interval;
			this.func = func;
		}

		private boolean run() {
			lastRun = System.currentTimeMillis() / 1000.0;
			return func.run();
		}
	}

	private List<Task> tasks = new ArrayList<>();

	public synchronized void add(Task task) {
		if (tasks.contains(task))
			return;

		tasks.add(task);
	}

	public synchronized Task add(RunnableTask func) {
		Task task = new Task(0, func);
		tasks.add(task);
		return task;
	}


	public synchronized Task add(double interval, RunnableTask func) {
		Task task = new Task(interval, func);
		tasks.add(task);
		return task;
	}

	/**
	 * Call this often to actually execute tasks.
	 */
	public synchronized void update() {
		double time = System.currentTimeMillis() / 1000.0;

		for (int i = tasks.size() - 1; i > -1; i--) {
			Task task = tasks.get(i);
			if (task.lastRun + task.interval <= time)
				if (task.run())
					tasks.remove(i);
		}
	}

	/**
	 * Removes a task if it exists.
	 */
	public synchronized boolean remove(Task task) {
		return tasks.remove(task);
	}

	public synchronized boolean hasTask(Task task) {
		return tasks.contains(task);
	}
}
