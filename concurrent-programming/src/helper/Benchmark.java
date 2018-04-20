package helper;

import java.util.function.*;

public class Benchmark {
  public static void measure(String desc, Runnable function) {
    measure(desc, () -> {
      function.run();
      return null;
    });
  }

  public static <T> T measure(String desc, Supplier<T> function) {
    long startTime = System.currentTimeMillis();

    T result = function.get();

    long endTime = System.currentTimeMillis();

    System.out.println("'" + desc + "' took " + (endTime - startTime) + " milliseconds.");

    return result;
  }
}
