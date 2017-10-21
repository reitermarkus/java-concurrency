package ex1.task3;


import java.util.ArrayList;

//Number of columns in the first matrix should be equal to the number of rows in the second matrix

public class SerialMatrixMultiplication {

  private static ArrayList<ArrayList<Long>> matrix1 = new ArrayList<>();
  private static ArrayList<ArrayList<Long>> matrix2 = new ArrayList<>();

  public static void main(String[] args){
    // final matrix has the number of columns from matrix 1 and number of rows of matrix 2

    for(long row = 0; row < 4; row++){
      ArrayList<Long> columnTemp = new ArrayList<>();
      for(long column = 0; column < 4; column++){
        columnTemp.add(3L+column);
      }
      matrix1.add(columnTemp);
      matrix2.add(columnTemp);
    }

    ArrayList<ArrayList<Long>> result = new ArrayList<>();

    for(long finalRow = 0; finalRow <= matrix1.size(); finalRow++){
      ArrayList<Long> temp = new ArrayList<>();
      for(long finalColumn = 0; finalColumn < matrix2.get(0).size(); finalColumn++){
        temp.add(0L);
      }
      result.add(temp);
    }

    result.stream()
      .forEach(column -> {
        column.stream()
          .map(i -> {
            final long[] retVal = {0L};
            matrix1.get(result.indexOf(column)).stream()
              .forEach(r -> retVal[0] = r*matrix2.get(matrix1.indexOf(r)).get(result.indexOf(column)));
            return 5;
          });
      });

    System.out.println(result);
  }

}
