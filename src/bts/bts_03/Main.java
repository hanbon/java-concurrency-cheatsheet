package bts.bts_03;

import java.util.Random;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * 使用重入锁实现同步
 */
public class Main {


    public static void main(String args[]) throws InterruptedException {

        System.out.printf("Running example with fair-mode = false\n\n");
        testPrintQueue(false);
        System.out.printf("\n\nRunning example with fair-mode = true\n\n");
        testPrintQueue(true);
    }

    private static void testPrintQueue(boolean fairMode) throws InterruptedException {
        // 创建打印机
        Printer printer = new Printer(fairMode);

        Thread thread[] = new Thread[10];
        for (int i = 0; i < 10; i++) {
            thread[i] = new Thread(new Job(printer), "Person " + i);
        }

        for (int i = 0; i < 10; i++) {
            thread[i].start();
            TimeUnit.MILLISECONDS.sleep(100);
        }

        TimeUnit.SECONDS.sleep(15);
    }

}

class Printer {

    private Lock queueLock;

    public Printer(boolean fairMode) {
        queueLock = new ReentrantLock(fairMode);
    }

    /**
     * 打印分为两步，以便观察公平模式和非公平模式的区别
     */
    public void printJob(Object document) {
        queueLock.lock();

        try {
            int duration = new Random().nextInt(3);
            System.out.printf("%s: Printing step 1 take %d seconds\n", Thread.currentThread().getName(), duration);
            TimeUnit.SECONDS.sleep(duration);
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            queueLock.unlock();
        }

        queueLock.lock();
        try {
            int duration = new Random().nextInt(3);
            System.out.printf("%s: Printing step 2 take %d seconds\n", Thread.currentThread().getName(), duration);
            TimeUnit.SECONDS.sleep(duration);
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            queueLock.unlock();
        }
    }
}

class Job implements Runnable {

    private Printer printer;

    public Job(Printer printer) {
        this.printer = printer;
    }

    /**
     * 提交文档给打印机打印
     */
    @Override
    public void run() {
        System.out.printf("%s: Going to print a document\n", Thread.currentThread().getName());
        printer.printJob(new Object());
        System.out.printf("%s: The document has been printed\n", Thread.currentThread().getName());
    }
}


