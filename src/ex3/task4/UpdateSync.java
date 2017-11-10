package ex3.task4;

import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.IntStream;

public class UpdateSync {

  private int counter = 0;

  public synchronized void modify(int val) {
    this.counter += val;
  }

  public synchronized int getCounter() {
    return counter;
  }

  public static void main(String[] args) {

    UpdateSync instance = new UpdateSync();

    IntStream.rangeClosed(1, 10)
      .mapToObj(i -> new Thread(() -> {
        instance.modify(ThreadLocalRandom.current().nextInt(1, 6));
        System.out.println("Thread " + i + " increased counter to: " + instance.getCounter());
      }))
      .forEach(t -> t.start());
  }
}
