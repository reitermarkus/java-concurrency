package ex1.task2;

import java.util.Vector;
import java.util.concurrent.Callable;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * Created by Lukas Dötlinger.
 */
public class Multiplication implements Callable<Integer> {

  private static Vector<Integer> vector1 = new Vector<>();
  private static Vector<Integer> vector2 = new Vector<>();
  private static int min = 0;
  private static int max = 0;

  public Multiplication(Vector vec1, Vector vec2, int min, int max) {
    this.vector1 = vec1;
    this.vector2 = vec2;
    this.min = min;
    this.max = max;
  }

  public Integer call() {
    return IntStream.rangeClosed(min, max)
      .mapToObj(i -> vector1.get(i-1)* vector2.get(i-1))
      .collect(Collectors.summingInt(Integer::intValue));
  }

}
