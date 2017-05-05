package threadsafecollections.reducing;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class SynchronizedCollectionDemo {
	
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
		Map<Integer, Integer> sharedSynchronizedMap = Collections.synchronizedMap(sharedMap);
		
		// Initialize threads
		Thread[] readers = new Thread[NUM_THREADS];
		for (int i = 0; i < NUM_THREADS; i++) {
			readers[i] = new Thread(new ReadUpdateWrite(sharedSynchronizedMap, i));
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
			System.out.println ("Key: " + key + " val: " + sharedSynchronizedMap.get(key));
		}
		
		/*
		 * 
		 * Work took: 401.38
			NumThreads: 1000 IterationsPerThread: 1000000
			Key: 0 val: 2197185
			Key: 1 val: 2397587
			Key: 2 val: 2177411
			Key: 3 val: 2286043
		 */
		
		List<Integer> unsynchronizedList = new ArrayList<Integer>();
		List<Integer> synchronizedList = Collections.synchronizedList(unsynchronizedList);
		
		synchronizedList.add(25);
		synchronizedList.get(0);
		
		synchronized(synchronizedList) {
			for (Integer i : synchronizedList) {
				System.out.println(i);
			}
		}
		
		synchronized(synchronizedList) {
			Iterator<Integer> iter = synchronizedList.iterator();
			while(iter.hasNext()) {
				Integer i = iter.next();
				System.out.println(i);
			}
		}
	}
}
