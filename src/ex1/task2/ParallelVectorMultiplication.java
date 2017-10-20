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

  private static Vector<Long> vector1 = new Vector<>();
  private static Vector<Long> vector2 = new Vector<>();

  public static void main(String[] args) {
    long startTime = System.currentTimeMillis();

    for(int i = 1; i <= 10000; i++){
      vector1.add((long) i);
      vector2.add((long) i);
    }
    final int vecSize = vector1.size();

    final int threadCount = Runtime.getRuntime().availableProcessors();
    final ExecutorService pool = Executors.newFixedThreadPool(threadCount);

    Stream<Future<Long>> futures = IntStream.rangeClosed(1, threadCount).mapToObj(i -> {
      int min = (i * (vecSize/threadCount)) - (vecSize/threadCount) + 1;
      int max = (i * (vecSize/threadCount));
      Callable<Long> task = new Multiplication(vector1, vector2, min, max);
      return pool.submit(task);
    });

    Long result = futures.mapToLong(f -> {
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
