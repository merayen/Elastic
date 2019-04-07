package net.merayen.elastic.util;

import java.util.ArrayList;
import java.util.List;

/**
 * Reduces the amount a task executed by delaying and queuing. By adding your
 * task with add() you will queue it to be executed, though your task is not
 * guaranteed to run at all; it might be overwritten by a task with the same
 * *key*.
 * 
 * This is helpful when there are several Objects that wants to do the same
 * task, but where you do want to limit the frequency it is executed.
 */
public class TaskExecutor {
	public static class Task {
		private Object key;
		private long fire;
		private Runnable func;

		public Task(Object key, long delay_ms, Runnable func) {
			this.key = key;
			this.fire = System.currentTimeMillis() + delay_ms;
			this.func = func;
		}

		public Task(Object key, Runnable func) {
			this(key, 0, func);

		}
	}

	private List<Task> tasks = new ArrayList<>();

	public synchronized void add(Task task) {
		Task last = get(task.key);

		if(last != null) {
			task.fire = Math.min(last.fire, task.fire); // Prevents a task from never being executed if add() is flooded
			remove(task.key); // Remove previous task as it will be overwritten
		}

		tasks.add(task);
	}

	/**
	 * Call this often to actually execute tasks.
	 */
	public synchronized void update() {
		long time = System.currentTimeMillis();

		for(int i = tasks.size() - 1; i > -1; i--)
			if(tasks.get(i).fire <= time)
				tasks.remove(i).func.run();
	}

	private void remove(Object key) {
		for(int i = tasks.size() - 1; i > -1; i--)
			if(tasks.get(i).key == key)
				tasks.remove(i);
	}

	private Task get(Object key) {
		for(Task t : tasks)
			if(t.key == key)
				return t;

		return null;
	}
}
