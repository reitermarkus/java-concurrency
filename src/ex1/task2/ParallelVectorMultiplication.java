package ex1.task2;

import java.util.Vector;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.stream.IntStream;
import java.util.stream.Stream;

/**
 * Created by Lukas Dötlinger.
 */
public class ParallelVectorMultiplication {

  private static Vector<Integer> vector1 = new Vector<>();
  private static Vector<Integer> vector2 = new Vector<>();

  public static void main(String[] args) {
    long startTime = System.currentTimeMillis();

    for(int i = 1; i <= 500; i++){
      vector1.add(i);
      vector2.add(i);
    }
    final int vecSize = vector1.size();

    final int threadCount = Runtime.getRuntime().availableProcessors();
    final ExecutorService pool = Executors.newFixedThreadPool(threadCount);

    Stream<Future<Integer>> futures = IntStream.rangeClosed(1, threadCount).mapToObj(i -> {
      int min = (i * (vecSize/threadCount)) - (vecSize/threadCount) + 1;
      int max = (i * (vecSize/threadCount));
      Callable<Integer> task = new Multiplication(vector1, vector2, min, max);
      return pool.submit(task);});

    Integer result = futures.mapToInt(f -> {
      try {
        return f.get();
      } catch (Exception e) {
        e.printStackTrace();
        return 0;
      }
    }).sum();

    pool.shutdown();

    System.out.println(result);

    long endTime = System.currentTimeMillis();
    System.out.println("This took " + (endTime-startTime) + " milliseconds!");
  }

}
