package ex1.task2;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

/**
 * Created by Lukas Dötlinger.
 */
public class ParallelVectorMultiplication {

  private static List<Integer> vector1 = new ArrayList<>();
  private static List<Integer> vector2 = new ArrayList<>();

  public static void main(String[] args) {

    for(int i = 1; i <= 500; i++){
      vector1.add(i);
      vector2.add(i);
    }
    final int vecSize = vector1.size();

    final int threadCount = Runtime.getRuntime().availableProcessors();
    final ExecutorService pool = Executors.newFixedThreadPool(threadCount);

    Stream<Future<Integer>> futures = IntStream.range(1, threadCount).mapToObj(i -> {
      int min = (i * (vecSize/i)) - (vecSize/i) + 1;
      int max = (i * (vecSize/i));
      ArrayList<Integer> temp1 = new ArrayList<>();
      ArrayList<Integer> temp2 = new ArrayList<>();
      for(int j = min; j <= max; j++) {
        temp1.add(vector1.get(j-1));
        temp2.add(vector2.get(j-1));
      }
      Callable<Integer> task = new Multiplication(temp1, temp2);
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
  }

}
