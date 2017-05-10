import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

// This class will create threads to count how many time each word appears in TheGodfather.txt.  For example
// "a b b c c c" will have a map [a: 1, b: 2, c: 3].  At the moment this program can run concurrently
// with many threads, but will not produce the correct output because there is a race condition.  Modify
// The WordCountTask and possibly use a different map structure to produce the correct output.  Running
// the program will produce a success or failure message.

public class WordCount {
	public static final int NUM_THREADS = 100;
	public static String pathToFile = "./TheGodfather.txt";

	// Each Thread will count words from different parts of the file.
	public static class WordCountTask implements Runnable {
		// ***************************************
		// YOU MAY CHANGE THIS CODE
		// ***************************************
		Map<String, Integer> map;
		List<String> lines;

		public WordCountTask(Map<String, Integer> map, List<String> lines) {
			this.map = map;
			this.lines = lines;
		}

		@Override
		public void run() {
			System.out.println("Running on thread " + Thread.currentThread().getName());
			for (String line : lines) {
				String[] tokens = line.split("\\s");
				for (String token : tokens) {
					map.putIfAbsent(token, 0);
					map.put(token, map.get(token) + 1);
				}
			}
		}
		// *****************************************
		// END - DONT CHANGE CODE BELOW THIS POINT
		// *****************************************
	}

	public static void main(String[] args) throws FileNotFoundException {
		
		// Read the file into memory
		Scanner s = new Scanner(new File(pathToFile));
		ArrayList<String> allLines = new ArrayList<String>();
		HashMap<String, Integer> sequentialWordCounts = new HashMap<String, Integer>();
		while (s.hasNextLine()) {
			allLines.add(s.nextLine().toLowerCase());
		}
		
		// One thread will get the word counts. This will produce the correct
		// answer, because there's no concurrency issues with one thread.
		for (String line : allLines) {
			String[] tokens = line.split("\\s");
			for (String token : tokens) {
				token = token.toLowerCase();
				sequentialWordCounts.putIfAbsent(token, 0);
				sequentialWordCounts.put(token, sequentialWordCounts.get(token) + 1);
			}
		}

		// ***************************************
		// YOU MAY CHANGE THIS CODE
		// ***************************************
		
		// Now do multi-threaded word counts.
		List<Thread> allThreads = new ArrayList<Thread>();
		HashMap<String, Integer> wordCounts = new HashMap<String, Integer>();
		Map<String, Integer> synchronizedWordCounts = Collections.synchronizedMap(wordCounts);
		for (int i = 0; i < NUM_THREADS; i++) {
			final int threadId = i;
			List<String> threadLocalWork = IntStream.range(0, allLines.size()).filter(idx -> idx % NUM_THREADS == threadId)
					.mapToObj(idx -> allLines.get(idx)).collect(Collectors.toList()); // assign each thread some lines from TheGodfather.txt

			allThreads.add(new Thread(new WordCountTask(synchronizedWordCounts, threadLocalWork)));
		}
		// *****************************************
		// END - DONT CHANGE CODE BELOW THIS POINT
		// *****************************************
		
		allThreads.forEach(t -> t.start());
		allThreads.forEach(t -> {
			try {
				t.join();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		});

		for (String key : sequentialWordCounts.keySet()) {
			System.out.println(
					"key: " + key + " expected: " + sequentialWordCounts.get(key) + " actual: " + wordCounts.get(key));
			if (!sequentialWordCounts.get(key).equals(wordCounts.get(key))) {
				System.out.println(
						" _______    ___       __   __      \n" +
						"|   ____|  /   \\     |  | |  |     \n" +
						"|  |__    /  ^  \\    |  | |  |     \n" +
						"|   __|  /  /_\\  \\   |  | |  |     \n" +
						"|  |    /  _____  \\  |  | |  `----.\n" +
						"|__|   /__/     \\__\\ |__| |_______|\n");
				return;
			}
		}
		System.out.println("SUCCESS: Program seems to have no race conditions");
		
		// END - DONT CHANGE THIS CODE
	}
}
