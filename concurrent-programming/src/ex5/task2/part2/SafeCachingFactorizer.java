package ex5.task2.part2;

import java.math.BigInteger;
import java.util.Arrays;
import java.util.concurrent.*;

/**
 * Created by Michael Kaltschmid on 10.12.2017.
 */
public class SafeCachingFactorizer {
  private static ImmutableCache cache;

  public static BigInteger[] getImmutableCache(BigInteger[] factor, BigInteger number) {
      if (cache != null && number.equals(cache.getLastNumber())) {
        System.out.println("Reuse cache");
        return cache.getLastFactors();
      } else {
        cache = new ImmutableCache(factor, number);
        System.out.println("new cache");
        return factor;
      }
  }

  public static void main(String[] args) {
    final int limit = 15;
    final CyclicBarrier cyclicBarrier = new CyclicBarrier(4);

    Runnable setCyclicBarrier = () -> {
      try {
        cyclicBarrier.await();
      } catch (InterruptedException | BrokenBarrierException e) {
        e.printStackTrace();
      }
    };

    new Thread(() -> {
      for (int i = 0; i < limit; i++) {
        BigInteger[] factors = Arrays.stream(ThreadLocalRandom.current().ints().limit(limit).toArray())
            .mapToObj(b -> BigInteger.valueOf(b % limit)).toArray(BigInteger[]::new);

        SafeCachingFactorizer.getImmutableCache(factors, factors[0]);
      }

      setCyclicBarrier.run();
    }).start();

    new Thread(() -> {
      for (int i = 0; i < limit; i++) {
        BigInteger[] factors = Arrays.stream(ThreadLocalRandom.current().ints().limit(limit).toArray())
            .mapToObj(b -> BigInteger.valueOf(b % limit)).toArray(BigInteger[]::new);

        SafeCachingFactorizer.getImmutableCache(factors, factors[0]);
      }

      setCyclicBarrier.run();
    }).start();

    new Thread(() -> {
      for (int i = 0; i < limit; i++) {
        BigInteger[] factors = Arrays.stream(ThreadLocalRandom.current().ints().limit(limit).toArray())
            .mapToObj(b -> BigInteger.valueOf(b % limit)).toArray(BigInteger[]::new);

        SafeCachingFactorizer.getImmutableCache(factors, factors[0]);
      }

      setCyclicBarrier.run();
    }).start();

    new Thread(() -> {
      for (int i = 0; i < limit; i++) {
        BigInteger[] factors = Arrays.stream(ThreadLocalRandom.current().ints().limit(limit).toArray())
            .mapToObj(b -> BigInteger.valueOf(b % ThreadLocalRandom.current().nextInt() % limit)).toArray(BigInteger[]::new);

        SafeCachingFactorizer.getImmutableCache(factors, factors[0]);
      }

      setCyclicBarrier.run();
    }).start();
  }
}
