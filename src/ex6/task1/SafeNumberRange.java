package ex6.task1;

import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.*;
import java.util.stream.*;

/**
 * NumberRange
 * <p/>
 * Number range class that does not sufficiently protect its invariants
 *
 * @author Brian Goetz and Tim Peierls
 */

public class SafeNumberRange {
  // INVARIANT: lower <= upper
  private Integer lower = 0;
  private Integer upper = 0;

  public synchronized void setLower(int i) {
    if (i > upper) {
      throw new IllegalArgumentException("can't set lower to " + i + " > upper");
    }

    lower = i;
  }

  public synchronized void setUpper(int i) {
    if (i < lower) {
      throw new IllegalArgumentException("can't set upper to " + i + " < lower");
    }

    upper = i;
  }

  public synchronized boolean isInRange(int i) {
    return (i >= lower && i <= upper);
  }

  public static void main(String[] args) throws InterruptedException {

    SafeNumberRange range = new SafeNumberRange();

    List<Thread> threads = IntStream.range(0, 4).mapToObj(i -> new Thread(() -> {
      ThreadLocalRandom random = ThreadLocalRandom.current();

      while (true) {
        Integer lower = random.nextInt();
        Integer upper = random.nextInt();

        try {
          range.setLower(lower);
        } catch (IllegalArgumentException e) {}

        try {
          range.setUpper(upper);
        } catch (IllegalArgumentException e) {}
      }
    })).collect(Collectors.toList());

    Thread printerThread = new Thread(() -> {
      ThreadLocalRandom random = ThreadLocalRandom.current();

      while (true) {
        Integer num = random.nextInt();

        System.out.println("Is " + num + " in range? " + range.isInRange(num));
        try {
          Thread.sleep(1000);
        } catch (InterruptedException e) {
          e.printStackTrace();
        }
      }
    });

    for (Thread t: threads) t.start();
    printerThread.start();

    for (Thread t: threads) t.join();
    printerThread.join();
  }
}

