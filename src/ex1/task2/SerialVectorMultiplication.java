package ex1.task2;

import java.util.Vector;
import java.util.stream.Collectors;

/**
 * Created by Lukas Dötlinger.
 */
public class SerialVectorMultiplication {

  private static Vector<Long> vector1 = new Vector<>();
  private static Vector<Long> vector2 = new Vector<>();

  public static void main(String[] args) {
    long startTime = System.currentTimeMillis();

    for(int i = 1; i <= 10000; i++) {
      vector1.add((long) i);
      vector2.add((long) i);
    }

    long result = vector1.stream()
      .map(j -> j*vector2.get(vector1.indexOf(j)))
      .collect(Collectors.summingLong(Long::longValue));

    System.out.println(result);

    long endTime = System.currentTimeMillis();
    System.out.println("This took " + (endTime-startTime) + " milliseconds!");
  }
}
