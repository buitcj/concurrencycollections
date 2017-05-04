package threadsafecollections.reducing;

import java.util.HashMap;
import java.util.Map;

public class UnsynchronizedCollectionTask {
	
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
			for (int i = 0; i < NUM_ITERATIONS; i++) {
				int key = threadId % 4;
				Integer val = this.map.get(key);
				map.put(key, val == null ? 1 : val + 1);
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
		 * Work took: 34.296
			NumThreads: 1000 IterationsPerThread: 1000000
			Key: 0 val: 10960199
			Key: 1 val: 11832951
			Key: 2 val: 9852383
			Key: 3 val: 12248297
		 */
	}
}
