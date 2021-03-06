package ex2.task3;

import java.util.*;
import java.util.concurrent.*;

public class Producer implements Runnable {
  private static int MAX = 100;

  private Buffer buffer;

  Producer(Buffer buffer) {
    this.buffer = buffer;
  }

  public void run() {
    System.out.println(Thread.currentThread().getName() + " started producing.");

    Random randomizer = ThreadLocalRandom.current();

    while (true) {
      Integer number = randomizer.nextInt(MAX);

      this.buffer.put(number);

      if (number == 0) {
        System.out.println(Thread.currentThread().getName() + " stopped producing.");
        break;
      }
    }
  }
}
