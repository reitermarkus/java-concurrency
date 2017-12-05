package ex5.task1;

import java.util.concurrent.atomic.*;
import java.util.stream.*;

public class AtomicLazyInit {
  private AtomicReference<ExpensiveObject> instance = new AtomicReference<>();

  public ExpensiveObject getInstance () {
    ExpensiveObject object = instance.get();

    if (object == null) {
      ExpensiveObject newObject = new ExpensiveObject();

      if (instance.compareAndSet(object, newObject)) {
        return newObject;
      } else {
        return instance.get();
      }
    }

    return object;
  }

  public static void main(String[] args) {
    AtomicLazyInit raceCondition = new AtomicLazyInit();

    IntStream.range(0, 4).mapToObj(i -> new Thread(() -> {
      ExpensiveObject object = raceCondition.getInstance();
      System.out.println(object);
    })).forEach(t -> t.start());
  }
}
