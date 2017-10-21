package ex1.task3;


import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

//Number of columns in the first matrix should be equal to the number of rows in the second matrix

public class SerialMatrixMultiplication {

  private static ArrayList<ArrayList<Long>> matrix1 = new ArrayList<>();
  private static ArrayList<ArrayList<Long>> matrix2 = new ArrayList<>();

  public static void main(String[] args){
    // final matrix has the number of columns from matrix 1 and number of rows of matrix 2

    for(long row = 0; row < 4; row++){
      ArrayList<Long> columnTemp = new ArrayList<>();
      for(long column = 0; column < 4; column++){
        columnTemp.add(1L+column);
      }
      matrix1.add(columnTemp);
      matrix2.add(columnTemp);
    }

    ArrayList<ArrayList<Long>> result = new ArrayList<>();

    for(long finalRow = 0; finalRow < matrix1.size(); finalRow++){
      ArrayList<Long> temp = new ArrayList<>();
      for(long finalColumn = 0; finalColumn < matrix2.get(0).size(); finalColumn++){
        temp.add(0L);
      }
      result.add(temp);
    }

    for(int i = 0; i < matrix1.size(); i++){
      for(int j = 0; j < matrix2.get(0).size(); j++){
        result.get(i).set(j, multiply(matrix1.get(i), getRow(matrix2, j)));
      }
    }

    System.out.println(result);
  }

  private static long multiply(ArrayList<Long> column, ArrayList<Long> row){
    return column.stream()
      .collect(Collectors.summingLong(l -> l*row.get(column.indexOf(l))));
  }

  private static ArrayList<Long> getRow(ArrayList<ArrayList<Long>> matrix, int index){
    return matrix.stream()
      .map(l -> l.get(index))
      .collect(Collectors.toCollection(ArrayList::new));
  }
}
