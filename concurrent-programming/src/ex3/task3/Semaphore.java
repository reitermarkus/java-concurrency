package ex3.task3;

import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.IntStream;

/**
 * Matr. Numbers:
 *  Lukas Dötlinger -> 01518316
 *  Markus Reiter -> 01518446
 *  Michael Kaltschmid -> 01518956
 */
public class Semaphore {

  private ReentrantLock lock = new ReentrantLock();
  private Condition condition = lock.newCondition();
  private int capacity;
  private int threads;

  public Semaphore(){
    this.threads = 0;
    this.capacity = calcCapacity();
  }

  public int calcCapacity(){
    int random = ThreadLocalRandom.current().nextInt(1, 4);
    return IntStream.of(
      0, 1, 5, 1, 8, 3, 1, 6,
      0, 1, 5, 1, 8, 4, 4, 6,
      0, 1, 5, 1, 8, 9, 5, 6)
      .limit(random*8)
      .skip((random-1)*8)
      .sum();
  }

  /* acquire */
  public void P(int x){
    lock.lock();
    try {
      while ((capacity - threads) < x){
        condition.await();
      }
      threads += x;
      Thread.sleep(1000);
    } catch (InterruptedException e) {
      e.printStackTrace();
    } finally {
      lock.unlock();
    }
  }

  /* release */
  public void V(int x){
    lock.lock();
    try {
      threads -= x;
      Thread.sleep(1000);
      condition.signalAll();
    } catch (InterruptedException e) {
      e.printStackTrace();
    } finally {
      lock.unlock();
    }
  }

  public static void main(String[] args){

    Semaphore sem = new Semaphore();

    System.out.println("Capacity: " + sem.capacity);

    IntStream.rangeClosed(1, 50)
      .mapToObj(i -> new Thread(() -> {
        int localRandom = ThreadLocalRandom.current().nextInt(1, 10);
        sem.P(localRandom);
        System.out.println("Thread " + i + " acquired " + localRandom + " resources!");
        sem.V(localRandom);
        System.out.println("Thread " + i + " released " + localRandom + " resources!");
      }
      ))
      .forEach(t -> t.start());

  }
}
