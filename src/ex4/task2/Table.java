package ex4.task2;

import java.util.List;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * Class representing the table.
 */
public class Table {

  private List<Fork> forks;

  public Table(int size) {
    this.forks = IntStream.range(0, size)
      .mapToObj(i -> new Fork(size, i))
      .collect(Collectors.toList());
  }

  /* Get index of left fork for a threads id. */
  public int leftFork(int id) {
    return forks.indexOf(forks.stream()
      .filter(f -> f.getRightP() == id)
      .findFirst()
      .get());
  }

  /* Get index of right fork for a threads id. */
  public int rightFork(int id) {
    return forks.indexOf(forks.stream()
      .filter(f -> f.getLeftP() == id)
      .findFirst()
      .get());
  }

  public synchronized boolean takeForks(int id) {
    if (forks.get(leftFork(id)).isAvailable() && forks.get(rightFork(id)).isAvailable()) {
      forks.get(leftFork(id)).setAvailable(false);
      forks.get(rightFork(id)).setAvailable(false);
      return true;
    }
    try {
      System.out.println("Thread " + id + " is now thinking!");
      wait();
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
    return false;
  }

  public synchronized boolean putBackFork(int id) {
    if (!forks.get(leftFork(id)).isAvailable() && !forks.get(rightFork(id)).isAvailable()) {
      forks.get(leftFork(id)).setAvailable(true);
      forks.get(rightFork(id)).setAvailable(true);
      notifyAll();
      return true;
    }
    return false;
  }

  public static void main(String[] args) {
    Table table = new Table(5);

    IntStream.range(0, 5)
      .mapToObj(i -> new Philosopher(i, "name", table))
      .forEach(p -> {
        Thread t = new Thread(p);
        t.start();
      });
  }

}
