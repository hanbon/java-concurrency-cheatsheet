package bts.bts_01;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 测试对象锁和类锁
 * 验证不用获取锁也能访问非 synchronized 方法，并且和 synchronized 方法混用会带来数据不一致的错误
 *
 */
public class SyncMethods {

    public static void main(String[] args) throws InterruptedException {
        TestSync testSync = new TestSync();

        ExecutorService es = Executors.newFixedThreadPool(200);
        int times = 1000;

        for (int i = 0; i < times; i++) {
            es.submit(testSync::add);
            es.submit(TestSync::addTen);
            es.submit(testSync::minusTen);
        }

        TimeUnit.SECONDS.sleep(11);
        es.shutdownNow();

        System.out.println("should be: " + times + ", actual is: " + testSync.getI());
    }

}

class TestSync extends Thread {

    // 共享变量
    private static int i = 0;
    // 为了方便观察执行顺序
    private static AtomicInteger order = new AtomicInteger(0);

    /**
     * 对象锁
     */
    public synchronized void add() {
        i++;
        System.out.println(order.incrementAndGet() + " " + Thread.currentThread().getName() + " get add monitor");
        try {
            TimeUnit.MILLISECONDS.sleep(10);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println(order.incrementAndGet() + " " + Thread.currentThread().getName() + " release add monitor");
    }

    /**
     * 类锁
     */
    public static synchronized void addTen() {
        i += 10;
        System.out.println(order.incrementAndGet() + " " + Thread.currentThread().getName() + " get addTen monitor");
        try {
            TimeUnit.MILLISECONDS.sleep(10);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println(order.incrementAndGet() + " " + Thread.currentThread().getName() + " release addTen monitor");
    }

    public void minusTen() {
        i -= 10;
        System.out.println(order.incrementAndGet() + " " + Thread.currentThread().getName() + " minus ten");
    }


    public synchronized int getI() {
        return i;
    }

}
