package ex3.task4;

import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.IntStream;

public class UpdateAtomic {

  private AtomicInteger counter = new AtomicInteger(0);

  public int modify(int val) {
    return counter.addAndGet(val);
  }

  public static void main(String[] args) {

    UpdateSync instance = new UpdateSync();

    IntStream.rangeClosed(1, 10)
      .mapToObj(i -> new Thread(() -> {
        int counter = instance.modify(ThreadLocalRandom.current().nextInt(1, 6));
        System.out.println("Thread " + i + " increased counter to: " + counter);
      }))
      .forEach(t -> t.start());
  }

}
