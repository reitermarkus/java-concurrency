package ex1.task4;

import java.util.*;
import java.util.concurrent.*;

public class MonteCarlo implements Callable<Long> {
  private Random randomizer;
  private final Long tries;

  public MonteCarlo(Random randomizer, Long tries) {
    this.randomizer = randomizer;
    this.tries = tries;
  }

  public Long call() {
    long localSuccesses = 0L;

    for (long i = 0L; i < this.tries; i++) {
      float x = this.randomizer.nextFloat();
      float y = this.randomizer.nextFloat();

      if (x * x + y * y < 1f) {
        localSuccesses++;
      }
    }

    return localSuccesses;
  }
}
