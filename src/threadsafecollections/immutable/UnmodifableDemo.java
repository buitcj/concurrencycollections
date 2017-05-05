package threadsafecollections.immutable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class UnmodifableDemo {
	public static int NUM_THREADS = 100;
	public static int NUM_ITERATIONS = 1000;
	public static int INITIAL_LIST_SIZE = 1000000;

	public static class ReaderTask implements Runnable {
		private List<Integer> list;
		private int threadId;

		ReaderTask(List<Integer> list, int threadId) {
			this.list = list;
			this.threadId = threadId;
		}

		@Override
		public void run() {
			long count = 0;
			for (int iter = 0; iter < NUM_ITERATIONS; iter++) {
				for (int i = 0; i < list.size(); i++) {
					count += list.get(i);
				}
			}
			System.out.println("Thread " + threadId + " got count = " + count);
		}
	}
	
	public static void main(String[] args) throws InterruptedException {
		ArrayList<Integer> arrayList = new ArrayList<Integer>();
		for (int i = 0; i < INITIAL_LIST_SIZE; i++) {
			arrayList.add(1);
		}
		List<Integer> unmodifiableList = Collections.unmodifiableList(arrayList);
		arrayList = null; // throw away the reference
		
		// Initialize readers
		Thread[] readers = new Thread[NUM_THREADS];
		for (int i = 0; i < NUM_THREADS; i++) {
			readers[i] = new Thread(new ReaderTask(unmodifiableList, i));
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
		
		System.out.println("#Threads: " + NUM_THREADS + " #ItersPerThread: " + NUM_ITERATIONS + " InitialListSize: " + INITIAL_LIST_SIZE);
		System.out.println("Work took: " + (end - start) / 1000D);
		
		/*
		 * #Threads: 100 #ItersPerThread: 1000 InitialListSize: 1000000
			Work took: 36.792
		 */
	}
}
