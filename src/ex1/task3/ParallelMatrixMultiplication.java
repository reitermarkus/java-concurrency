package ex1.task3;


import java.util.ArrayList;
import java.util.Objects;
import java.util.concurrent.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;


/**
 * Number of columns in the first matrix should be equal to the number of rows in the second matrix
 */
public class ParallelMatrixMultiplication {

  private static ArrayList<ArrayList<Long>> matrix1 = new ArrayList<>();
  private static ArrayList<ArrayList<Long>> matrix2 = new ArrayList<>();

  public static void main(String[] args) {
    long startTime = System.currentTimeMillis();

    matrix1 = fillMatrix(100, 100);
    matrix2 = fillMatrix(100, 100);

    // final matrix has the number of columns from matrix 1 and number of rows of matrix 2

    final int threadCount = Runtime.getRuntime().availableProcessors();
    final ExecutorService pool = Executors.newFixedThreadPool(threadCount);

    Stream<Future<ArrayList<ArrayList<Long>>>> futures = IntStream.rangeClosed(1, threadCount).mapToObj(i -> {
      int minColumn = (i * (matrix1.size()/threadCount)) - (matrix1.size()/threadCount);
      int maxColumn = (i * (matrix1.size()/threadCount)) - 1;
      if(i == threadCount) {maxColumn = matrix1.size();}
      Callable<ArrayList<ArrayList<Long>>> task = new MatrixMultiply(matrix1, matrix2, minColumn, maxColumn);
      return pool.submit(task);
    });

    Stream<ArrayList<ArrayList<Long>>> lists = futures
      .map(i -> {
        try {
          return i.get();
        } catch (Exception e) {
          e.printStackTrace();
          return null;
        }
      });

    ArrayList<ArrayList<Long>> result = lists
      .filter(Objects::nonNull)
      .flatMap(i -> i.stream())
      .collect(Collectors.toCollection(ArrayList::new));

    pool.shutdown();

    printMatrix(result);

    long endTime = System.currentTimeMillis();
    System.out.println("This took " + (endTime-startTime) + " milliseconds!");
  }

  private static void printMatrix(ArrayList<ArrayList<Long>> matrix) {
    System.out.println("[");
    for(int i = 0; i < matrix.size(); i++){
      System.out.println(matrix.get(i));
    }
    System.out.println("]");
  }

  private static ArrayList<ArrayList<Long>> fillMatrix(int rows, int columns) {
    ArrayList<ArrayList<Long>> mTemp = new ArrayList<>();
    for (long c = 0; c < columns; c++) {
      ArrayList<Long> cTemp = new ArrayList<>();
      for (long r = 0; r < rows; r++) {
        cTemp.add(r + 1);
      }
      mTemp.add(cTemp);
    }
    return mTemp;
  }

}
