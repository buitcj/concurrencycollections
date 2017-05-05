package threadsafecollections.chm.parallelfxns;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ForkJoinPool;

public class ParallelFunctionsDemo {

	public static class BeefAdderTask implements Runnable {
		ConcurrentHashMap<String, String> chm;

		BeefAdderTask(ConcurrentHashMap<String, String> chm) {
			this.chm = chm;
		}

		@Override
		public void run() {
			chm.put("pho", "bo");
			chm.put("bun", "bo");
			chm.put("chao", "bo");
			chm.put("pizza", "bo");
			chm.put("com", "bo");
			chm.put("mien", "bo");
		}
	}

	public static class ChickenAdderTask implements Runnable {
		ConcurrentHashMap<String, String> chm;

		ChickenAdderTask(ConcurrentHashMap<String, String> chm) {
			this.chm = chm;
		}

		@Override
		public void run() {
			chm.put("pho", "ga");
			chm.put("bun", "ga");
			chm.put("chao", "ga");
			chm.put("pizza", "ga");
			chm.put("soup", "ga");
		}
	}

	public static void main(String[] args) throws InterruptedException {
		ConcurrentHashMap<String, String> chm = new ConcurrentHashMap<String, String>();
		Thread chickenAdderThread = new Thread(new ChickenAdderTask(chm));
		Thread beefAdderThread = new Thread(new BeefAdderTask(chm));

		chickenAdderThread.start();
		beefAdderThread.start();
		chickenAdderThread.join();
		beefAdderThread.join();

		System.out.println("Parallelism: " + ForkJoinPool.getCommonPoolParallelism()); // 3

		System.out.println("------For Each-------");
		
		chm.forEach(ForkJoinPool.getCommonPoolParallelism(), 
			(key, value) -> System.out.printf("key: %s; value: %s; thread: %s\n", key, value,
				Thread.currentThread().getName())
			);
		
		System.out.println("------Search For Pho-------");

		String result = chm.search(ForkJoinPool.getCommonPoolParallelism(), 
			(key, value) -> {
				System.out.println(Thread.currentThread().getName());
				if ("pho".equals(key)) {
					return value;
				}
				return null;
			}
		);
		System.out.println("Result: " + result);
		
		System.out.println("------Reduce-------");

		String result2 = chm.reduce(ForkJoinPool.getCommonPoolParallelism(), 
			(key, value) -> {
				System.out.println("Transform: " + Thread.currentThread().getName());
				return key + "=" + value;
			}, 

			(aggregate, element) -> {
				System.out.println("Reduce: " + Thread.currentThread().getName());
				return aggregate + ", " + element;
			}
		);

		System.out.println("Result: " + result2);
	}
}
