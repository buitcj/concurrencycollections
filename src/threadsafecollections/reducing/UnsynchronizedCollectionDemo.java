package threadsafecollections.reducing;

import java.util.HashMap;
import java.util.Map;

public class UnsynchronizedCollectionDemo {
	
	public static int NUM_THREADS = 1000;
	public static int NUM_ITERATIONS = 1000000;
	
	public static class ReadUpdateWrite implements Runnable {
		
		Map<Integer, Integer> map;
		int threadId;
		
		ReadUpdateWrite(Map<Integer, Integer> m, int threadId) {
			this.map = m;
			this.threadId = threadId;
		}

		@Override
		public void run() {
			int key = threadId % 4;
			this.map.putIfAbsent(key, 0);
			for (int i = 0; i < NUM_ITERATIONS; i++) {
				boolean wasReplaced;
				do {
					Integer val = this.map.get(key);
					wasReplaced = this.map.replace(key, val, val + 1);
				} while (!wasReplaced);
			}
		}
		
	}
	public static void main(String[] args) throws InterruptedException {
		Map<Integer, Integer> sharedMap = new HashMap<Integer, Integer>();
		
		// Initialize threads
		Thread[] readers = new Thread[NUM_THREADS];
		for (int i = 0; i < NUM_THREADS; i++) {
			readers[i] = new Thread(new ReadUpdateWrite(sharedMap, i));
		}
		
		long start = System.currentTimeMillis();
		
		// Start threads
		for (int i = 0; i < NUM_THREADS; i++) {
			readers[i].start();
		}
		
		// Join threads
		for (int i = 0; i < NUM_THREADS; i++) {
			readers[i].join();
		}
		
		long end = System.currentTimeMillis();
		
		System.out.println("Work took: " + (end - start) / 1000D);
		System.out.println("NumThreads: " + NUM_THREADS + " IterationsPerThread: " + NUM_ITERATIONS);
		
		for (int key : sharedMap.keySet()) {
			System.out.println ("Key: " + key + " val: " + sharedMap.get(key));
		}
		
		/*
		 * Work took: 14.176
			NumThreads: 1000 IterationsPerThread: 1000000
			Key: 0 val: 199355898
			Key: 1 val: 198536239
			Key: 2 val: 192390738
			Key: 3 val: 224033486
		 */
	}
}
