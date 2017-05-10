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

public class WordCount {
	public static final int NUM_THREADS = 100;
	public static String pathToFile = "./TheGodfather.txt";

	public static class WordCountTask implements Runnable {

		Map<String, Integer> map;
		List<String> lines;

		public WordCountTask(Map<String, Integer> map, List<String> lines) {
			this.map = map;
			this.lines = lines;
		}

		@Override
		public void run() {
			System.out.println("RUNNING");
			for (String line : lines) {
				String[] tokens = line.split("\\s"); // splits the string into
														// tokens separated by
														// whitespace
				for (String token : tokens) {
					token = token.toLowerCase();
					map.putIfAbsent(token, 0);
					map.put(token, map.get(token) + 1); // increment the word
														// count
				}
			}
		}
	}

	public static void main(String[] args) throws FileNotFoundException {
		Scanner s = new Scanner(new File(pathToFile));

		// One thread will get the word counts. This will produce the correct
		// answer, because there's no concurrency issues with one thread.
		ArrayList<String> allLines = new ArrayList<String>();
		HashMap<String, Integer> sequentialWordCounts = new HashMap<String, Integer>();
		while (s.hasNextLine()) {
			allLines.add(s.nextLine());
		}
		Thread mainThread = new Thread(() -> {
			for (String line : allLines) {
				String[] tokens = line.split("\\s");
				for (String token : tokens) {
					token = token.toLowerCase();
					sequentialWordCounts.putIfAbsent(token, 0);
					sequentialWordCounts.put(token, sequentialWordCounts.get(token) + 1);
				}
			}
		});
		mainThread.run();

		// Now do multi-threaded word counts.
		s = new Scanner(new File(pathToFile));
		ArrayList<ArrayList<String>> threadWork = new ArrayList<ArrayList<String>>();
		for (int i = 0; i < NUM_THREADS; i++) {
			threadWork.add(new ArrayList<String>());
		}
		int lineCount = 0;
		while (s.hasNextLine()) {
			String line = s.nextLine();
			threadWork.get(lineCount % NUM_THREADS).add(line);
			lineCount++;
		}
		List<Thread> allThreads = new ArrayList<Thread>();
		HashMap<String, Integer> unsynchronizedWordCounts = new HashMap<String, Integer>();
		Map<String, Integer> wordCounts = Collections.synchronizedMap(unsynchronizedWordCounts);
		for (int i = 0; i < NUM_THREADS; i++) {
			final int threadId = i;
			List<String> work = IntStream.range(0, allLines.size()).filter(idx -> idx % NUM_THREADS == threadId)
					.mapToObj(idx -> allLines.get(idx)).collect(Collectors.toList());

			allThreads.add(new Thread(new WordCountTask(wordCounts, work)));
		}
		allThreads.forEach(t -> t.start());
		allThreads.forEach(t -> {
			try {
				t.join();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		});

		for (String key : sequentialWordCounts.keySet()) {
			System.out.println(
					"key: " + key + " count1: " + sequentialWordCounts.get(key) + " count2: " + wordCounts.get(key));
			if (sequentialWordCounts.get(key) != wordCounts.get(key)) {
				System.out.println("FAILURE: race condition detected");
				return;
			}
		}
		System.out.println("SUCCESS: Program seems to have no race conditions");
	}
}
