package ex2.task3;

import java.util.*;
import java.util.stream.*;

public class Consumer implements Runnable {
  private List<Buffer> buffers;

  Consumer(final List<Buffer> buffers) {
    this.buffers = buffers;
  }

  public void run() {
    System.out.println(Thread.currentThread().getName() + " started consuming.");

    while (!buffers.isEmpty()) {
      this.buffers.stream().forEach(Buffer::waitUntilAvailable);

      this.buffers = this.buffers.stream()
        .filter(b -> {
          Integer number = b.get();

          if (number == 0) {
            System.out.println(Thread.currentThread().getName() + " stopped consuming from " + b.getName() + ".");
            return false;
          }

          return true;
        })
        .collect(Collectors.toList());
    }
  }
}
