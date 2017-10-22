package ex1.task3;


import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Number of columns in the first matrix should be equal to the number of rows in the second matrix
*/
public class SerialMatrixMultiplication {

  private static ArrayList<ArrayList<Long>> matrix1 = new ArrayList<>();
  private static ArrayList<ArrayList<Long>> matrix2 = new ArrayList<>();

  public static void main(String[] args) {
    matrix1 = fillMatrix(4, 4);
    matrix2 = fillMatrix(4, 4);

    // final matrix has the number of columns from matrix 1 and number of rows of matrix 2
    ArrayList<ArrayList<Long>> result = fillMatrix(matrix1.size(), matrix2.get(0).size());

    // main loop to perform matrix multiplication
    for(int i = 0; i < matrix1.size(); i++) {
      for (int j = 0; j < matrix2.get(0).size(); j++) {
        result.get(i).set(j, multiply(matrix1.get(i), getRow(matrix2, j)));
      }
    }

    System.out.println(result);
  }

  private static long multiply(ArrayList<Long> column, ArrayList<Long> row) {
    return column.stream()
      .collect(Collectors.summingLong(l -> l*row.get(column.indexOf(l))));
  }

  private static ArrayList<Long> getRow(ArrayList<ArrayList<Long>> matrix, int index) {
    return matrix.stream()
      .map(l -> l.get(index))
      .collect(Collectors.toCollection(ArrayList::new));
  }

  private static ArrayList<ArrayList<Long>> fillMatrix(int rows, int columns) {
    ArrayList<ArrayList<Long>> tempMatrix = new ArrayList<>();
    for (long column = 0; column < columns; column++) {
      ArrayList<Long> columnTemp = new ArrayList<>();
      for (long row = 0; row < rows; row++) {
        columnTemp.add(row + 1);
      }
      tempMatrix.add(columnTemp);
    }
    return tempMatrix;
  }
}
