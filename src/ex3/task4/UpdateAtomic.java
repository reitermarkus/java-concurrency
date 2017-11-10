package ex3.task4;

import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.IntStream;

public class UpdateAtomic {

  private AtomicInteger counter = new AtomicInteger(0);

  public void modify(int val) {
    IntStream.rangeClosed(1, val)
      .forEach(i -> counter.incrementAndGet());
  }

  public int getCounter() {
    return counter.get();
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
