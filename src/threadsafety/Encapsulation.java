package threadsafety;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.locks.ReentrantLock;

public class Encapsulation {
	
	private ReentrantLock lock = new ReentrantLock();
	
	private int numElements;
	private List<Integer> queue = new ArrayList<Integer>();
	
	public void addVal(Integer i) {
		lock.lock();
		queue.add(i);
		numElements++;
		lock.unlock();
	}
	
	public Integer removeTail() {
		lock.lock();
		if (numElements > 0) {
			Integer i = queue.get(numElements - 1);
			numElements--;
			lock.unlock();
			return i;
		} else {
			lock.unlock();
			return null;
		}
	}
	
	public static void main(String[] args) {
		Encapsulation encapsulation = new Encapsulation();
		
		ExecutorService es = Executors.newFixedThreadPool(10);
		
		final int NUM_ITERATIONS = 10000;
		final int NUM_THREADS = 10;
		
		for (int j = 0; j < NUM_THREADS; j++) {
			es.submit(() -> {
				for (int i = 0; i < NUM_ITERATIONS; i++) {
					try {
						encapsulation.addVal(i);
						encapsulation.removeTail();
					} catch (Exception e) {
						System.out.println("exception: " + e.getMessage());
						break;
					}
				}
				System.out.println("thread done");
			});
		}

		es.shutdown();
		System.out.println("Done");
	}
}
