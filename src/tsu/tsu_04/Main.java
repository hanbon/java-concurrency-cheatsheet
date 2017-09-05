package tsu.tsu_04;

import java.util.Random;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;

/**
 * 使用 CyclicBarrier 类完成分治编程技术
 * 在一个大矩阵中寻找一个数字。
 * 将大矩阵分为几个子集，每个线程在一个子集中查找。一旦所有线程都完成查找，最终的任务将统一这些结果。
 */
public class Main {

    public static void main(String[] args) {

        final int ROWS = 10000;
        final int NUMBERS = 1000;
        final int SEARCH = 5;
        final int PARTICIPANTS = 5;
        final int LINES_PARTICIPANT = 2000;
        MatrixMock mock = new MatrixMock(ROWS, NUMBERS, SEARCH);

        // 存储计算结果
        Results results = new Results(ROWS);

        // 执行合并的对象
        Grouper grouper = new Grouper(results);

        CyclicBarrier barrier = new CyclicBarrier(PARTICIPANTS, grouper);

        Searcher searchers[] = new Searcher[PARTICIPANTS];
        for (int i = 0; i < PARTICIPANTS; i++) {
            searchers[i] = new Searcher(i * LINES_PARTICIPANT, (i * LINES_PARTICIPANT) + LINES_PARTICIPANT, mock, results, 5, barrier);
            Thread thread = new Thread(searchers[i]);
            thread.start();
        }
        System.out.printf("Main: The main thread has finished.\n");

    }

}

/**
 * 模拟被查找的矩阵
 */
class MatrixMock {

    private final int data[][];

    public MatrixMock(int size, int length, int number) {
        int counter = 0;
        data = new int[size][length];
        Random random = new Random();
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < length; j++) {
                data[i][j] = random.nextInt(10);
                if (data[i][j] == number) {
                    counter++;
                }
            }
        }
        System.out.printf("Mock: There are %d occurrences of number %d in generated data.\n", counter, number);
    }

    public int[] getRow(int row) {
        if ((row >= 0) && (row < data.length)) {
            return data[row];
        }
        return null;
    }

}

/**
 * 用来存储矩阵中每一行出现的目标数字的个数
 */
class Results {

    private final int data[];

    public Results(int size) {
        data = new int[size];
    }

    public void setData(int position, int value) {
        data[position] = value;
    }

    public int[] getData() {
        return data;
    }
}

/**
 * 每个子任务，负责矩阵中一个子集的查找
 */
class Searcher implements Runnable {

    private final int firstRow;
    private final int lastRow;
    private final MatrixMock mock;
    private final Results results;
    private final int number;
    private final CyclicBarrier barrier;

    public Searcher(int firstRow, int lastRow, MatrixMock mock, Results results, int number, CyclicBarrier barrier) {
        this.firstRow = firstRow;
        this.lastRow = lastRow;
        this.mock = mock;
        this.results = results;
        this.number = number;
        this.barrier = barrier;
    }

    @Override
    public void run() {
        int counter;
        System.out.printf("%s: Processing lines from %d to %d.\n", Thread.currentThread().getName(), firstRow, lastRow);
        for (int i = firstRow; i < lastRow; i++) {
            int row[] = mock.getRow(i);
            counter = 0;
            for (int aRow : row) {
                if (aRow == number) {
                    counter++;
                }
            }
            results.setData(i, counter);
        }
        System.out.printf("%s: Lines processed.\n", Thread.currentThread().getName());
        try {
            barrier.await();
        } catch (InterruptedException | BrokenBarrierException e) {
            e.printStackTrace();
        }
    }

}

/**
 * 最终的合并线程
 */
class Grouper implements Runnable {

    private final Results results;

    public Grouper(Results results) {
        this.results = results;
    }

    @Override
    public void run() {
        int finalResult = 0;
        System.out.printf("Grouper: Processing results...\n");
        int data[] = results.getData();
        for (int number : data) {
            finalResult += number;
        }
        System.out.printf("Grouper: Total result: %d.\n", finalResult);
    }

}