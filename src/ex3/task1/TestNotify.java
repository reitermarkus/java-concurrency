package ex3.task1;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * Created by Michael Kaltschmid on 12.11.2017.
 */
public class TestNotify implements ReadWrite {
  static boolean shutdown = false;

  private volatile int wrcur = 0;
  private volatile int wrmax = 0;
  private volatile int wrsum = 0;
  private volatile double wravg = 0;

  private volatile int rwcur = 0;
  private volatile int rwmax = 0;
  private volatile int rwsum = 0;
  private volatile double rwavg = -1;

  private volatile int wwcur = 0;
  private volatile int wwmax = 0;
  private volatile int wwsum = 0;
  private volatile double wwavg = -1;

  private volatile int totalWrites = 0;
  private volatile int totalReads = 0;

  private volatile int readersCount = 0;
  private volatile int writerCount = 0;

  private final Object writeLock = new Object();
  private final Object readLock = new Object();
  private final Object readWriteLock = new Object();

  public TestNotify() throws InterruptedException  {
    final int matrikelNr = 1514886;

    int readers = Math.max(matrikelNr % 1000, 1000 - matrikelNr % 1000);
    int writers = Math.min(matrikelNr % 1000, 1000 - matrikelNr % 1000);

    int arrayLength = 8;

    int[] array = new int[arrayLength];

    List<Thread> writerThreads = IntStream.range(0, writers).mapToObj((int i) -> new Thread(() -> {
      while (!shutdown) {
        int index = ThreadLocalRandom.current().nextInt(arrayLength - 1);
        int value = ThreadLocalRandom.current().nextInt(1, arrayLength);

        acquireWrite();

        synchronized (writeLock) {
          writerCount++;
          array[index] = value;

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

          writerCount--;
          totalWrites++;
        }

        releaseWrite();

        // System.out.println(Thread.currentThread().getName() + " wrote " + value + " to   index " + index + ".");
      }
    }, "Writer " + i)).collect(Collectors.toList());

    List<Thread> readerThreads = IntStream.range(0, readers).mapToObj(i -> new Thread(() -> {
      while (!shutdown) {
        int index = ThreadLocalRandom.current().nextInt(arrayLength - 1);

        acquireRead();

        synchronized (readLock) {
          readersCount++;

          int value = array[index];

          // Count reads before writes.
          wrcur++;
          if (wrcur > wrmax) {
            wrmax = wrcur;
          }

          // Reset writes before reads.
          rwsum += rwcur;
          rwcur = 0;

          readersCount--;
          totalReads++;
        }

        releaseRead();

        // System.out.println(Thread.currentThread().getName() + " read  " + value + " from index " + index + ".");
      }
    }, "Reader " + i)).collect(Collectors.toList());

    writerThreads.forEach(Thread::start);
    readerThreads.forEach(Thread::start);

    Thread.sleep(1000);

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

  @Override
  public void acquireRead() {
    synchronized (readWriteLock) {
      while (writerCount > 0) {
        try {
          readWriteLock.wait();
        } catch (InterruptedException e) {
          e.printStackTrace();
        }
      }
    }
  }

  @Override
  public void releaseRead() {
    synchronized (readWriteLock) {
      readWriteLock.notifyAll();
    }
  }

  @Override
  public void acquireWrite() {
    synchronized (readWriteLock) {
      while (readersCount > 0) {
        try {
          readWriteLock.wait();
        } catch (InterruptedException e) {
          e.printStackTrace();
        }
      }
    }
  }

  @Override
  public void releaseWrite() {
    synchronized (readWriteLock) {
      readWriteLock.notifyAll();
    }
  }

  public static void main(String[] args) throws InterruptedException {
    new TestNotify();
  }
}
