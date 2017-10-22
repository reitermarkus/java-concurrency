package ex1.task3;

import java.util.ArrayList;
import java.util.stream.Collectors;

/**
 * Created by Michael Kaltschmid on 22.10.2017.
 */
public class MatrixUtilities {
	public static void printMatrix(ArrayList<ArrayList<Long>> matrix) {
		System.out.println("[");
		for(int i = 0; i < matrix.size(); i++){
			System.out.println(matrix.get(i));
		}
		System.out.println("]");
	}

	public static ArrayList<ArrayList<Long>> fillMatrix(int rows, int columns) {
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

	public static ArrayList<Long> getRow(ArrayList<ArrayList<Long>> matrix, int index) {
		return matrix.stream()
			.map(l -> l.get(index))
			.collect(Collectors.toCollection(ArrayList::new));
	}
}
