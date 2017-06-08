package threadsafety;

import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ThreadSafeQueueDemo {

	public static class ThreadSafeQueueUserTask implements Runnable {
		private static final int NUM_ITERATIONS = 10000;
		
		private ThreadSafeQueue q;
		
		public ThreadSafeQueueUserTask(ThreadSafeQueue q) {
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
		
		ThreadSafeQueue q = new ThreadSafeQueue();
		for (int i = 0; i < 10; i++) {
			es.submit(new ThreadSafeQueueUserTask(q));
		}
		es.shutdown();
	}
}
