package ex2.task3;

import java.util.*;

public class Buffer {
  private List<Integer> numbers = new LinkedList<>();

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
