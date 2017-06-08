package threadsafety;

import java.util.concurrent.locks.ReentrantLock;

public class ThreadSafePrimeCache {
	
	private static ReentrantLock lock = new ReentrantLock();
	private static Integer lastRequest;
	private static boolean lastRequestIsPrime;

	public static boolean isPrimeImpl(int num) {
		for (int i = 2; i < num; i++) {
			if (num % i == 0) {
				return false;
			}
		}
		return true;
	}
	
	// Uses caching!
	public static boolean isPrime(int num) {
		lock.lock();
		if (lastRequest != null && lastRequest.intValue() == num) {
			
			// use caching
			boolean isPrime = lastRequestIsPrime;
			lock.unlock();
			
			return isPrime;
			
		} else {
			lock.unlock();
			
			// can't use caching
			boolean isPrime = isPrimeImpl(num); // runs slowly
			
			lock.lock();
			lastRequest = num;
			lastRequestIsPrime = isPrime;
			lock.unlock();
			
			return isPrime;
		}
	}
}
