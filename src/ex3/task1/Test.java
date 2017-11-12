package ex3.task1;

import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.locks.*;
import java.util.stream.*;

public class Test {
  static volatile boolean shutdown = false;

  static Lock variableLock = new StampedLock().asReadLock();

  static int wrcur = 0;
  static int wrmax = 0;
  static int wrsum = 0;
  static double wravg = 0;

  static int rwcur = 0;
  static int rwmax = 0;
  static int rwsum = 0;
  static double rwavg = -1;

  static int wwcur = 0;
  static int wwmax = 0;
  static int wwsum = 0;
  static double wwavg = -1;

  static int totalWrites = 0;
  static int totalReads = 0;

  public static void main(String[] args) throws InterruptedException {
    final int matrikelNr = 1514886;

    int readers = Math.max(matrikelNr % 1000, 1000 - matrikelNr % 1000);
    int writers = Math.min(matrikelNr % 1000, 1000 - matrikelNr % 1000);

    int arrayLength = 8;

    int[] array = new int[arrayLength];

    ReadWrite db = new Database();

    List<Thread> writerThreads = IntStream.range(0, writers).mapToObj(i -> new Thread(() -> {
      while (!shutdown) {
        int index = ThreadLocalRandom.current().nextInt(arrayLength - 1);
        int value = ThreadLocalRandom.current().nextInt(1, arrayLength);

        db.acquireWrite();

        array[index] = value;

        db.releaseWrite();

        variableLock.lock();

        // Count writes before writes.
        if (wwcur > wwmax) {
          wwmax = wwcur;
        }

        wwsum += wwcur;
        wwcur = 0;
        wwcur++;

        // Count writes before reads.
        rwcur++;
        if (rwcur > rwmax) {
          rwmax = rwcur;
        }

        // Reset reads before writes.
        wrsum += wrcur;
        wrcur = 0;

        totalWrites++;

        variableLock.unlock();

        // System.out.println(Thread.currentThread().getName() + " wrote " + value + " to   index " + index + ".");
      }
    }, "Writer " + i)).collect(Collectors.toList());

    List<Thread> readerThreads = IntStream.range(0, readers).mapToObj(i -> new Thread(() -> {
      while (!shutdown) {
        int index = ThreadLocalRandom.current().nextInt(arrayLength - 1);

        db.acquireRead();

        int value = array[index];

        db.releaseRead();

        variableLock.lock();

        // Count reads before writes.
        wrcur++;
        if (wrcur > wrmax) {
          wrmax = wrcur;
        }

        // Reset writes before reads.
        rwsum += rwcur;
        rwcur = 0;

        totalReads++;

        variableLock.unlock();

        // System.out.println(Thread.currentThread().getName() + " read  " + value + " from index " + index + ".");
      }
    }, "Reader " + i)).collect(Collectors.toList());

    writerThreads.forEach(Thread::start);
    readerThreads.forEach(Thread::start);

    Thread.sleep(10000);

    shutdown = true;

    for (Thread t: writerThreads) t.join();
    for (Thread t: readerThreads) t.join();

    wravg = (double)wrsum / (double)totalWrites;
    rwavg = (double)rwsum / (double)totalReads;
    wwavg = (double)wwsum / (double)totalWrites;

    System.out.println();
    System.out.println("totalReads = " + totalReads);
    System.out.println("totalWrites = " + totalWrites);

    System.out.println();
    System.out.println("reads before writes:");
    System.out.println("wrmax = " + wrmax);
    System.out.println("wravg = " + wravg);

    System.out.println();
    System.out.println("writes before reads:");
    System.out.println("rwmax = " + rwmax);
    System.out.println("rwavg = " + rwavg);

    System.out.println();
    System.out.println("writes before writes:");
    System.out.println("wwmax = " + wwmax);
    System.out.println("wwavg = " + wwavg);
  }
}
