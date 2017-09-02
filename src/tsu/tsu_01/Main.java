package tsu.tsu_01;

import java.util.Date;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * 使用 Semaphore 控制并发的访问一个或多个资源
 */
public class Main {

    public static void main(String args[]) {

        PrintQueue printQueue = new PrintQueue();

        Thread[] threads = new Thread[12];
        for (int i = 0; i < threads.length; i++) {
            threads[i] = new Thread(new Job(printQueue), "Thread " + i);
        }

        for (int i = 0; i < threads.length; i++) {
            threads[i].start();
        }
    }

}

class Job implements Runnable {

    private PrintQueue printQueue;

    public Job(PrintQueue printQueue) {
        this.printQueue = printQueue;
    }

    /**
     * 发送文档到打印队列直到打印完成
     */
    @Override
    public void run() {
        System.out.printf("%s: Going to print a job\n", Thread.currentThread().getName());
        printQueue.printJob(new Object());
        System.out.printf("%s: The document has been printed\n", Thread.currentThread().getName());
    }
}

/**
 * 打印队列
 */
class PrintQueue {

    private static final int PRINTER_NUM = 3;

    // Semaphore 用来控制并发访问打印机的线程数量
    private final Semaphore semaphore;

    // 表征哪台打印机可用
    private final boolean freePrinters[];

    // 控制线程对 freePrinters 的并发访问
    private final Lock lockPrinters;

    public PrintQueue() {
        semaphore = new Semaphore(PRINTER_NUM);
        freePrinters = new boolean[PRINTER_NUM];
        for (int i = 0; i < PRINTER_NUM; i++) {
            freePrinters[i] = true;
        }
        lockPrinters = new ReentrantLock();
    }

    public void printJob(Object document) {
        try {
            // 保证最多有三个线程可以去访问打印机
            semaphore.acquire();

            // 获取打印机
            int assignedPrinter = getPrinter();

            Long duration = (long) (Math.random() * 10);
            System.out.printf("%s - %s: PrintQueue: Printing a Job in Printer %d during %d seconds\n", new Date(), Thread.currentThread().getName(), assignedPrinter, duration);
            TimeUnit.SECONDS.sleep(duration);

            // 打印机使用完成
            freePrinters[assignedPrinter] = true;
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            // 释放 semaphore
            semaphore.release();
        }
    }

    /**
     * 获取一台可用的打印机，不可用则返回 -1
     */
    private int getPrinter() {
        int ret = -1;

        try {
            lockPrinters.lock();

            // 找第一个可用的打印机
            for (int i = 0; i < freePrinters.length; i++) {
                if (freePrinters[i]) {
                    ret = i;
                    freePrinters[i] = false;
                    break;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            lockPrinters.unlock();
        }
        return ret;
    }

}
