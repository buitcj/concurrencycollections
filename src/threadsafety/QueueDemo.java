package threadsafety;

import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class QueueDemo {

	public static class QueueUserTask implements Runnable {
		private static final int NUM_ITERATIONS = 100;

		private Queue q;

		public QueueUserTask(Queue q) {
			this.q = q;
		}

		@Override
		public void run() {
			System.out.println("Starting");

			Random r = new Random();

			try {
				for (int i = 0; i < NUM_ITERATIONS; i++) {
					q.add(r.nextInt());
					q.removeLast();
				}
			} catch (Exception e) {
				System.out.println("Encountered exception: " + e.getMessage());
			}

			System.out.println("Done");
		}
	}

	public static void main(String[] args) {
		ExecutorService es = Executors.newFixedThreadPool(10);

		Queue q = new Queue();
		for (int i = 0; i < 10; i++) {
			es.submit(new QueueUserTask(q));
		}
	}
}
