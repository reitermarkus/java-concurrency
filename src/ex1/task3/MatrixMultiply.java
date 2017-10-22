package ex1.task3;

import java.util.ArrayList;
import java.util.concurrent.Callable;
import java.util.stream.Collectors;

public class MatrixMultiply implements Callable<ArrayList<ArrayList<Long>>> {

  private static ArrayList<ArrayList<Long>> matrix1 = new ArrayList<>();
  private static ArrayList<ArrayList<Long>> matrix2 = new ArrayList<>();
  private static int minColumn;
  private static int maxColumn;

  public MatrixMultiply(ArrayList<ArrayList<Long>> m1, ArrayList<ArrayList<Long>> m2, int min, int max){
    this.matrix1 = m1;
    this.matrix2 = m2;
    this.minColumn = min;
    this.maxColumn = max;
  }

  public ArrayList<ArrayList<Long>> call() {
    ArrayList<ArrayList<Long>> result = new ArrayList<>();

    // main loop to perform matrix multiplication
    for(int i = minColumn; i <= maxColumn; i++) {
      ArrayList<Long> newColumn = new ArrayList<>();
      for (int j = 0; j < matrix2.get(0).size(); j++) {
        newColumn.add(j, multiply(matrix1.get(i), getRow(matrix2, j)));
      }
      result.add(newColumn);
    }

    return result;
  }

  private long multiply(ArrayList<Long> column, ArrayList<Long> row) {
    return column.stream()
      .collect(Collectors.summingLong(l -> l*row.get(column.indexOf(l))));
  }

  private ArrayList<Long> getRow(ArrayList<ArrayList<Long>> matrix, int index) {
    return matrix.stream()
      .map(l -> l.get(index))
      .collect(Collectors.toCollection(ArrayList::new));
  }
}
