package ex2.task3;

import java.util.*;

public class Buffer {
  private static int nameCounter = 0;

  private String name;

  private List<Integer> numbers = new LinkedList<>();

  Buffer() {
    this.name = "Buffer-" + nameCounter;
    nameCounter++;
  }

  Buffer(String name) {
    this();
    this.name = name;
  }

  public String getName() {
    return this.name;
  }

  public void waitUntilAvailable() {
    while (true) {
      synchronized (this.numbers) {
        if (!this.numbers.isEmpty()) {
          break;
        }
      }

      synchronized (this) {
        try {
          wait();
        } catch (InterruptedException e) { }
      }
    }
  }

  public synchronized void put(Integer number) {
    this.numbers.add(number);
    notifyAll();
  }

  public synchronized Integer get() {
    return this.numbers.remove(0);
  }
}
