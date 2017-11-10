package ex3.task1;

import java.util.concurrent.locks.*;

public class Database implements ReadWrite {
  ReadWriteLock lock = new ReentrantReadWriteLock();
  Lock reader = lock.readLock();
  Lock writer = lock.writeLock();

  public void acquireRead() {
    reader.lock();
  }

  public void releaseRead() {
    reader.unlock();
  }

  public void acquireWrite() {
    writer.lock();
  }

  public void releaseWrite() {
    writer.unlock();
  }
}
