package ex1.task3;


import java.util.ArrayList;
import java.util.stream.Collectors;

/**
 * Number of columns in the first matrix should be equal to the number of rows in the second matrix
*/
public class SerialMatrixMultiplication {

  private static ArrayList<ArrayList<Long>> matrix1 = new ArrayList<>();
  private static ArrayList<ArrayList<Long>> matrix2 = new ArrayList<>();

  public static void main(String[] args) {
    long startTime = System.currentTimeMillis();

    matrix1 = MatrixUtilities.fillMatrix(100, 100);
    matrix2 = MatrixUtilities.fillMatrix(100, 100);

    // final matrix has the number of columns from matrix 1 and number of rows of matrix 2
    ArrayList<ArrayList<Long>> result = MatrixUtilities.fillMatrix(matrix1.size(), matrix2.get(0).size());

    // main loop to perform matrix multiplication
    for(int i = 0; i < matrix1.size(); i++) {
      for (int j = 0; j < matrix2.get(0).size(); j++) {
        result.get(i).set(j, multiply(matrix1.get(i), MatrixUtilities.getRow(matrix2, j)));
      }
    }

    MatrixUtilities.printMatrix(result);

    long endTime = System.currentTimeMillis();
    System.out.println("This took " + (endTime-startTime) + " milliseconds!");
  }

  private static long multiply(ArrayList<Long> column, ArrayList<Long> row) {
    return column.stream()
      .collect(Collectors.summingLong(l -> l*row.get(column.indexOf(l))));
  }
}
