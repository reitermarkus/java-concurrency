package ex1;

import java.util.*;
import java.util.concurrent.*;
import java.util.stream.*;

public class ParallelPiCalculation {
  public static void main(String[] args) throws InterruptedException {
    Random randomizer = new Random();

    long tries = 1000000;

    int threadCount = Runtime.getRuntime().availableProcessors();

    long triesPerThread = tries / threadCount;
    long rest = tries % threadCount;

    ExecutorService threadPool = Executors.newFixedThreadPool(threadCount);

    Stream<Future<Long>> futures = IntStream.rangeClosed(1, threadCount).mapToObj(i -> {
      long triesForThread = i < threadCount ? triesPerThread : triesPerThread + rest;
      Callable<Long> task = new MonteCarlo(randomizer, triesForThread);
      return threadPool.submit(task);
    });

    Long successes = futures.mapToLong(f -> {
      try {
        return f.get();
      } catch (Exception e) {
        e.printStackTrace();
        return 0L;
      }
    }).sum();

    threadPool.shutdown();

    float quarterPi = (float) successes / (float) tries;
    float pi = quarterPi * 4f;

    System.out.print(pi);
  }
}
