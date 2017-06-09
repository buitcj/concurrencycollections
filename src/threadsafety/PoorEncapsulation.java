package threadsafety;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.locks.ReentrantLock;

public class PoorEncapsulation {
	
	public ReentrantLock lock = new ReentrantLock();
	
	private int numElements;
	private List<Integer> queue = new ArrayList<Integer>();
	
	public void addVal(Integer i) {
		queue.add(i);
		numElements++;
	}
	
	public Integer removeTail() {
		if (numElements > 0) {
			Integer i = queue.remove(numElements - 1);
			numElements--;
			return i;
		} else {
			return null;
		}
	}
	
	public static void main(String[] args) {
		PoorEncapsulation pe = new PoorEncapsulation();
		
		ExecutorService es = Executors.newFixedThreadPool(10);
		
		final int NUM_ITERATIONS = 10000;
		final int NUM_THREADS = 10;
		
		for (int j = 0; j < NUM_THREADS; j++) {
			es.submit(() -> {
				for (int i = 0; i < NUM_ITERATIONS; i++) {
					try {
						pe.lock.lock();
						pe.addVal(i);
						pe.removeTail();
					} catch (Exception e) {
						System.out.println("exception: " + e.getMessage());
						break;
					} finally {
						pe.lock.unlock();
					}
				}
				System.out.println("locking thread done");
			});
		}

//		es.submit(() -> {
//			for (int i = 0; i < NUM_ITERATIONS; i++) {
//				try {
//					pe.addVal(i);
//					pe.removeTail();
//				} catch (Exception e) {
//					System.out.println("Bad client exception: " + e.getMessage());
//					break;
//				}
//			}
//			System.out.println("no locking thread done");
//		});
		
//		es.submit(() -> {
//			for (int i = 0; i < NUM_ITERATIONS; i++) {
//				try {
//					pe.lock.lock();
//					Thread.sleep(1000);
//					pe.addVal(i);
//					pe.removeTail();
//				} catch (Exception e) {
//					System.out.println("Slow client exception: " + e.getMessage());
//					break;
//				} finally {
//					pe.lock.unlock();
//				}
//			}
//			System.out.println("slow thread done");
//		});

		es.shutdown();
		System.out.println("Done");
	}
}
