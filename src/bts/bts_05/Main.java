package bts.bts_05;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * 使用读写锁实现同步数据访问
 *
 */
public class Main {

    private static final int WRITER_NUM = 2;
    private static final int READER_NUM = 10;

    public static void main(String[] args) throws InterruptedException {
        PricesInfo pricesInfo = new PricesInfo();
        ExecutorService es = Executors.newFixedThreadPool(WRITER_NUM + READER_NUM);
        CountDownLatch latch = new CountDownLatch(WRITER_NUM + READER_NUM);

        long start = System.currentTimeMillis();

        for (int i = 0; i < READER_NUM; i++) {
            es.submit(() -> {
                for (int j = 0; j <= WRITER_NUM / 2; j++) {
                    // 每次取值花费 1 S
                    System.out.printf("%s: Price 1: %f\n", Thread.currentThread().getName(), pricesInfo.getPrice1());
                    System.out.printf("%s: Price 2: %f\n", Thread.currentThread().getName(), pricesInfo.getPrice2());
                }
                latch.countDown();
            });
        }

        for (int i = 0; i < WRITER_NUM; i++) {
            es.submit(() -> {
                // 每次设置花费 1 S
                pricesInfo.setPrices(Math.random() * 10, Math.random() * 15);
                latch.countDown();
            });
            TimeUnit.SECONDS.sleep(2);
        }

        es.shutdown();
        latch.await();
        System.out.println("total cost: " + (System.currentTimeMillis() - start) + " ms");
    }

}

class PricesInfo {

    private double price1;
    private double price2;

    private ReadWriteLock lock;

    public PricesInfo() {
        price1 = 1.0;
        price2 = 2.0;
        lock = new ReentrantReadWriteLock();
    }

    public double getPrice1() {
        lock.readLock().lock();
        sleep(1);
        double value = price1;
        lock.readLock().unlock();
        return value;
    }

    public double getPrice2() {
        lock.readLock().lock();
        sleep(1);
        double value = price2;
        lock.readLock().unlock();
        return value;
    }

    public void setPrices(double price1, double price2) {
        lock.writeLock().lock();
        System.out.println(Thread.currentThread().getName() + " Write Lock Acquired.");
        sleep(1);
        this.price1 = price1;
        this.price2 = price2;
        System.out.printf("price1 & price2 is set to %f & %f\n", price1, price2);
        System.out.println(Thread.currentThread().getName() + " Write Lock Released.");
        lock.writeLock().unlock();
    }

    private void sleep(int seconds) {
        try {
            TimeUnit.SECONDS.sleep(seconds);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

}




