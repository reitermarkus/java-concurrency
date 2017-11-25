package ex4.task2;

import static java.lang.Thread.sleep;

/**
 * Class representing a Philosopher.
 */
public class Philosopher implements Runnable {

  private int id;
  private boolean eating;
  private String name;
  private Table table;

  public Philosopher(int id, String name, Table table) {
    this.id = id;
    this.eating = false;
    this.name = name;
    this.table = table;
  }

  public void eat() throws InterruptedException {
    if (table.takeForks(id)) {
      System.out.println("Thread " + id + " is now eating!");
      sleep(3000);
      table.putBackFork(id);
    }
  }

  @Override
  public void run() {
    while (true) {
      try {
        eat();
      } catch (InterruptedException e) {
        e.printStackTrace();
      }
    }
  }
}
