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
			(key, value) -> { // search function returns 
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
				System.out.println("Map: " + Thread.currentThread().getName()); // Actually called a transformer
				return key + "=" + value;
			}, 

			(aggregate, element) -> {
				System.out.println("Reduce: " + Thread.currentThread().getName());
				return aggregate + ", " + element;
			}
		);

		System.out.println("Result: " + result2);
	}

	/*
	Parallelism: 3
	------For Each-------
	key: com; value: bo; thread: main
	key: chao; value: bo; thread: ForkJoinPool.commonPool-worker-3
	key: bun; value: bo; thread: ForkJoinPool.commonPool-worker-1
	key: pho; value: bo; thread: ForkJoinPool.commonPool-worker-2
	key: mien; value: bo; thread: ForkJoinPool.commonPool-worker-2
	key: soup; value: ga; thread: main
	key: pizza; value: bo; thread: main
	------Search For Pho-------
	main
	ForkJoinPool.commonPool-worker-3
	ForkJoinPool.commonPool-worker-1
	ForkJoinPool.commonPool-worker-2
	main
	Result: bo
	------Reduce-------
	Transform: main
	Transform: main
	Reduce: main
	Transform: main
	Reduce: main
	Transform: ForkJoinPool.commonPool-worker-3
	Transform: ForkJoinPool.commonPool-worker-1
	Transform: ForkJoinPool.commonPool-worker-2
	Transform: ForkJoinPool.commonPool-worker-1
	Reduce: ForkJoinPool.commonPool-worker-1
	Reduce: ForkJoinPool.commonPool-worker-2
	Reduce: ForkJoinPool.commonPool-worker-2
	Reduce: ForkJoinPool.commonPool-worker-2
	Result: com=bo, soup=ga, pizza=bo, pho=bo, mien=bo, bun=bo, chao=bo
	*/	
}
