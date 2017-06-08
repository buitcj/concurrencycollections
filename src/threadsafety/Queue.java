package threadsafety;

import java.util.ArrayList;
import java.util.List;

public class Queue {
	private int numElements; // numElements >= 0, numElements == elements.size()
	private List<Integer> elements;
	
	public Queue() {
		elements = new ArrayList<Integer>();
	}
	
	public void add(Integer i) {
		elements.add(numElements, i);
		numElements++;
	}
	
	public Integer removeLast() {
		if (numElements > 0) {
			Integer i = elements.get(numElements - 1);
			numElements--;
			return i;
		} else {
			return null;
		}
	}
	
	public static void main(String[] args) {
		
	}
}