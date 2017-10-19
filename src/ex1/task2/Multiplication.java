package ex1.task2;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.stream.Collectors;

/**
 * Created by Lukas Dötlinger.
 */
public class Multiplication implements Callable<Integer> {

  private static List<Integer> vector1 = new ArrayList<>();
  private static List<Integer> vector2 = new ArrayList<>();

  public Multiplication(ArrayList vec1, ArrayList vec2) {
    this.vector1 = vec1;
    this.vector2 = vec2;
  }

  public Integer call() {
    return vector1.stream()
      .map(j -> j*vector2.get(vector1.indexOf(j)))
      .collect(Collectors.summingInt(Integer::intValue));
  }

}
