package threadsafecollections.reducing;

import java.util.concurrent.ConcurrentHashMap;

public class ConcurrentCollectionTask {
	
	public static int NUM_THREADS = 1000;
	public static int NUM_ITERATIONS = 1000000;
	
	public static class ReadUpdateWrite implements Runnable {
		
		ConcurrentHashMap<Integer, Integer> map;
		int threadId;
		
		ReadUpdateWrite(ConcurrentHashMap<Integer, Integer> m, int threadId) {
			this.map = m;
			this.threadId = threadId;
		}

		@Override
		public void run() {
			int key = threadId % 4;
			this.map.putIfAbsent(key, 0);
			for (int i = 0; i < NUM_ITERATIONS; i++) {
				
				boolean replaced;
				do {
					Integer val = this.map.get(key);
					replaced = this.map.replace(key, val, val + 1);
				} while (!replaced);
			}
		}
		
	}
	public static void main(String[] args) throws InterruptedException {
		ConcurrentHashMap<Integer, Integer> concurrentHashMap = new 
				ConcurrentHashMap<Integer, Integer>();
		
		// Initialize threads
		Thread[] readers = new Thread[NUM_THREADS];
		for (int i = 0; i < NUM_THREADS; i++) {
			readers[i] = new Thread(new ReadUpdateWrite(concurrentHashMap, i));
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
		for (int key : concurrentHashMap.keySet()) {
			System.out.println ("Key: " + key + " val: " + concurrentHashMap.get(key));
		}
		
		/*
		 * Work took: 95.125
			Key: 0 val: 250000000
			Key: 1 val: 250000000
			Key: 2 val: 250000000
			Key: 3 val: 250000000
		 */
	}
}
