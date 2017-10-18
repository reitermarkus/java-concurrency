package ex1.task4;

import java.util.*;

public class SerialPiCalculation {
  public static void main(String[] args) {
    Random randomizer = new Random();

    long tries = 1000000;

    long successes = 0;

    for (long i = 0; i < tries; i++) {
      float x = randomizer.nextFloat();
      float y = randomizer.nextFloat();

      if (x * x + y * y < 1f) {
        successes++;
      }
    }

    float quarterPi = (float) successes / (float) tries;
    float pi = quarterPi * 4f;

    System.out.println(pi);
  }
}
