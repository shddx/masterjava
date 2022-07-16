package ru.javaops.masterjava.matrix;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.stream.IntStream;

/**
 * gkislin
 * 03.07.2016
 */
public class MatrixUtil {

    // TODO implement parallel multiplication matrixA*matrixB
    public static int[][] concurrentMultiply(int[][] matrixA, int[][] matrixB, ExecutorService executor) throws InterruptedException, ExecutionException {
        final int matrixSize = matrixA.length;
        final int[][] matrixC = new int[matrixSize][matrixSize];
        List<Future<int[]>> futures = new ArrayList<>();
        int[][] matrixBTrans = new int[matrixSize][matrixSize];
        for (int i = 0; i < matrixSize; i++) {
            for (int j = 0; j < matrixSize; j++) {
                matrixBTrans[i][j] = matrixB[j][i];
            }
        }
        for (int[] row : matrixA) {
            Future<int[]> future = executor.submit(() -> multiplyOneColumn(row, matrixBTrans));
            futures.add(future);
        }
        for (int i = 0; i < futures.size(); i++) {
            matrixC[i] = futures.get(i).get();
        }
        return matrixC;
    }

    private static int[] multiplyOneColumn(int[] row, int[][] matrix) {
        int size = row.length;
        int[] result = new int[size];
        for (int i = 0; i < size; i++) {
            result[i] = multiplySingle(row, matrix[i]);
        }
        return result;
    }

    private static int multiplySingle(int[] column, int[] row) {
        return IntStream.range(0, column.length)
                .map(i -> column[i] * row[i])
                .sum();
    }

    // TODO optimize by https://habrahabr.ru/post/114797/
    public static int[][] singleThreadMultiply(int[][] matrixA, int[][] matrixB) {
        final int matrixSize = matrixA.length;
        final int[][] matrixC = new int[matrixSize][matrixSize];

        int[] column = new int[matrixSize];
        for (int i = 0; i < matrixSize; i++) {
            for (int k = 0; k < matrixSize; k++) {
                column[k] = matrixB[k][i];
            }

            for (int j = 0; j < matrixSize; j++) {
                int[] row = matrixA[j];
                int sum = 0;
                for (int k = 0; k < matrixSize; k++) {
                    sum += row[k] * column[k];
                }
                matrixC[j][i] = sum;
            }
        }
        return matrixC;
    }

    public static int[][] create(int size) {
        int[][] matrix = new int[size][size];
        Random rn = new Random();

        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                matrix[i][j] = rn.nextInt(10);
            }
        }
        return matrix;
    }

    public static boolean compare(int[][] matrixA, int[][] matrixB) {
        final int matrixSize = matrixA.length;
        for (int i = 0; i < matrixSize; i++) {
            for (int j = 0; j < matrixSize; j++) {
                if (matrixA[i][j] != matrixB[i][j]) {
                    return false;
                }
            }
        }
        return true;
    }
}
