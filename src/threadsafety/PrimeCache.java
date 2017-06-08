package threadsafety;

// not thread safe
public class PrimeCache {
	
	private static Integer lastRequest;
	private static boolean lastRequestIsPrime;

	// takes a long time
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
		if (lastRequest != null && lastRequest.intValue() == num) {
			// use caching
			return lastRequestIsPrime;
		} else {
			// can't use caching
			lastRequest = num;
			lastRequestIsPrime = isPrimeImpl(num); // takes a long time
			return lastRequestIsPrime;
		}
	}
}
