package ex2.task3;

import java.util.*;
import java.util.stream.*;

public class Consumer implements Runnable {
  private List<Buffer> buffers;

  Consumer(List<Buffer> buffers) {
    this.buffers = buffers;
  }

  public void run() {
    while (true) {
      this.buffers.forEach(Buffer::waitUntilAvailable);

      List<Integer> numbers = this.buffers.stream().map(b -> b.get()).collect(Collectors.toList());



      if (numbers.contains(0)) {
        System.out.println("Stopped consuming, got " + numbers + ".");
        break;
      }
    }
  }
}
