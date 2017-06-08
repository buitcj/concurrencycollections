package executors;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class ExecutorDemo {

	public static class PrimerFinderTask implements Callable<String> {
		
		
		private int num;
		
		public PrimerFinderTask(int num) {
			this.num = num;
		}

		@Override
		public String call() throws Exception {
			
			for (int i = 2; i < num; i++) {
				if (num % i == 0) {
					return num + " was divisible by : " + i;
				}
			}
			
			return "Prime found: " + num;
		}
	}
	
	public static void main(String[] args) {
		ExecutorService es = Executors.newFixedThreadPool(10);
		
		List<Future<String>> futures = new ArrayList<Future<String>>();
		for (int i = 0; i < 10000000; i++) {
			Future<String> future = es.submit(new PrimerFinderTask(i));
			futures.add(future);
		}
		
		futures.forEach(f -> {
			try {
				System.out.println(f.get());
			} catch (InterruptedException e) {
				e.printStackTrace();
			} catch (ExecutionException e) {
				e.printStackTrace();
			}
		});
	}
}
