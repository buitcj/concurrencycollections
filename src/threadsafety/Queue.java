package threadsafety;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.ReentrantLock;

public class Queue {
	ReentrantLock lock = new ReentrantLock();
	private int numElements; // > 0, numElements = elements.size()
	private List<Integer> elements;
	
	public Queue() {
		elements = new ArrayList<Integer>();
	}
	
	public void add(Integer i) {
		lock.lock();
		elements.add(numElements, i);
		numElements++;
		lock.unlock();
	}
	
	public Integer removeLast() {
		try {
			lock.lock();
			if (numElements > 0) {
				Integer i = elements.get(numElements - 1);
				numElements--;
				return i;
			} else {
				return null;
			}
		} finally {
			lock.unlock();
		}
	}
	
	public static void main(String[] args) {
		
	}
}