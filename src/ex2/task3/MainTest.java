package ex2.task3;

import java.util.*;
import java.util.stream.*;

public class MainTest {
  public static void main(String[] args) {
    int producerCount = 4;

    List<Buffer> buffers = IntStream.range(0, producerCount)
      .mapToObj(i -> new Buffer("Buffer " + (i + 1)))
      .collect(Collectors.toList());

    List<Thread> producers = IntStream.range(0, producerCount)
      .mapToObj(i -> new Thread(new Producer(buffers.get(i)), "Producer " + (i + 1)))
      .collect(Collectors.toList());

    Thread consumer = new Thread(new Consumer(buffers), "Consumer 1");

    producers.forEach(p -> p.start());
    consumer.start();
  }
}
