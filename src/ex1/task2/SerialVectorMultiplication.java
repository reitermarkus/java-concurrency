package ex1.task2;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by Lukas Dötlinger.
 */
public class SerialVectorMultiplication {

  private static List<Integer> vector1 = new ArrayList<>();
  private static List<Integer> vector2 = new ArrayList<>();

  public static void main(String[] args){

    for(int i = 1; i <= 500; i++){
      vector1.add(i);
      vector2.add(i);
    }

    int result = vector1.stream()
      .map(j -> j*vector2.get(vector1.indexOf(j)))
      .collect(Collectors.summingInt(Integer::intValue));

    System.out.println(result);
  }
}
