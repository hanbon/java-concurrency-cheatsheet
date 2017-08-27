package bts.bts_01;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * 测试对象锁和类锁
 * 验证静态同步方法和实例同步方法访问了相同变量可能会带来数据不一致的错误
 *
 */
public class SyncMethods {

    public static void main(String[] args) throws InterruptedException {
        TestSync testSync = new TestSync();

        ExecutorService es = Executors.newFixedThreadPool(200);
        int times = 1000000;

        for (int i = 0; i < times; i++) {
            es.submit(testSync::add);
            es.submit(TestSync::addStatic);
        }

        TimeUnit.SECONDS.sleep(1);
        es.shutdownNow();

        System.out.println("should be: " + times * 2 + ", actual is: " + testSync.getI());
    }

}

class TestSync extends Thread {

    // 共享变量
    private static int i = 0;

    /**
     * 对象锁
     */
    public synchronized void add() {
        i++;
    }

    /**
     * 类锁
     */
    public static synchronized void addStatic() {
        i++;
    }

    public synchronized int getI() {
        return i;
    }

}
