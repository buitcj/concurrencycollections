package threadsafecollections.sumofelements;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class NonCowCollectionTask {
	
	public static int NUM_THREADS = 100;
	public static int NUM_ITERATIONS = 10000;
	public static int INITIAL_LIST_SIZE = 100;
	
	public static class InfrequentWriterTask implements Runnable {
		List<Integer> list;
		int threadId;
		
		InfrequentWriterTask(List<Integer> list, int threadId) {
			this.list = list;
			this.threadId = threadId;
		}
		
		@Override
		public void run() {
			while (true) {
				list.add(1);
				
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
		
	}
	
	public static class FrequentReaderTask implements Runnable {
		
		List<Integer> list;
		int threadId;
		
		FrequentReaderTask(List<Integer> list, int threadId) {
			this.list = list;
			this.threadId = threadId;
		}

		@Override
		public void run() {
			long count = 0;
			for(int iter = 0; iter < NUM_ITERATIONS; iter++) {
				for (int i = 0; i < list.size(); i++) {
					Integer element = list.get(i);
					if (element != null) {
						count += element;
					}
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
		List<Integer> synchronizedList = Collections.synchronizedList(arrayList);
		
		// Initialize 1 writer
		Thread infrequentWriterThread = new Thread(new InfrequentWriterTask(synchronizedList, -1));
		infrequentWriterThread.start();
		
		// Initialize readers
		Thread[] readers = new Thread[NUM_THREADS];
		for (int i = 0; i < NUM_THREADS; i++) {
			readers[i] = new Thread(new FrequentReaderTask(synchronizedList, i));
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
		 * Threads: 100 #ItersPerThread: 10000 InitialListSize: 100
			Work took: 39.671
		 */
	}
}
