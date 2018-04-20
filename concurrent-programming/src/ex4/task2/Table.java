package ex4.task2;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * Class representing the table.
 */
public class Table {

  private int size;
  private List<Fork> forks;
  private String alphabet = "abcdefghijklmnopqrstuvwxyz";

  public Table(int size) {
    this.size = size;
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

  public synchronized boolean takeForks(int id, String name) {
    if (forks.get(leftFork(id)).isAvailable() && forks.get(rightFork(id)).isAvailable()) {
      forks.get(leftFork(id)).setAvailable(false);
      forks.get(rightFork(id)).setAvailable(false);
      System.out.println(name + " " + id + " is now eating!");
      return true;
    }
    try {
      System.out.println(name + " " + id + " is now thinking!");
      wait();
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
    return false;
  }

  public synchronized boolean putBackFork(int id) throws InterruptedException {
    if (!forks.get(leftFork(id)).isAvailable() && !forks.get(rightFork(id)).isAvailable()) {
      forks.get(leftFork(id)).setAvailable(true);
      forks.get(rightFork(id)).setAvailable(true);
      notifyAll();
      /* Let Philosopher think after eating. */
      wait();
      return true;
    }
    return false;
  }

  public String generateName() {
    return IntStream.rangeClosed(0, ThreadLocalRandom.current().nextInt(5, 9))
      .mapToObj(i -> {
        if (i == 0) {
          return Character.toUpperCase(alphabet.charAt(ThreadLocalRandom.current().nextInt(0, 25)));
        } else {
          return alphabet.charAt(ThreadLocalRandom.current().nextInt(0, 25));
        }
      })
      .map(i -> String.valueOf(i))
      .collect(Collectors.joining());
  }

  public static void main(String[] args) {
    Table table = new Table(5);

    IntStream.range(0, table.size)
      .mapToObj(i -> new Philosopher(i, table.generateName(), table))
      .forEach(p -> {
        Thread t = new Thread(p);
        t.start();
      });
  }

}
