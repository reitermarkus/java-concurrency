package ex3.task2;

import ex3.task1.*;

public class Database implements ReadWrite {
  private final Object readLock = new Object();
  private final Object readWriteLock = new Object();
  private final Object writeLock = new Object();

  private int readers = 0;
  private int writers = 0;

  public void acquireRead() {
    synchronized (readWriteLock) {
      while (writers > 0) {
        try {
          readWriteLock.wait();
        } catch (InterruptedException e) {
          e.printStackTrace();
        }
      }
    }

    synchronized (readLock) {
      readers++;
    }
  }

  public void releaseRead() {
    synchronized (readLock) {
      readers--;
    }

    synchronized (readWriteLock) {
      readWriteLock.notifyAll();
    }
  }

  public void acquireWrite() {
    synchronized (readWriteLock) {
      while (readers > 0) {
        try {
          readWriteLock.wait();
        } catch (InterruptedException e) {
          e.printStackTrace();
        }
      }
    }

    synchronized (writeLock) {
      writers++;
    }
  }

  public void releaseWrite() {
    synchronized (writeLock) {
      writers--;
    }

    synchronized (readWriteLock) {
      readWriteLock.notifyAll();
    }
  }
}
