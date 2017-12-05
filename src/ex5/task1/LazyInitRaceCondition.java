package ex5.task1;

import java.util.stream.*;

public class LazyInitRaceCondition {
  private ExpensiveObject instance = null;

  public ExpensiveObject getInstance () {
    if (instance == null) {
      instance = new ExpensiveObject();
    }

    return instance;
  }

  public static void main(String[] args) {
    LazyInitRaceCondition raceCondition = new LazyInitRaceCondition();

    IntStream.range(0, 4).mapToObj(i -> new Thread(() -> {
      ExpensiveObject object = raceCondition.getInstance();
      System.out.println(object);
    })).forEach(t -> t.start());
  }
}
