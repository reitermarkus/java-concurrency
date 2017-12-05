package ex5.task1;

import java.util.concurrent.atomic.*;

public class ExpensiveObject {

  private static AtomicInteger counter = new AtomicInteger(0);

  ExpensiveObject() {
    try {
      Thread.sleep(counter.incrementAndGet() * 1000);
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
  }
}
