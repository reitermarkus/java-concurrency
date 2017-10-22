package ex1.task3;

import java.util.ArrayList;
import java.util.concurrent.Callable;
import java.util.stream.Collectors;

public class MatrixMultiply implements Callable<ArrayList<ArrayList<Long>>> {

  private ArrayList<ArrayList<Long>> matrix1;
  private ArrayList<ArrayList<Long>> matrix2;
  private int minColumn;
  private int maxColumn;

  public MatrixMultiply(ArrayList<ArrayList<Long>> m1, ArrayList<ArrayList<Long>> m2, int min, int max){
    this.matrix1 = m1;
    this.matrix2 = m2;
    this.minColumn = min;
    this.maxColumn = max;
  }

  public ArrayList<ArrayList<Long>> call() {
    ArrayList<ArrayList<Long>> result = new ArrayList<>();

    // main loop to perform matrix multiplication
    for(int i = minColumn; i < maxColumn; i++) {
      ArrayList<Long> newColumn = new ArrayList<>();
      for (int j = 0; j < matrix2.get(0).size(); j++) {
        newColumn.add(j, multiply(matrix1.get(i), MatrixUtilities.getRow(matrix2, j)));
      }
      result.add(newColumn);
    }

    return result;
  }

  public long multiply(ArrayList<Long> column, ArrayList<Long> row) {
    return column.stream()
      .collect(Collectors.summingLong(l -> l*row.get(column.indexOf(l))));
  }
}
