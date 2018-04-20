package ex3.task4;

import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.IntStream;

public class UpdateSync {

  private int counter = 0;

  public synchronized int modify(int val) {
    this.counter += val;
    return this.counter;
  }

  public static void main(String[] args) {

    UpdateSync instance = new UpdateSync();

    IntStream.rangeClosed(1, 10)
      .mapToObj(i -> new Thread(() -> {
        int value = instance.modify(ThreadLocalRandom.current().nextInt(1, 6));
        System.out.println("Thread " + i + " increased counter to: " + value);
      }))
      .forEach(t -> t.start());
  }
}
